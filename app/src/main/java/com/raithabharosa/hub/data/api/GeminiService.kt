package com.raithabharosa.hub.data.api

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.raithabharosa.hub.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "GeminiService"

class GeminiService {
    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash-lite",
        apiKey = BuildConfig.GEMINI_API_KEY,
        generationConfig = generationConfig {
            temperature = 0.7f
            topK = 40
            topP = 0.95f
            maxOutputTokens = 1024
        }
    )

    private var chatSession = generativeModel.startChat()

    suspend fun generateActionPlan(
        cropInfo: String,
        weatherForecast: String,
        language: String = "English"
    ): String? = withContext(Dispatchers.IO) {
        try {
            if (BuildConfig.GEMINI_API_KEY.isBlank() || BuildConfig.GEMINI_API_KEY == "YOUR_API_KEY_HERE") {
                Log.e(TAG, "API Key is missing or invalid!")
                return@withContext null
            }
            
            val prompt = """
                You are an expert agricultural advisor in Karnataka, India. 
                Based on the following information, provide a concise daily action plan for the next 7 days.
                
                Crop Details: $cropInfo
                Weather Forecast: $weatherForecast
                
                Guidelines:
                1. Provide exactly 7 points, one for each day.
                2. Each point should have a short "Action" and a "Reason".
                3. Keep the advice practical for a small-scale farmer.
                4. Respond in $language.
                
                Format:
                Day 1: Action | Reason
                Day 2: Action | Reason
                ...and so on.
            """.trimIndent()

            val response = generativeModel.generateContent(prompt)
            response.text
        } catch (e: Exception) {
            Log.e(TAG, "Error generating action plan: ${e.message}", e)
            null
        }
    }

    suspend fun getChatResponse(
        userInput: String,
        contextInfo: String,
        language: String = "English"
    ): String = withContext(Dispatchers.IO) {
        try {
            if (BuildConfig.GEMINI_API_KEY.isBlank() || BuildConfig.GEMINI_API_KEY == "YOUR_API_KEY_HERE") {
                return@withContext "API Key is missing or invalid. Please check your local.properties file."
            }

            val systemContext = """
                You are 'Raitha Sahaya', an intelligent agricultural assistant for farmers in Karnataka.
                Current Farmer Context: $contextInfo
                Language Preference: $language
                
                Guidelines:
                1. Provide helpful, polite, and practical agricultural advice.
                2. Use the provided context (crop type, location, etc.) to tailor your answers.
                3. Respond in $language.
            """.trimIndent()

            // Using direct generateContent for better reliability during troubleshooting
            val prompt = "$systemContext\n\nUser Question: $userInput"
            val response = generativeModel.generateContent(prompt)
            
            response.text ?: "The AI returned an empty response."
        } catch (e: Exception) {
            Log.e(TAG, "Chat Error: ${e.message}", e)
            "Connection Error: ${e.localizedMessage ?: "Unknown error"}. Check your internet connection."
        }
    }

    fun resetChat() {
        chatSession = generativeModel.startChat()
    }
}
