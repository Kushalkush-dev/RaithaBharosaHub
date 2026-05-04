package com.raithabharosa.hub.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raithabharosa.hub.data.repository.FarmerRepository
import com.raithabharosa.hub.domain.model.CropHistory
import com.raithabharosa.hub.domain.model.CropProfile
import com.raithabharosa.hub.domain.model.CropType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class HistoryState(
    val cropProfiles: List<CropProfile> = emptyList(),
    val activeCropId: Long? = null,
    val isLoading: Boolean = false
)

class HistoryViewModel(
    private val repository: FarmerRepository
) : ViewModel() {
    private val _state = MutableStateFlow(HistoryState())
    val state: StateFlow<HistoryState> = _state.asStateFlow()

    init {
        loadCrops()
    }

    private fun loadCrops() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val farmer = repository.getCurrentFarmerSync()
                if (farmer != null) {
                    val profiles = withContext(Dispatchers.IO) {
                        repository.getCropProfiles(farmer.id).first()
                    }
                    val active = profiles.firstOrNull { it.isActive }
                    _state.value = _state.value.copy(
                        cropProfiles = profiles,
                        activeCropId = active?.id,
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

    fun addCropProfile(name: String, cropType: CropType, plotSize: Float, location: String) {
        viewModelScope.launch {
            try {
                val farmer = repository.getCurrentFarmerSync() ?: return@launch
                val profile = CropProfile(
                    farmerId = farmer.id,
                    name = name,
                    cropType = cropType,
                    plotSize = plotSize,
                    location = location
                )
                withContext(Dispatchers.IO) {
                    repository.saveCropProfile(profile)
                }
                loadCrops()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun setActiveCrop(profileId: Long) {
        viewModelScope.launch {
            try {
                val farmer = repository.getCurrentFarmerSync() ?: return@launch
                withContext(Dispatchers.IO) {
                    repository.setActiveCropProfile(profileId, farmer.id)
                }
                loadCrops()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun deleteCropProfile(profileId: Long) {
        viewModelScope.launch {
            try {
                val farmer = repository.getCurrentFarmerSync() ?: return@launch
                withContext(Dispatchers.IO) {
                    repository.deleteCropProfile(profileId)
                }
                loadCrops()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}