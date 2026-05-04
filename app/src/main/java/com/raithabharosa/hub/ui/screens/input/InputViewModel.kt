package com.raithabharosa.hub.ui.screens.input

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raithabharosa.hub.data.repository.FarmerRepository
import com.raithabharosa.hub.domain.model.Farmer
import com.raithabharosa.hub.domain.model.SoilData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class InputState(
    val farmer: Farmer? = null,
    val moisture: String = "",
    val nitrogen: String = "",
    val phosphorus: String = "",
    val potassium: String = "",
    val temperature: String = "",
    val isLoading: Boolean = false,
    val isSaved: Boolean = false
)

class InputViewModel(
    private val repository: FarmerRepository
) : ViewModel() {
    private val _state = MutableStateFlow(InputState())
    val state: StateFlow<InputState> = _state.asStateFlow()

    init {
        loadFarmer()
    }

    private fun loadFarmer() {
        viewModelScope.launch {
            val farmer = repository.getCurrentFarmerSync()
            _state.value = _state.value.copy(farmer = farmer)
        }
    }

    fun updateMoisture(value: String) {
        _state.value = _state.value.copy(moisture = value)
    }

    fun updateNitrogen(value: String) {
        _state.value = _state.value.copy(nitrogen = value)
    }

    fun updatePhosphorus(value: String) {
        _state.value = _state.value.copy(phosphorus = value)
    }

    fun updatePotassium(value: String) {
        _state.value = _state.value.copy(potassium = value)
    }

    fun updateTemperature(value: String) {
        _state.value = _state.value.copy(temperature = value)
    }

    fun saveSoilData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val farmer = _state.value.farmer
                if (farmer != null) {
                    val soilData = SoilData(
                        farmerId = farmer.id,
                        moisture = _state.value.moisture.toFloatOrNull() ?: 25f,
                        nitrogen = _state.value.nitrogen.toFloatOrNull() ?: 60f,
                        phosphorus = _state.value.phosphorus.toFloatOrNull() ?: 30f,
                        potassium = _state.value.potassium.toFloatOrNull() ?: 25f,
                        temperature = _state.value.temperature.toFloatOrNull() ?: 25f
                    )
                    repository.saveSoilData(soilData)
                }
                _state.value = _state.value.copy(isLoading = false, isSaved = true)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    fun isValid(): Boolean {
        return _state.value.moisture.isNotBlank() &&
                _state.value.farmer != null
    }

    fun resetSaved() {
        _state.value = _state.value.copy(isSaved = false)
    }
    
    fun clearSoilData() {
        _state.value = _state.value.copy(
            moisture = "",
            nitrogen = "",
            phosphorus = "",
            potassium = "",
            temperature = "",
            isSaved = false
        )
    }
}