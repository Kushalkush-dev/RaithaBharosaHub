package com.raithabharosa.hub.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.raithabharosa.hub.data.local.entity.CropProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CropProfileDao {
    @Query("SELECT * FROM crop_profiles WHERE farmerId = :farmerId ORDER BY createdAt DESC")
    fun getCropProfiles(farmerId: Long): Flow<List<CropProfileEntity>>
    
    @Query("SELECT * FROM crop_profiles WHERE farmerId = :farmerId AND isActive = 1 LIMIT 1")
    fun getActiveProfile(farmerId: Long): Flow<CropProfileEntity?>
    
    @Query("SELECT * FROM crop_profiles WHERE id = :id")
    suspend fun getCropProfileById(id: Long): CropProfileEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCropProfile(profile: CropProfileEntity): Long
    
    @Update
    suspend fun updateCropProfile(profile: CropProfileEntity)
    
    @Delete
    suspend fun deleteCropProfile(profile: CropProfileEntity)
    
    @Query("DELETE FROM crop_profiles WHERE id = :id")
    suspend fun deleteCropProfileById(id: Long)
    
    @Query("UPDATE crop_profiles SET isActive = 0 WHERE farmerId = :farmerId")
    suspend fun deactivateAllProfiles(farmerId: Long)
    
    @Query("UPDATE crop_profiles SET isActive = 1 WHERE id = :id")
    suspend fun activateProfile(id: Long)
}