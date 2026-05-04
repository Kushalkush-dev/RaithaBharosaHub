package com.raithabharosa.hub.domain.usecase

import com.raithabharosa.hub.domain.model.ActionPlan
import com.raithabharosa.hub.domain.model.DailyForecast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GenerateActionPlanUseCase {
    operator fun invoke(
        dailyForecasts: List<DailyForecast>
    ): List<ActionPlan> {
        if (dailyForecasts.isEmpty()) {
            return generateDefaultPlans(7)
        }
        
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
            val isCloudy = condition.contains("Cloud", ignoreCase = true)
            val isHumid = humidity > 80
            val isHot = temp > 35
            val isCold = temp < 15
            
            val (action, reason, isUrgent) = when {
                isStormy && isToday -> Triple("Secure crops & equipment", "Storm warning!", true)
                isRainy && precipChance > 50 && isToday -> Triple("Avoid field work", "$precipChance% rain expected", true)
                isHot -> Triple("Provide irrigation", "High: ${temp.toInt()}°C", false)
                isCold -> Triple("Protect crops from cold", "Low: ${temp.toInt()}°C", false)
                isHumid -> Triple("Check for fungal diseases", "Humidity: $humidity%", false)
                isToday -> Triple("Check soil moisture", "Today: ${forecast.tempMin.toInt()}-${temp.toInt()}°C", false)
                isRainy -> Triple("Delay field work", "$precipChance% rain likely", false)
                isCloudy && precipChance < 20 -> Triple("Good for transplanting", "Cloudy, low rain chance", false)
                else -> getOptimalAction(i)
            }

            plans.add(
                ActionPlan(
                    day = dayName,
                    action = action,
                    isUrgent = isUrgent,
                    reason = reason
                )
            )
        }

        return plans
    }

    private fun generateDefaultPlans(days: Int): List<ActionPlan> {
        val plans = mutableListOf<ActionPlan>()
        val dateFormat = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())

        for (i in 0 until days) {
            val date = Date(System.currentTimeMillis() + i * 24 * 60 * 60 * 1000L)
            plans.add(
                ActionPlan(
                    day = dateFormat.format(date),
                    action = getOptimalAction(i).first,
                    isUrgent = false,
                    reason = getOptimalAction(i).second
                )
            )
        }
        return plans
    }

    private fun getOptimalAction(dayOffset: Int): Triple<String, String, Boolean> {
        return when (dayOffset % 7) {
            0 -> Triple("Check soil moisture", "Regular assessment", false)
            1 -> Triple("Prepare seeds/seedlings", "Get materials ready", false)
            2 -> Triple("Apply base fertilizer", "Before sowing", false)
            3 -> Triple("Land preparation", "Plowing & leveling", false)
            4 -> Triple("Sowing/Planting", "Optimal conditions", false)
            5 -> Triple("First irrigation", "After sowing", false)
            6 -> Triple("Monitor germination", "Check crop emergence", false)
            else -> Triple("Rest day", "Assess weekly progress", false)
        }
    }
}