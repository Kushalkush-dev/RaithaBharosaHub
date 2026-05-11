package com.raithabharosa.hub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.raithabharosa.hub.data.local.entity.SoilDataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SoilDataDao {
    @Query("SELECT * FROM soil_data WHERE farmerId = :farmerId ORDER BY recordedAt DESC LIMIT 1")
    fun getLatestSoilData(farmerId: Long): Flow<SoilDataEntity?>

    @Query("SELECT * FROM soil_data WHERE farmerId = :farmerId ORDER BY recordedAt DESC")
    fun getSoilDataHistory(farmerId: Long): Flow<List<SoilDataEntity>>

    @Query("SELECT * FROM soil_data WHERE farmerId = :farmerId AND recordedAt >= :startTime ORDER BY recordedAt ASC")
    fun getSoilDataInRange(farmerId: Long, startTime: Long): Flow<List<SoilDataEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSoilData(soilData: SoilDataEntity): Long

    @Query("DELETE FROM soil_data WHERE farmerId = :farmerId")
    suspend fun deleteAllForFarmer(farmerId: Long)
}