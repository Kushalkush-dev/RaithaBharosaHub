package com.raithabharosa.hub.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "farmers")
data class FarmerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val cropType: String,
    val plotSize: Float = 0f,
    val location: String = "",
    val createdAt: Long = System.currentTimeMillis()
)