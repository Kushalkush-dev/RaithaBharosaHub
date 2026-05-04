package com.raithabharosa.hub.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "crop_history")
data class CropHistoryEntity(
    @PrimaryKey(autoGenerate = true)
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