package com.raithabharosa.hub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.raithabharosa.hub.data.local.entity.FarmerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmerDao {
    @Query("SELECT * FROM farmers ORDER BY createdAt DESC LIMIT 1")
    fun getCurrentFarmer(): Flow<FarmerEntity?>

    @Query("SELECT * FROM farmers ORDER BY createdAt DESC LIMIT 1")
    suspend fun getCurrentFarmerSync(): FarmerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFarmer(farmer: FarmerEntity): Long

    @Update
    suspend fun updateFarmer(farmer: FarmerEntity)

    @Query("DELETE FROM farmers")
    suspend fun deleteAll()
}