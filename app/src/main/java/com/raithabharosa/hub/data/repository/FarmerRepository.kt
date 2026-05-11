package com.raithabharosa.hub.data.repository

import android.content.Context
import android.util.Log
import com.raithabharosa.hub.data.local.AppDatabase
import com.raithabharosa.hub.data.local.entity.CropHistoryEntity
import com.raithabharosa.hub.data.local.entity.CropProfileEntity
import com.raithabharosa.hub.data.local.entity.FarmerEntity
import com.raithabharosa.hub.data.local.entity.SoilDataEntity
import kotlin.random.Random
import com.raithabharosa.hub.data.remote.DataGenerator
import com.raithabharosa.hub.data.remote.RetrofitClient
import com.raithabharosa.hub.domain.model.CropHistory
import com.raithabharosa.hub.domain.model.CropProfile
import com.raithabharosa.hub.domain.model.CropType
import com.raithabharosa.hub.domain.model.DailyForecast
import com.raithabharosa.hub.domain.model.Farmer
import com.raithabharosa.hub.domain.model.SoilData
import com.raithabharosa.hub.domain.model.SoilTrendData
import com.raithabharosa.hub.domain.model.TrendDataPoint
import com.raithabharosa.hub.domain.model.WeatherData
import com.raithabharosa.hub.domain.model.WeatherTrendData
import com.raithabharosa.hub.domain.model.YieldTrendData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "FarmerRepository"

class FarmerRepository(private val database: AppDatabase) {
    private val farmerDao = database.farmerDao()
    private val soilDataDao = database.soilDataDao()
    private val cropHistoryDao = database.cropHistoryDao()
    private val cropProfileDao = database.cropProfileDao()

    fun getCurrentFarmer(): Flow<Farmer?> {
        return farmerDao.getCurrentFarmer().map { entity ->
            entity?.let {
                Farmer(
                    id = it.id,
                    name = it.name,
                    cropType = CropType.entries.find { c -> c.displayName == it.cropType } ?: CropType.SUGARCANE,
                    plotSize = it.plotSize,
                    location = it.location
                )
            }
        }
    }

    suspend fun getCurrentFarmerSync(): Farmer? {
        return farmerDao.getCurrentFarmerSync()?.let { entity ->
            Farmer(
                id = entity.id,
                name = entity.name,
                cropType = CropType.entries.find { c -> c.displayName == entity.cropType } ?: CropType.SUGARCANE,
                plotSize = entity.plotSize,
                location = entity.location
            )
        }
    }

    suspend fun saveFarmer(farmer: Farmer): Long {
        val entity = FarmerEntity(
            id = farmer.id,
            name = farmer.name,
            cropType = farmer.cropType.displayName,
            plotSize = farmer.plotSize,
            location = farmer.location
        )
        return farmerDao.insertFarmer(entity)
    }

    fun getSoilData(farmerId: Long): Flow<SoilData?> {
        return soilDataDao.getLatestSoilData(farmerId).map { entity ->
            entity?.let {
                SoilData(
                    id = it.id,
                    farmerId = it.farmerId,
                    moisture = it.moisture,
                    nitrogen = it.nitrogen,
                    phosphorus = it.phosphorus,
                    potassium = it.potassium,
                    ph = it.ph,
                    temperature = it.temperature,
                    recordedAt = it.recordedAt
                )
            }
        }
    }

    suspend fun saveSoilData(soilData: SoilData): Long {
        val entity = SoilDataEntity(
            id = soilData.id,
            farmerId = soilData.farmerId,
            moisture = soilData.moisture,
            nitrogen = soilData.nitrogen,
            phosphorus = soilData.phosphorus,
            potassium = soilData.potassium,
            ph = soilData.ph,
            temperature = soilData.temperature
        )
        return soilDataDao.insertSoilData(entity)
    }

    fun getCropHistory(farmerId: Long): Flow<List<CropHistory>> {
        return cropHistoryDao.getCropHistory(farmerId).map { entities ->
            entities.map { entity ->
                CropHistory(
                    id = entity.id,
                    farmerId = entity.farmerId,
                    cropType = entity.cropType,
                    sowingDate = entity.sowingDate,
                    harvestDate = entity.harvestDate,
                    yield = entity.yield,
                    notes = entity.notes,
                    season = entity.season,
                    year = entity.year
                )
            }
        }
    }

