package com.raithabharosa.hub.domain.model

data class CropHistory(
    val id: Long = 0,
    val farmerId: Long,
    val cropType: String,
    val sowingDate: Long,
    val harvestDate: Long? = null,
    val yield: Float = 0f,
    val notes: String = "",
    val season: String,
    val year: Int
)