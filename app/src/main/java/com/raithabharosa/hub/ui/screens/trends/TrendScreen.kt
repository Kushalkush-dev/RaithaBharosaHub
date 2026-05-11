package com.raithabharosa.hub.ui.screens.trends

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.line.lineSpec
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import com.raithabharosa.hub.TranslationManager
import com.raithabharosa.hub.data.repository.FarmerRepository
import com.raithabharosa.hub.domain.model.SoilMetric
import com.raithabharosa.hub.domain.model.TrendDataPoint
import com.raithabharosa.hub.domain.model.TrendPeriod
import com.raithabharosa.hub.ui.theme.GreenGo
import com.raithabharosa.hub.ui.theme.OrangeCaution

private val cropColors = listOf(
    GreenGo,
    OrangeCaution,
    Color(0xFF2196F3),
    Color(0xFF9C27B0),
    Color(0xFF00BCD4)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrendScreen(
    repository: FarmerRepository,
    showKannadaLabels: Boolean = false
) {
    val viewModel = viewModel { TrendViewModel(repository) }
    val state by viewModel.state.collectAsState()
    val t = { text: String -> TranslationManager.translate(text, showKannadaLabels) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = t("Trends"),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { viewModel.refresh() }) {
                Icon(Icons.Default.Refresh, contentDescription = t("Refresh"))
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            item {
                Text(
                    text = t("Time Period"),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(TrendPeriod.entries.toList()) { period ->
                        FilterChip(
                            selected = state.selectedPeriod == period,
                            onClick = { viewModel.setPeriod(period) },
                            label = { Text(t(period.label)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GreenGo,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                TrendSection(
                    title = t("Soil Health Trends"),
                    icon = Icons.Default.Agriculture,
                    iconColor = GreenGo,
                    dataPoints = getSoilDataPoints(state),
                    metric = state.selectedSoilMetric,
                    onMetricChange = { viewModel.setSoilMetric(it) },
                    isEmpty = !state.hasSoilData,
                    emptyMessage = t("Add soil data to see trends")
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                YieldTrendSection(
                    title = t("Yield Trends"),
                    icon = Icons.Default.ShowChart,
                    iconColor = OrangeCaution,
                    dataPoints = state.yieldTrendData?.yieldData ?: emptyList(),
                    isEmpty = !state.hasYieldData,
                    emptyMessage = t("Record your first harvest"),
                    t = t
                )
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrendSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    dataPoints: List<TrendDataPoint>,
    metric: SoilMetric,
    onMetricChange: (SoilMetric) -> Unit,
    isEmpty: Boolean,
    emptyMessage: String
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(SoilMetric.entries.toList()) { m ->
                FilterChip(
                    selected = metric == m,
                    onClick = { onMetricChange(m) },
                    label = { Text(m.label, style = MaterialTheme.typography.labelSmall) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = iconColor,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isEmpty) {
            EmptyStateCard(emptyMessage)
        } else if (dataPoints.isEmpty()) {
            EmptyStateCard("No data for selected period")
        } else {
            TrendChart(dataPoints = dataPoints, color = iconColor)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun YieldTrendSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    dataPoints: List<TrendDataPoint>,
    isEmpty: Boolean,
    emptyMessage: String,
    t: (String) -> String
) {
    var selectedCrop by remember { mutableStateOf<String?>(null) }

    val crops = remember(dataPoints) {
        dataPoints.map { it.cropType }.distinct()
    }

    val filteredDataPoints = remember(dataPoints, selectedCrop) {
        if (selectedCrop == null) dataPoints
        else dataPoints.filter { it.cropType == selectedCrop }
    }

    val chartColor = remember(selectedCrop, crops) {
        if (selectedCrop == null) iconColor
        else {
            val index = crops.indexOf(selectedCrop)
            cropColors[index % cropColors.size]
        }
    }

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }

        if (crops.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = t("Filter by Crop"),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(crops) { crop ->
                    val colorIndex = crops.indexOf(crop) % cropColors.size

                    FilterChip(
                        selected = selectedCrop == crop || (selectedCrop == null && crops.size == 1),
                        onClick = {
                            selectedCrop = if (selectedCrop == crop && crops.size > 1) null else crop
                        },
                        label = { Text(crop, style = MaterialTheme.typography.labelSmall) },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(cropColors[colorIndex])
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = cropColors[colorIndex],
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isEmpty) {
            EmptyStateCard(emptyMessage)
        } else if (filteredDataPoints.isEmpty()) {
            EmptyStateCard(t("No yield records yet"))
        } else {
            SingleTrendChart(
                dataPoints = filteredDataPoints.sortedBy { it.timestamp },
                color = chartColor,
                unit = " q"
            )
        }
    }
}

@Composable
private fun EmptyStateCard(message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.ShowChart,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun SingleTrendChart(
    dataPoints: List<TrendDataPoint>,
    color: Color,
    unit: String = ""
) {
    val chartEntryModelProducer = remember(dataPoints) {
        ChartEntryModelProducer(
            dataPoints.mapIndexed { index, point -> entryOf(index.toFloat(), point.value) }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (dataPoints.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Latest: ${dataPoints.last().value}$unit",
                        style = MaterialTheme.typography.labelMedium,
                        color = color,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "First: ${dataPoints.first().value}$unit",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Chart(
                chart = lineChart(
                    lines = listOf(
                        lineSpec(
                            lineColor = color,
                            lineBackgroundShader = null
                        )
                    )
                ),
                chartModelProducer = chartEntryModelProducer,
                startAxis = rememberStartAxis(
                    label = textComponent(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textSize = androidx.compose.ui.unit.TextUnit.Unspecified
                    ),
                    itemPlacer = AxisItemPlacer.Vertical.default(maxItemCount = 5)
                ),
                bottomAxis = rememberBottomAxis(
                    label = textComponent(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textSize = androidx.compose.ui.unit.TextUnit.Unspecified
                    ),
                    valueFormatter = { value, _ ->
                        val index = value.toInt()
                        if (index in dataPoints.indices) dataPoints[index].label else ""
                    },
                    itemPlacer = AxisItemPlacer.Horizontal.default(spacing = 2)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
    }
}

@Composable
private fun TrendChart(
    dataPoints: List<TrendDataPoint>,
    color: Color,
    unit: String = ""
) {
    val chartEntryModelProducer = remember(dataPoints) {
        ChartEntryModelProducer(
            dataPoints.mapIndexed { index, point -> entryOf(index.toFloat(), point.value) }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (dataPoints.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Latest: ${dataPoints.last().value}$unit",
                        style = MaterialTheme.typography.labelMedium,
                        color = color,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "First: ${dataPoints.first().value}$unit",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Chart(
                chart = lineChart(
                    lines = listOf(
                        lineSpec(
                            lineColor = color,
                            lineBackgroundShader = null
                        )
                    )
                ),
                chartModelProducer = chartEntryModelProducer,
                startAxis = rememberStartAxis(
                    label = textComponent(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textSize = androidx.compose.ui.unit.TextUnit.Unspecified
                    ),
                    itemPlacer = AxisItemPlacer.Vertical.default(maxItemCount = 5)
                ),
                bottomAxis = rememberBottomAxis(
                    label = textComponent(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textSize = androidx.compose.ui.unit.TextUnit.Unspecified
                    ),
                    valueFormatter = { value, _ ->
                        val index = value.toInt()
                        if (index in dataPoints.indices) dataPoints[index].label else ""
                    },
                    itemPlacer = AxisItemPlacer.Horizontal.default(spacing = 2)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
    }
}

private fun getSoilDataPoints(state: TrendState): List<TrendDataPoint> {
    return when (state.selectedSoilMetric) {
        SoilMetric.MOISTURE -> state.soilTrendData?.moistureData ?: emptyList()
        SoilMetric.NITROGEN -> state.soilTrendData?.nitrogenData ?: emptyList()
        SoilMetric.PHOSPHORUS -> state.soilTrendData?.phosphorusData ?: emptyList()
        SoilMetric.POTASSIUM -> state.soilTrendData?.potassiumData ?: emptyList()
        SoilMetric.PH -> state.soilTrendData?.phData ?: emptyList()
        SoilMetric.TEMPERATURE -> state.soilTrendData?.temperatureData ?: emptyList()
    }
}
