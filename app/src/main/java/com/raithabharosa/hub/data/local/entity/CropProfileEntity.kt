package com.raithabharosa.hub.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "crop_profiles")
data class CropProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val farmerId: Long,
    val name: String,
    val cropType: String,
    val plotSize: Float,
    val location: String,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)