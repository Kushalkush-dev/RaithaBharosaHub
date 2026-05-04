package com.raithabharosa.hub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.raithabharosa.hub.data.local.entity.CropHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CropHistoryDao {
    @Query("SELECT * FROM crop_history WHERE farmerId = :farmerId ORDER BY sowingDate DESC")
    fun getCropHistory(farmerId: Long): Flow<List<CropHistoryEntity>>

    @Query("SELECT * FROM crop_history WHERE farmerId = :farmerId AND season = :season ORDER BY sowingDate DESC")
    fun getCropHistoryBySeason(farmerId: Long, season: String): Flow<List<CropHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCropHistory(history: CropHistoryEntity): Long

    @Query("DELETE FROM crop_history WHERE farmerId = :farmerId")
    suspend fun deleteAllForFarmer(farmerId: Long)
}