    suspend fun saveCropHistory(history: CropHistory): Long {
        val entity = CropHistoryEntity(
            id = history.id,
            farmerId = history.farmerId,
            cropType = history.cropType,
            sowingDate = history.sowingDate,
            harvestDate = history.harvestDate,
            yield = history.yield,
            notes = history.notes,
            season = history.season,
            year = history.year
        )
        return cropHistoryDao.insertCropHistory(entity)
    }

    suspend fun getWeatherData(location: String, apiKey: String?): Result<WeatherData> {
        return try {
            Log.d(TAG, "Fetching weather for location: $location, apiKey provided: ${!apiKey.isNullOrEmpty()}")
            
            if (apiKey.isNullOrEmpty()) {
                Log.d(TAG, "No API key - using simulated data")
                val simulatedTemp = DataGenerator.generateTemperature()
                val simulatedMoisture = DataGenerator.generateMoistureLevel()
                return Result.success(
                    WeatherData(
                        temperature = simulatedTemp,
                        humidity = (simulatedMoisture * 3).toInt(),
                        condition = DataGenerator.simulateWeatherCondition(),
                        windSpeed = Random.nextFloat() * 10f,
                        daysUntilStorm = DataGenerator.daysUntilStorm()
                    )
                )
            }
            
            Log.d(TAG, "Calling OpenWeatherMap API...")
            val response = RetrofitClient.weatherApiService.getCurrentWeather(location, apiKey)
            val main = response.main
            val weather = response.weather?.firstOrNull()
            
            Log.d(TAG, "API Response: temp=${main?.temp}, humidity=${main?.humidity}, condition=${weather?.main}")
            
            Result.success(
                WeatherData(
                    temperature = main?.temp?.toFloat() ?: 25f,
                    humidity = main?.humidity ?: 50,
                    condition = weather?.main ?: "Clear",
                    windSpeed = response.wind?.speed?.toFloat() ?: 0f
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Weather API error: ${e.message}", e)
            val simulatedTemp = DataGenerator.generateTemperature()
            val simulatedMoisture = DataGenerator.generateMoistureLevel()
            Result.success(
                WeatherData(
                    temperature = simulatedTemp,
                    humidity = (simulatedMoisture * 3).toInt(),
                    condition = DataGenerator.simulateWeatherCondition(),
                    windSpeed = Random.nextFloat() * 10f,
                    daysUntilStorm = DataGenerator.daysUntilStorm()
                )
            )
        }
    }

    suspend fun getForecastData(location: String, apiKey: String?): Result<List<DailyForecast>> {
        return try {
            Log.d(TAG, "Fetching forecast for location: $location")
            
            if (apiKey.isNullOrEmpty()) {
                return Result.success(generateSimulatedForecast())
            }
            
            val response = RetrofitClient.weatherApiService.getForecast(location, apiKey)
            val forecastList = response.list ?: emptyList()
            
            val dailyForecasts = forecastList
                .groupBy { it.dtTxt?.substringBefore(" ") }
                .map { (date, items) ->
                    val temps = items.mapNotNull { it.main?.temp }
                    val humidities = items.mapNotNull { it.main?.humidity }
                    val conditions = items.mapNotNull { it.weather?.firstOrNull()?.main }
                    val pops = items.mapNotNull { it.pop }
                    
                    DailyForecast(
                        day = date ?: "",
                        tempMin = temps.minOrNull()?.toFloat() ?: 20f,
                        tempMax = temps.maxOrNull()?.toFloat() ?: 30f,
                        humidity = humidities.average().toInt(),
                        condition = conditions.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key ?: "Clear",
                        precipitationChance = (pops.maxOrNull() ?: 0.0 * 100).toInt()
                    )
                }
                .take(7)
            
            Result.success(dailyForecasts)
        } catch (e: Exception) {
            Log.e(TAG, "Forecast API error: ${e.message}", e)
            Result.success(generateSimulatedForecast())
        }
    }
    
    private fun generateSimulatedForecast(): List<DailyForecast> {
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return (0..6).map { i ->
            val date = java.util.Date(System.currentTimeMillis() + i * 24 * 60 * 60 * 1000L)
            DailyForecast(
                day = dateFormat.format(date),
                tempMin = (22..28).random().toFloat(),
                tempMax = (30..36).random().toFloat(),
                humidity = (40..80).random(),
                condition = listOf("Clear", "Clouds", "Rain", "Clear").random(),
                precipitationChance = listOf(0, 10, 30, 60).random()
            )
        }
    }
    
    fun getCropProfiles(farmerId: Long): Flow<List<CropProfile>> {
        return cropProfileDao.getCropProfiles(farmerId).map { entities ->
            entities.map { it.toCropProfile() }
        }
    }
    
    fun getActiveCropProfile(farmerId: Long): Flow<CropProfile?> {
        return cropProfileDao.getActiveProfile(farmerId).map { it?.toCropProfile() }
    }
    
    suspend fun saveCropProfile(profile: CropProfile): Long {
        cropProfileDao.deactivateAllProfiles(profile.farmerId)
        val entity = profile.toEntity()
        return cropProfileDao.insertCropProfile(entity)
    }
    
    suspend fun setActiveCropProfile(profileId: Long, farmerId: Long) {
        cropProfileDao.deactivateAllProfiles(farmerId)
        cropProfileDao.activateProfile(profileId)
    }
    
    suspend fun deleteCropProfile(profileId: Long) {
        cropProfileDao.deleteCropProfileById(profileId)
    }
    
    private fun CropProfileEntity.toCropProfile(): CropProfile {
        return CropProfile(
            id = id,
            farmerId = farmerId,
            name = name,
            cropType = CropType.entries.find { c -> c.displayName == cropType } ?: CropType.SUGARCANE,
            plotSize = plotSize,
            location = location,
            isActive = isActive
        )
    }
    
    private fun CropProfile.toEntity(): CropProfileEntity {
        return CropProfileEntity(
            id = id,
            farmerId = farmerId,
            name = name,
            cropType = cropType.displayName,
            plotSize = plotSize,
            location = location,
            isActive = isActive
        )
    }

    fun getSoilTrendData(farmerId: Long, startTime: Long): Flow<List<SoilDataEntity>> {
        return soilDataDao.getSoilDataInRange(farmerId, startTime)
    }

    fun getYieldTrendData(farmerId: Long): Flow<List<CropHistoryEntity>> {
        return cropHistoryDao.getYieldHistory(farmerId)
    }

    suspend fun getWeatherTrendData(farmerId: Long): List<WeatherTrendData> {
        return emptyList()
    }

    fun mapSoilEntitiesToTrendData(entities: List<SoilDataEntity>): SoilTrendData {
        val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())

        return SoilTrendData(
            moistureData = entities.map { TrendDataPoint(it.recordedAt, dateFormat.format(Date(it.recordedAt)), it.moisture) },
            nitrogenData = entities.map { TrendDataPoint(it.recordedAt, dateFormat.format(Date(it.recordedAt)), it.nitrogen) },
            phosphorusData = entities.map { TrendDataPoint(it.recordedAt, dateFormat.format(Date(it.recordedAt)), it.phosphorus) },
            potassiumData = entities.map { TrendDataPoint(it.recordedAt, dateFormat.format(Date(it.recordedAt)), it.potassium) },
            phData = entities.map { TrendDataPoint(it.recordedAt, dateFormat.format(Date(it.recordedAt)), it.ph) },
            temperatureData = entities.map { TrendDataPoint(it.recordedAt, dateFormat.format(Date(it.recordedAt)), it.temperature) }
        )
    }

    fun mapCropHistoryToYieldTrend(entities: List<CropHistoryEntity>): YieldTrendData {
        val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

        return YieldTrendData(
            yieldData = entities.mapNotNull { entity ->
                if (entity.harvestDate != null && entity.yield > 0) {
                    TrendDataPoint(
                        timestamp = entity.harvestDate,
                        label = dateFormat.format(Date(entity.harvestDate)),
                        value = entity.yield,
                        cropType = entity.cropType
                    )
                } else null
            }
        )
    }
}