package com.raithabharosa.hub.ui.screens.calendar

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raithabharosa.hub.AppConfig
import com.raithabharosa.hub.data.local.AppDatabase
import com.raithabharosa.hub.data.location.LocationHelper
import com.raithabharosa.hub.data.repository.FarmerRepository
import com.raithabharosa.hub.domain.model.ActionPlan
import com.raithabharosa.hub.domain.model.DailyForecast
import com.raithabharosa.hub.domain.usecase.GenerateActionPlanUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "CalendarViewModel"

data class CalendarState(
    val actionPlans: List<ActionPlan> = emptyList(),
    val dailyForecasts: List<DailyForecast> = emptyList(),
    val location: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class CalendarViewModel(
    private val repository: FarmerRepository,
    private val generateActionPlan: GenerateActionPlanUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(CalendarState())
    val state: StateFlow<CalendarState> = _state.asStateFlow()

    init {
        loadActionPlans()
    }

    private fun loadActionPlans() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val farmer = withContext(Dispatchers.IO) {
                    repository.getCurrentFarmerSync()
                }
                
                var location = farmer?.location ?: ""
                
                if (location.isBlank()) {
                    val context = AppDatabase.getContext()
                    if (context != null) {
                        try {
                            val locationHelper = LocationHelper(context)
                            if (locationHelper.hasLocationPermission()) {
                                val gpsLocation = withContext(Dispatchers.IO) {
                                    locationHelper.getCurrentLocation()
                                }
                                if (gpsLocation != null) {
                                    location = locationHelper.getCityName(gpsLocation.latitude, gpsLocation.longitude)
                                }
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "GPS error: ${e.message}")
                        }
                    }
                    if (location.isBlank()) {
                        location = AppConfig.DEFAULT_LOCATION
                    }
                }
                
                Log.d(TAG, "Calendar using location: $location")
                
                val forecastResult = withContext(Dispatchers.IO) {
                    repository.getForecastData(location, AppConfig.WEATHER_API_KEY)
                }
                val dailyForecasts = forecastResult.getOrNull() ?: emptyList()
                
                val cropInfo = if (farmer != null) {
                    "Farmer Name: ${farmer.name}, Primary Crop: ${farmer.cropType.displayName}"
                } else {
                    "General agriculture"
                }

                val plans = generateActionPlan(
                    cropInfo = cropInfo,
                    dailyForecasts = dailyForecasts,
                    isKannada = false
                )
                _state.value = _state.value.copy(
                    actionPlans = plans,
                    dailyForecasts = dailyForecasts,
                    location = location,
                    isLoading = false
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error: ${e.message}")
                val plans = generateActionPlan("General agriculture", emptyList())
                _state.value = _state.value.copy(
                    actionPlans = plans,
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun refresh() {
        loadActionPlans()
    }
}