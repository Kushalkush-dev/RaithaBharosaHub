package com.raithabharosa.hub.domain.model

data class Farmer(
    val id: Long = 0,
    val name: String,
    val cropType: CropType,
    val plotSize: Float = 0f,
    val location: String = ""
)

enum class CropType(val displayName: String, val kannadaName: String) {
    SUGARCANE("Sugarcane", "Kabbu"),
    RAGI("Ragi", "Ragi"),
    PADDY("Paddy", "Batti"),
    WHEAT("Wheat", "Gori"),
    MAIZE("Maize", "Makki")
}