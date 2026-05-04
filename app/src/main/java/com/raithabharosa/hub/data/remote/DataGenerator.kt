package com.raithabharosa.hub.data.remote

import kotlin.random.Random

object DataGenerator {
    fun generateMoistureLevel(): Float {
        return Random.nextFloat() * 30f + 10f
    }

    fun generateTemperature(): Float {
        return Random.nextFloat() * 15f + 20f
    }

    fun generateNPK(): Triple<Float, Float, Float> {
        return Triple(
            Random.nextFloat() * 100f,
            Random.nextFloat() * 60f,
            Random.nextFloat() * 40f
        )
    }

    fun simulateWeatherCondition(): String {
        val conditions = listOf("Clear", "Clouds", "Rain", "Storm", " drizzle")
        return conditions[Random.nextInt(conditions.size)]
    }

    fun shouldWarnOfStorm(): Boolean {
        return Random.nextFloat() > 0.7f
    }

    fun daysUntilStorm(): Int {
        return if (shouldWarnOfStorm()) Random.nextInt(1, 4) else -1
    }
}