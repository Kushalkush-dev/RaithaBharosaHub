package com.raithabharosa.hub.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raithabharosa.hub.data.repository.FarmerRepository
import com.raithabharosa.hub.domain.model.Farmer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class SettingsState(
    val farmerName: String = "",
    val farmerLocation: String = "",
    val cropType: String = "",
    val isLoading: Boolean = false
)

class SettingsViewModel(
    private val repository: FarmerRepository
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        loadFarmerData()
    }

    private fun loadFarmerData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val farmer = withContext(Dispatchers.IO) {
                    repository.getCurrentFarmerSync()
                }
                if (farmer != null) {
                    _state.value = _state.value.copy(
                        farmerName = farmer.name,
                        farmerLocation = farmer.location,
                        cropType = farmer.cropType.displayName,
                        isLoading = false
                    )
                } else {
                    _state.value = _state.value.copy(isLoading = false)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    fun updateProfile(name: String, cropType: String) {
        viewModelScope.launch {
            try {
                val farmer = withContext(Dispatchers.IO) {
                    repository.getCurrentFarmerSync()
                }
                if (farmer != null) {
                    val updatedFarmer = farmer.copy(
                        name = name,
                        cropType = com.raithabharosa.hub.domain.model.CropType.entries.find {
                            it.displayName == cropType
                        } ?: com.raithabharosa.hub.domain.model.CropType.SUGARCANE
                    )
                    withContext(Dispatchers.IO) {
                        repository.saveFarmer(updatedFarmer)
                    }
                    _state.value = _state.value.copy(
                        farmerName = name,
                        cropType = cropType
                    )
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateLocation(location: String) {
        viewModelScope.launch {
            try {
                val farmer = withContext(Dispatchers.IO) {
                    repository.getCurrentFarmerSync()
                }
                if (farmer != null) {
                    val updatedFarmer = farmer.copy(location = location)
                    withContext(Dispatchers.IO) {
                        repository.saveFarmer(updatedFarmer)
                    }
                    _state.value = _state.value.copy(farmerLocation = location)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}