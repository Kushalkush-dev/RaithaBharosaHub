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
        modelName = "gemini-3.1-flash-lite",
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
                You are an expert agricultural advisor 
                Based on the following information, provide a concise daily action plan for the next 7 days.
                
                Crop Details: $cropInfo
                Weather Forecast: $weatherForecast
                
                Guidelines:
                1. Provide exactly 7 points, one for each day.
                2. Each point should have a short "Action" and a "Reason".
                3. Keep the advice practical for a small-scale farmer.
                4. Respond in $language.
                
                Format:
                Day 1: Action | Reason | temperature
                Day 2: Action | Reason | temperature
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
               You are "Raitha Sahaya", an AI agricultural assistant for farmers.

Farmer Context:
$contextInfo

Response Language:
$language

Instructions:
- Reply only in $language.
- Give practical, accurate, and farmer-friendly agricultural advice.
- Use the farmer context when relevant.
- Keep answers concise, clear, and directly actionable.
- Avoid long explanations unless the farmer explicitly asks for details.
- Prefer step-by-step guidance for farming tasks.
- Mention quantities, timings, and precautions when useful.
- If information is uncertain, say so briefly instead of guessing.
- Do not repeat the farmer’s question.
- Avoid unnecessary introductions, summaries, or generic farming theory.
- Focus on solving the immediate problem first.
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
