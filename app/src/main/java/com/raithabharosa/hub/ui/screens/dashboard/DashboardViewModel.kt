package com.raithabharosa.hub.ui.screens.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raithabharosa.hub.AppConfig
import com.raithabharosa.hub.data.local.AppDatabase
import com.raithabharosa.hub.data.location.LocationHelper
import com.raithabharosa.hub.data.repository.FarmerRepository
import com.raithabharosa.hub.domain.model.CropProfile
import com.raithabharosa.hub.domain.model.Farmer
import com.raithabharosa.hub.domain.model.SoilData
import com.raithabharosa.hub.domain.model.SowingIndex
import com.raithabharosa.hub.domain.model.WeatherData
import com.raithabharosa.hub.domain.usecase.CalculateSowingIndexUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "DashboardViewModel"

data class DashboardState(
    val farmer: Farmer? = null,
    val sowingIndex: SowingIndex? = null,
    val soilData: SoilData? = null,
    val weatherData: WeatherData? = null,
    val cropProfiles: List<CropProfile> = emptyList(),
    val activeCropProfileId: Long? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class DashboardViewModel(
    private val repository: FarmerRepository,
    private val calculateSowingIndex: CalculateSowingIndexUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()
    
    private var cachedWeatherData: WeatherData? = null

    init {
        Log.d(TAG, "Initializing DashboardViewModel")
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                Log.d(TAG, "Loading farmer data...")
                
                val farmer = withContext(Dispatchers.IO) {
                    repository.getCurrentFarmerSync()
                }
                
                if (farmer == null) {
                    Log.d(TAG, "No farmer found")
                    _state.value = _state.value.copy(isLoading = false)
                    return@launch
                }
                
                Log.d(TAG, "Farmer: ${farmer.name}, Location: ${farmer.location}")
                
                var locationToUse = farmer.location
                if (locationToUse.isBlank()) {
                    val context = AppDatabase.getContext()
                    if (context != null) {
                        try {
                            val locationHelper = LocationHelper(context)
                            if (locationHelper.hasLocationPermission()) {
                                val gpsLocation = withContext(Dispatchers.IO) {
                                    locationHelper.getCurrentLocation()
                                }
                                if (gpsLocation != null) {
                                    locationToUse = locationHelper.getCityName(gpsLocation.latitude, gpsLocation.longitude)
                                    Log.d(TAG, "Using GPS location: $locationToUse")
                                }
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "GPS error: ${e.message}")
                        }
                    }
                    if (locationToUse.isBlank()) {
                        locationToUse = AppConfig.DEFAULT_LOCATION
                    }
                }
                
                val weatherResult = withContext(Dispatchers.IO) {
                    repository.getWeatherData(locationToUse, AppConfig.WEATHER_API_KEY)
                }
                val weatherData = weatherResult.getOrNull() ?: cachedWeatherData
                if (weatherData != null && weatherResult.isSuccess) {
                    cachedWeatherData = weatherData
                }
                Log.d(TAG, "Weather: ${weatherData?.temperature}°C, ${weatherData?.condition}")
                
                val soilData = withContext(Dispatchers.IO) {
                    repository.getSoilData(farmer.id).first()
                }
                
                val cropProfiles = withContext(Dispatchers.IO) {
                    repository.getCropProfiles(farmer.id).first()
                }
                
                val activeProfile = cropProfiles.firstOrNull { it.isActive }
                
                val sowingIndex = calculateSowingIndex(
                    soilData,
                    activeProfile?.cropType ?: farmer.cropType,
                    useWeatherData = weatherData
                )
                
                Log.d(TAG, "Sowing index: ${sowingIndex.score}")
                
                _state.value = _state.value.copy(
                    farmer = farmer,
                    sowingIndex = sowingIndex,
                    soilData = soilData,
                    weatherData = weatherData ?: cachedWeatherData,
                    cropProfiles = cropProfiles,
                    activeCropProfileId = activeProfile?.id,
                    isLoading = false
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error loading data", e)
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun refreshData() {
        loadData()
    }
    
    fun switchCrop(profileId: Long) {
        viewModelScope.launch {
            val farmer = _state.value.farmer ?: return@launch
            withContext(Dispatchers.IO) {
                repository.setActiveCropProfile(profileId, farmer.id)
            }
            loadData()
        }
    }
}