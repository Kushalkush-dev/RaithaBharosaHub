package com.raithabharosa.hub.domain.model

data class TrendDataPoint(
    val timestamp: Long,
    val label: String,
    val value: Float,
    val cropType: String = ""
)

data class SoilTrendData(
    val moistureData: List<TrendDataPoint>,
    val nitrogenData: List<TrendDataPoint>,
    val phosphorusData: List<TrendDataPoint>,
    val potassiumData: List<TrendDataPoint>,
    val phData: List<TrendDataPoint>,
    val temperatureData: List<TrendDataPoint>
)

data class WeatherTrendData(
    val temperatureData: List<TrendDataPoint>,
    val humidityData: List<TrendDataPoint>
)

data class YieldTrendData(
    val yieldData: List<TrendDataPoint>
) {
    fun getCrops(): List<String> = yieldData.map { it.cropType }.distinct()
}

enum class TrendPeriod(val label: String, val days: Int) {
    LAST_30_DAYS("Last 30 Days", 30),
    LAST_6_MONTHS("Last 6 Months", 180),
    LAST_12_MONTHS("Last 12 Months", 365)
}

enum class SoilMetric(val label: String) {
    MOISTURE("Moisture %"),
    NITROGEN("Nitrogen"),
    PHOSPHORUS("Phosphorus"),
    POTASSIUM("Potassium"),
    PH("pH"),
    TEMPERATURE("Temperature")
}
