package com.raithabharosa.hub.domain.model

data class CropProfile(
    val id: Long = 0,
    val farmerId: Long,
    val name: String,
    val cropType: CropType,
    val plotSize: Float,
    val location: String,
    val isActive: Boolean = true
)