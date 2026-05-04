package com.raithabharosa.hub.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.raithabharosa.hub.data.local.dao.CropHistoryDao
import com.raithabharosa.hub.data.local.dao.CropProfileDao
import com.raithabharosa.hub.data.local.dao.FarmerDao
import com.raithabharosa.hub.data.local.dao.SoilDataDao
import com.raithabharosa.hub.data.local.entity.CropHistoryEntity
import com.raithabharosa.hub.data.local.entity.CropProfileEntity
import com.raithabharosa.hub.data.local.entity.FarmerEntity
import com.raithabharosa.hub.data.local.entity.SoilDataEntity

@Database(
    entities = [FarmerEntity::class, SoilDataEntity::class, CropHistoryEntity::class, CropProfileEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun farmerDao(): FarmerDao
    abstract fun soilDataDao(): SoilDataDao
    abstract fun cropHistoryDao(): CropHistoryDao
    abstract fun cropProfileDao(): CropProfileDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private var appContext: Context? = null
        private var hasLocationPermission: Boolean = false

        fun getDatabase(context: Context): AppDatabase {
            appContext = context.applicationContext
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "raitha_bharosa_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
        
        fun getContext(): Context? = appContext
        
        fun updateLocationPermission(granted: Boolean) {
            hasLocationPermission = granted
        }
        
        fun hasLocationPermission(): Boolean = hasLocationPermission
    }
}