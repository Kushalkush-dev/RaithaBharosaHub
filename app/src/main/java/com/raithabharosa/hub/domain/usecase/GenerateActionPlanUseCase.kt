package com.raithabharosa.hub.domain.usecase

import com.raithabharosa.hub.data.api.GeminiService
import com.raithabharosa.hub.domain.model.ActionPlan
import com.raithabharosa.hub.domain.model.DailyForecast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GenerateActionPlanUseCase(
    private val geminiService: GeminiService = GeminiService()
) {
    suspend operator fun invoke(
        cropInfo: String,
        dailyForecasts: List<DailyForecast>,
        isKannada: Boolean = false
    ): List<ActionPlan> {
        if (dailyForecasts.isEmpty()) {
            return generateDefaultPlans(7)
        }

        val weatherSummary = dailyForecasts.take(7).joinToString("\n") { 
            "${it.day}: ${it.condition}, Temp: ${it.tempMin}-${it.tempMax}°C" 
        }

        val aiResponse = geminiService.generateActionPlan(
            cropInfo = cropInfo,
            weatherForecast = weatherSummary,
            language = if (isKannada) "Kannada" else "English"
        )

        return if (aiResponse != null) {
            parseAiResponse(aiResponse, dailyForecasts)
        } else {
            generateRuleBasedPlans(dailyForecasts)
        }
    }

    private fun parseAiResponse(response: String, forecasts: List<DailyForecast>): List<ActionPlan> {
        val plans = mutableListOf<ActionPlan>()
        val lines = response.lines().filter { it.contains("|") }
        val dateFormat = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())

        for (i in forecasts.indices) {
            if (i >= lines.size) break
            
            val line = lines[i]
            val content = if (line.contains(":")) line.substringAfter(":").trim() else line.trim()
            val action = content.substringBefore("|").trim()
            val reason = content.substringAfter("|").trim()

            val date = try {
                val dayFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                dayFormat.parse(forecasts[i].day)
            } catch (e: Exception) {
                Date(System.currentTimeMillis() + i * 24 * 60 * 60 * 1000L)
            }
            
            plans.add(
                ActionPlan(
                    day = dateFormat.format(date ?: Date()),
                    action = action,
                    reason = reason,
                    isUrgent = action.contains("Avoid", ignoreCase = true) || action.contains("Warning", ignoreCase = true)
                )
            )
        }
        
        return if (plans.isEmpty()) generateRuleBasedPlans(forecasts) else plans
    }

    private fun generateRuleBasedPlans(dailyForecasts: List<DailyForecast>): List<ActionPlan> {
        val plans = mutableListOf<ActionPlan>()
        val dateFormat = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())
        val dayFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        for (i in dailyForecasts.indices) {
            val forecast = dailyForecasts[i]
            val date = try {
                dayFormat.parse(forecast.day)
            } catch (e: Exception) {
                Date(System.currentTimeMillis() + i * 24 * 60 * 60 * 1000L)
            }
            val dayName = dateFormat.format(date ?: Date())
            val isToday = i == 0
            
            val temp = forecast.tempMax
            val humidity = forecast.humidity
            val condition = forecast.condition
            val precipChance = forecast.precipitationChance
            
            val isRainy = condition.contains("Rain", ignoreCase = true) || 
                         condition.contains("Drizzle", ignoreCase = true)
            val isStormy = condition.contains("Storm", ignoreCase = true)
            val isHumid = humidity > 80
            val isHot = temp > 35
            
            val (action, reason, isUrgent) = when {
                isStormy && isToday -> Triple("Secure crops", "Storm warning!", true)
                isRainy && precipChance > 50 && isToday -> Triple("Avoid field work", "High rain chance", true)
                isHot -> Triple("Irrigate", "High temperature: ${temp.toInt()}°C", false)
                isHumid -> Triple("Check for fungus", "High humidity: $humidity%", false)
                else -> getOptimalAction(i)
            }

            plans.add(ActionPlan(dayName, action, isUrgent, reason))
        }
        return if (plans.isEmpty()) generateDefaultPlans(7) else plans
    }

    private fun generateDefaultPlans(days: Int): List<ActionPlan> {
        val plans = mutableListOf<ActionPlan>()
        val dateFormat = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())

        for (i in 0 until days) {
            val date = Date(System.currentTimeMillis() + i * 24 * 60 * 60 * 1000L)
            val actionData = getOptimalAction(i)
            plans.add(ActionPlan(dateFormat.format(date), actionData.first, actionData.third, actionData.second))
        }
        return plans
    }

    private fun getOptimalAction(dayOffset: Int): Triple<String, String, Boolean> {
        return when (dayOffset % 7) {
            0 -> Triple("Check soil moisture", "Regular assessment", false)
            1 -> Triple("Prepare seeds", "Get materials ready", false)
            2 -> Triple("Apply fertilizer", "Before sowing", false)
            3 -> Triple("Land preparation", "Plowing", false)
            4 -> Triple("Sowing/Planting", "Optimal conditions", false)
            5 -> Triple("First irrigation", "After sowing", false)
            6 -> Triple("Monitor germination", "Check emergence", false)
            else -> Triple("Rest day", "Assess progress", false)
        }
    }
}
