package com.raithabharosa.hub.domain.usecase

import com.raithabharosa.hub.domain.model.CropType
import com.raithabharosa.hub.domain.model.SoilData
import com.raithabharosa.hub.domain.model.SowingIndex
import com.raithabharosa.hub.domain.model.SowingStatus
import com.raithabharosa.hub.domain.model.WeatherData

class CalculateSowingIndexUseCase {
    operator fun invoke(
        soilData: SoilData?,
        cropType: CropType,
        useWeatherData: WeatherData? = null
    ): SowingIndex {
        val hasSoilData = soilData != null && 
            (soilData.moisture > 0 || soilData.nitrogen > 0 || soilData.phosphorus > 0)
        
        val moisture = if (hasSoilData) soilData!!.moisture else 25f
        val nitrogen = if (hasSoilData) soilData!!.nitrogen else 60f
        val phosphorus = if (hasSoilData) soilData!!.phosphorus else 30f
        val potassium = if (hasSoilData) soilData!!.potassium else 25f
        
        val temperature = useWeatherData?.temperature ?: 28f
        
        val reasons = mutableListOf<String>()
        var score = 100

        val (optimalMoistureMin, optimalMoistureMax) = getOptimalMoistureRange(cropType)
        when {
            moisture > optimalMoistureMax -> {
                score -= 40
                reasons.add("Soil moisture too high (${moisture.toInt()}%)")
            }
            moisture < optimalMoistureMin -> {
                score -= 30
                reasons.add("Soil moisture too low (${moisture.toInt()}%)")
            }
            else -> reasons.add("Moisture is optimal")
        }

        val (optimalTempMin, optimalTempMax) = getOptimalTemperatureRange(cropType)
        when {
            temperature > optimalTempMax -> {
                score -= 25
                reasons.add("Temperature too high (${temperature.toInt()}°C)")
            }
            temperature < optimalTempMin -> {
                score -= 20
                reasons.add("Temperature too low (${temperature.toInt()}°C)")
            }
            else -> reasons.add("Temperature is optimal")
        }

        val weatherCondition = useWeatherData?.condition ?: "Clear"
        when {
            weatherCondition.contains("Rain", ignoreCase = true) || 
            weatherCondition.contains("Storm", ignoreCase = true) -> {
                score -= 30
                reasons.add("Bad weather: $weatherCondition")
            }
            weatherCondition.contains("Cloud", ignoreCase = true) -> {
                score -= 10
                reasons.add("Cloudy conditions")
            }
            else -> reasons.add("Weather is clear")
        }

        if (nitrogen < 40) {
            score -= 15
            reasons.add("Nitrogen deficient")
        } else {
            reasons.add("Nitrogen adequate")
        }

        if (phosphorus < 20) {
            score -= 15
            reasons.add("Phosphorus deficient")
        } else {
            reasons.add("Phosphorus adequate")
        }

        if (potassium < 15) {
            score -= 10
            reasons.add("Potassium deficient")
        } else {
            reasons.add("Potassium adequate")
        }

        score = score.coerceIn(0, 100)

        val status = when {
            score >= 85 -> SowingStatus.OPTIMAL
            score >= 70 -> SowingStatus.GOOD
            score >= 50 -> SowingStatus.FAIR
            score >= 30 -> SowingStatus.WAIT
            else -> SowingStatus.NOT_ADVISED
        }

        val recommendation = when (status) {
            SowingStatus.OPTIMAL -> "Perfect conditions! Start sowing now."
            SowingStatus.GOOD -> "Good conditions. Proceed with sowing."
            SowingStatus.FAIR -> "Fair conditions. Consider waiting."
            SowingStatus.WAIT -> "Conditions not ideal. Wait for improvement."
            SowingStatus.NOT_ADVISED -> "NOT advised to sow. Wait for better conditions."
        }

        return SowingIndex(
            score = score,
            status = status,
            recommendation = recommendation,
            reasons = reasons
        )
    }

    private fun getOptimalMoistureRange(cropType: CropType): Pair<Float, Float> {
        return when (cropType) {
            CropType.SUGARCANE -> Pair(25f, 35f)
            CropType.RAGI -> Pair(18f, 28f)
            CropType.PADDY -> Pair(30f, 40f)
            CropType.WHEAT -> Pair(20f, 30f)
            CropType.MAIZE -> Pair(20f, 30f)
        }
    }

    private fun getOptimalTemperatureRange(cropType: CropType): Pair<Float, Float> {
        return when (cropType) {
            CropType.SUGARCANE -> Pair(25f, 35f)
            CropType.RAGI -> Pair(20f, 30f)
            CropType.PADDY -> Pair(25f, 35f)
            CropType.WHEAT -> Pair(15f, 25f)
            CropType.MAIZE -> Pair(20f, 30f)
        }
    }
}