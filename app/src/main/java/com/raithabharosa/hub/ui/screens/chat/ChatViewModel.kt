package com.raithabharosa.hub.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raithabharosa.hub.AppConfig
import com.raithabharosa.hub.data.api.GeminiService
import com.raithabharosa.hub.data.repository.FarmerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class ChatState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val selectedLanguage: String? = null,
    val error: String? = null
)

class ChatViewModel(
    private val repository: FarmerRepository,
    private val geminiService: GeminiService
) : ViewModel() {

    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state.asStateFlow()

    fun selectLanguage(language: String) {
        val welcomeMessage = if (language == "Kannada") {
            "ನಮಸ್ಕಾರ! ನಾನು ರೈತ ಸಹಾಯ. ಇಂದು ನಿಮ್ಮ ಕೃಷಿಯಲ್ಲಿ ನಾನು ನಿಮಗೆ ಹೇಗೆ ಸಹಾಯ ಮಾಡಬಹುದು?"
        } else {
            "Namaste! I am Raitha Sahaya. How can I help you with your farming today?"
        }
        
        _state.value = _state.value.copy(
            selectedLanguage = language,
            messages = listOf(ChatMessage(welcomeMessage, false))
        )
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        val currentLanguage = _state.value.selectedLanguage ?: "English"

        val userMessage = ChatMessage(text, true)
        _state.value = _state.value.copy(
            messages = _state.value.messages + userMessage,
            isLoading = true
        )

        viewModelScope.launch {
            try {
                val farmer = repository.getCurrentFarmerSync()
                val location = farmer?.location ?: AppConfig.DEFAULT_LOCATION
                val forecastResult = repository.getForecastData(location, AppConfig.WEATHER_API_KEY)
                val dailyForecasts = forecastResult.getOrNull() ?: emptyList()
                
                val weatherSummary = dailyForecasts.take(3).joinToString("; ") { 
                    "${it.day}: ${it.condition}, ${it.tempMin}-${it.tempMax}°C" 
                }

                val contextInfo = """
                    Farmer: ${farmer?.name ?: "Unknown"}
                    Location: $location
                    Crop Type: ${farmer?.cropType?.displayName ?: "General"}
                    Weather (Next 3 days): $weatherSummary
                """.trimIndent()

                val response = geminiService.getChatResponse(
                    userInput = text,
                    contextInfo = contextInfo,
                    language = currentLanguage
                )

                val aiMessage = ChatMessage(
                    text = response,
                    isUser = false
                )

                _state.value = _state.value.copy(
                    messages = _state.value.messages + aiMessage,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Error: ${e.localizedMessage}"
                )
                val errorMsg = ChatMessage(
                    text = "System Error: ${e.localizedMessage}",
                    isUser = false
                )
                _state.value = _state.value.copy(messages = _state.value.messages + errorMsg)
            }
        }
    }

    fun clearChat() {
        geminiService.resetChat()
        _state.value = ChatState(selectedLanguage = null)
    }
}
