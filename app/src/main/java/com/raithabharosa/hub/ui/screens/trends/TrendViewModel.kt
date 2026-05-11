package com.raithabharosa.hub.ui.screens.trends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raithabharosa.hub.data.repository.FarmerRepository
import com.raithabharosa.hub.domain.model.SoilMetric
import com.raithabharosa.hub.domain.model.SoilTrendData
import com.raithabharosa.hub.domain.model.TrendPeriod
import com.raithabharosa.hub.domain.model.YieldTrendData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class TrendState(
    val selectedPeriod: TrendPeriod = TrendPeriod.LAST_30_DAYS,
    val soilTrendData: SoilTrendData? = null,
    val yieldTrendData: YieldTrendData? = null,
    val selectedSoilMetric: SoilMetric = SoilMetric.MOISTURE,
    val isLoading: Boolean = false,
    val hasSoilData: Boolean = false,
    val hasYieldData: Boolean = false
)

class TrendViewModel(
    private val repository: FarmerRepository
) : ViewModel() {
    private val _state = MutableStateFlow(TrendState())
    val state: StateFlow<TrendState> = _state.asStateFlow()

    init {
        loadTrendData()
    }

    fun setPeriod(period: TrendPeriod) {
        _state.value = _state.value.copy(selectedPeriod = period)
        loadTrendData()
    }

    fun setSoilMetric(metric: SoilMetric) {
        _state.value = _state.value.copy(selectedSoilMetric = metric)
    }

    private fun loadTrendData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val farmer = repository.getCurrentFarmerSync()
                if (farmer != null) {
                    val startTime = System.currentTimeMillis() - (_state.value.selectedPeriod.days * 24 * 60 * 60 * 1000L)

                    val soilEntities = withContext(Dispatchers.IO) {
                        repository.getSoilTrendData(farmer.id, startTime).first()
                    }

                    val yieldEntities = withContext(Dispatchers.IO) {
                        repository.getYieldTrendData(farmer.id).first()
                    }

                    val soilTrendData = if (soilEntities.isNotEmpty()) {
                        repository.mapSoilEntitiesToTrendData(soilEntities)
                    } else null

                    val yieldTrendData = if (yieldEntities.isNotEmpty()) {
                        repository.mapCropHistoryToYieldTrend(yieldEntities)
                    } else null

                    _state.value = _state.value.copy(
                        soilTrendData = soilTrendData,
                        yieldTrendData = yieldTrendData,
                        hasSoilData = soilEntities.isNotEmpty(),
                        hasYieldData = yieldEntities.isNotEmpty(),
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

    fun refresh() {
        loadTrendData()
    }
}