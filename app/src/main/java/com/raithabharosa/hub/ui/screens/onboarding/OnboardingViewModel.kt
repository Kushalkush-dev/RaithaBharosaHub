package com.raithabharosa.hub.ui.screens.onboarding

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raithabharosa.hub.AppConfig
import com.raithabharosa.hub.data.local.AppDatabase
import com.raithabharosa.hub.data.location.LocationHelper
import com.raithabharosa.hub.data.repository.FarmerRepository
import com.raithabharosa.hub.domain.model.CropType
import com.raithabharosa.hub.domain.model.Farmer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "OnboardingViewModel"

data class OnboardingState(
    val name: String = "",
    val selectedCrop: CropType? = null,
    val plotSize: String = "",
    val location: String = "",
    val isLoading: Boolean = false,
    val isComplete: Boolean = false,
    val error: String? = null
)

class OnboardingViewModel(
    private val repository: FarmerRepository
) : ViewModel() {
    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    fun updateName(name: String) {
        _state.value = _state.value.copy(name = name)
    }

    fun updateCrop(crop: CropType) {
        _state.value = _state.value.copy(selectedCrop = crop)
    }

    fun updatePlotSize(size: String) {
        _state.value = _state.value.copy(plotSize = size)
    }

    fun updateLocation(location: String) {
        _state.value = _state.value.copy(location = location)
    }

    fun refreshLocation() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                Log.d(TAG, "Fetching GPS location...")
                val context = AppDatabase.getContext()
                if (context == null) {
                    Log.e(TAG, "Context is null")
                    _state.value = _state.value.copy(isLoading = false, location = AppConfig.DEFAULT_LOCATION)
                    return@launch
                }
                
                val locationHelper = LocationHelper(context)
                
                if (!locationHelper.hasLocationPermission()) {
                    Log.e(TAG, "No location permission - using default location")
                    _state.value = _state.value.copy(
                        isLoading = false, 
                        location = AppConfig.DEFAULT_LOCATION,
                        error = "Location permission required"
                    )
                    return@launch
                }
                
                val gpsLocation = withContext(Dispatchers.IO) {
                    locationHelper.getCurrentLocation()
                }
                
                if (gpsLocation != null) {
                    val cityName = locationHelper.getCityName(gpsLocation.latitude, gpsLocation.longitude)
                    Log.d(TAG, "Got GPS location: $cityName")
                    _state.value = _state.value.copy(location = cityName, isLoading = false)
                } else {
                    Log.e(TAG, "Could not get GPS location - using default")
                    _state.value = _state.value.copy(
                        isLoading = false, 
                        location = AppConfig.DEFAULT_LOCATION
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error: ${e.message}")
                _state.value = _state.value.copy(
                    isLoading = false, 
                    location = AppConfig.DEFAULT_LOCATION,
                    error = e.message
                )
            }
        }
    }

    fun saveFarmer() {
        if (!isValid()) return
        
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val locationToSave = _state.value.location.ifBlank { AppConfig.DEFAULT_LOCATION }
                val farmer = Farmer(
                    name = _state.value.name,
                    cropType = _state.value.selectedCrop ?: CropType.SUGARCANE,
                    plotSize = _state.value.plotSize.toFloatOrNull() ?: 0f,
                    location = locationToSave
                )
                
                withContext(Dispatchers.IO) {
                    repository.saveFarmer(farmer)
                }
                
                Log.d(TAG, "Farmer saved: ${farmer.name}, ${farmer.location}")
                _state.value = _state.value.copy(isLoading = false, isComplete = true)
            } catch (e: Exception) {
                Log.e(TAG, "Error: ${e.message}")
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun isValid(): Boolean {
        return _state.value.name.isNotBlank() && _state.value.selectedCrop != null
    }
    
    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}