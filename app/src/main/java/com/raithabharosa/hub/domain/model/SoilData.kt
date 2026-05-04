package com.raithabharosa.hub.domain.model

data class SoilData(
    val id: Long = 0,
    val farmerId: Long,
    val moisture: Float,
    val nitrogen: Float,
    val phosphorus: Float,
    val potassium: Float,
    val ph: Float = 7f,
    val temperature: Float = 25f,
    val recordedAt: Long = System.currentTimeMillis()
)

data class SowingIndex(
    val score: Int,
    val status: SowingStatus,
    val recommendation: String,
    val reasons: List<String>
)

enum class SowingStatus {
    OPTIMAL,
    GOOD,
    FAIR,
    WAIT,
    NOT_ADVISED
}

data class WeatherData(
    val temperature: Float,
    val humidity: Int,
    val condition: String,
    val windSpeed: Float,
    val daysUntilStorm: Int = -1
)

data class DailyForecast(
    val day: String,
    val tempMin: Float,
    val tempMax: Float,
    val humidity: Int,
    val condition: String,
    val precipitationChance: Int
)

data class ActionPlan(
    val day: String,
    val action: String,
    val isUrgent: Boolean = false,
    val reason: String = ""
)