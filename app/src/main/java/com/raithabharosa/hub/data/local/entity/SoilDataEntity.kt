package com.raithabharosa.hub.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "soil_data")
data class SoilDataEntity(
    @PrimaryKey(autoGenerate = true)
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