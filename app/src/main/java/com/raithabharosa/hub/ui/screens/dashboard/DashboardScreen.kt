package com.raithabharosa.hub.ui.screens.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.raithabharosa.hub.data.repository.FarmerRepository
import com.raithabharosa.hub.domain.model.CropProfile
import com.raithabharosa.hub.domain.model.SowingStatus
import com.raithabharosa.hub.domain.usecase.CalculateSowingIndexUseCase
import com.raithabharosa.hub.ui.screens.dashboard.DashboardViewModel
import com.raithabharosa.hub.ui.theme.GreenGo
import com.raithabharosa.hub.ui.theme.GreenGoLight
import com.raithabharosa.hub.ui.theme.OrangeCaution
import com.raithabharosa.hub.ui.theme.RedWait

@Composable
fun DashboardScreen(
    repository: FarmerRepository,
    calculateSowingIndex: CalculateSowingIndexUseCase,
    showKannadaLabels: Boolean = false,
    onNavigateToMyCrops: () -> Unit = {}
) {
    val viewModel = viewModel { DashboardViewModel(repository, calculateSowingIndex) }
    val state by viewModel.state.collectAsState()

    val translate: (String) -> String = { key ->
        if (!showKannadaLabels) key
        else when(key) {
            "Welcome!" -> "ಸ್ವಾಗತ!"
            "Sowing Index" -> "ಬಿತ್ತಲು ಸೂಚ್ಯಂಕ"
            "GO" -> "ಮುಂದುವರಿಸಿ"
            "WAIT" -> "ನಿರೀಕ್ಷಿಸಿ"
            "CAUTION" -> "ಎಚ್ಚರಿಕೆ"
            "STOP" -> "ನಿಲ್ಲಿಸಿ"
            "Weather" -> "ಹವಾಮಾನ"
            "Recommendation" -> "ಶಿಫಾರಸು"
            "Temperature" -> "ತಾಪಮಾನ"
            "Humidity" -> "ಆರ್ದ್ರತೆ"
            "Wind" -> "ಗಾಳಿ"
            "Soil Health" -> "ಮಣ್ಣಿನ ಆರೋಗ್ಯ"
            "Loading..." -> "ಲೋಡ್ ಆಗುತ್ತಿದೆ..."
            "My Crops" -> "ನನ್ನ ಬೆಳೆಗಳು"
            "Switch Crop" -> "ಬೆಳೆ ಬದಲಿಸಿ"
            "Add Crop" -> "ಹೊಸ ಬೆಳೆ"
            "View Detailed History" -> "ವಿವರವಾದ ಇತಿಹಾಸವನ್ನು ವೀಕ್ಷಿಸಿ"
            else -> key
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            HeaderSection(state, viewModel, translate)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            CropSwitcherSection(state, onNavigateToMyCrops, translate)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (state.isLoading && state.sowingIndex == null) {
                LoadingSection(translate)
            } else {
                SowingIndexCard(state, translate)
                Spacer(modifier = Modifier.height(16.dp))
                WeatherCard(state, translate)
                Spacer(modifier = Modifier.height(16.dp))
                SoilHealthCard(state, translate)
                Spacer(modifier = Modifier.height(16.dp))
                RecommendationsCard(state, translate)
            }
        }
        
        if (state.isLoading && state.sowingIndex != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                CircularProgressIndicator(
                    color = GreenGo,
                    strokeWidth = 3.dp
                )
            }
        }
    }
}

@Composable
private fun CropSwitcherSection(
    state: DashboardState, 
    onNavigateToMyCrops: () -> Unit,
    t: (String) -> String
) {
    val profiles = state.cropProfiles
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = t("My Crops"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                IconButton(onClick = onNavigateToMyCrops) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Go to My Crops",
                        tint = GreenGo
                    )
                }
            }
            
            if (profiles.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    profiles.take(4).forEach { profile ->
                        CropChip(
                            profile = profile,
                            isSelected = profile.id == state.activeCropProfileId,
                            onClick = { }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Button(
                onClick = onNavigateToMyCrops,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = GreenGo.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                Icon(
                    Icons.Default.Grass,
                    contentDescription = null,
                    tint = GreenGo,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = t("View Detailed History"),
                    color = GreenGo,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun CropChip(profile: CropProfile, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) GreenGo else GreenGo.copy(alpha = 0.1f)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = profile.cropType.displayName,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) Color.White else GreenGo,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun HeaderSection(state: DashboardState, viewModel: DashboardViewModel, t: (String) -> String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = t("Welcome!"),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Text(
                text = state.farmer?.name ?: "Farmer",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            if (state.farmer?.location?.isNotBlank() == true) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Cloud,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = state.farmer?.location ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            }
        }
        IconButton(
            onClick = { viewModel.refreshData() },
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Icon(
                Icons.Default.Refresh,
                contentDescription = "Refresh",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun LoadingSection(t: (String) -> String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = GreenGo)
            Spacer(modifier = Modifier.height(16.dp))
            Text(t("Loading..."), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun SowingIndexCard(state: DashboardState, t: (String) -> String) {
    val index = state.sowingIndex
    val bgColor = when (index?.status) {
        SowingStatus.OPTIMAL -> GreenGo
        SowingStatus.GOOD -> GreenGoLight
        SowingStatus.FAIR -> OrangeCaution
        SowingStatus.WAIT -> Color(0xFFFF5722)
        SowingStatus.NOT_ADVISED -> RedWait
        null -> MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = if (index?.status != null && index.status != SowingStatus.FAIR) Color.White else Color.Black
    
    val statusText = when (index?.status) {
        SowingStatus.OPTIMAL, SowingStatus.GOOD -> t("GO")
        SowingStatus.FAIR -> t("CAUTION")
        SowingStatus.WAIT -> t("WAIT")
        SowingStatus.NOT_ADVISED -> t("STOP")
        null -> "--"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = t("Sowing Index"),
                style = MaterialTheme.typography.titleMedium,
                color = textColor.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "${index?.score ?: "--"}%",
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 72.sp),
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (textColor == Color.White) Color.White.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.1f))
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
        }
    }
}

@Composable
private fun WeatherCard(state: DashboardState, t: (String) -> String) {
    val weather = state.weatherData
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = t("Weather"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    when (weather?.condition) {
                        "Rain" -> Icons.Default.WaterDrop
                        "Clouds" -> Icons.Default.Cloud
                        else -> Icons.Default.Air
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherItem(icon = Icons.Default.Thermostat, value = "${weather?.temperature?.toInt() ?: "--"}°C", label = t("Temperature"))
                WeatherItem(icon = Icons.Default.WaterDrop, value = "${weather?.humidity ?: "--"}%", label = t("Humidity"))
                WeatherItem(icon = Icons.Default.Air, value = "${weather?.windSpeed?.toInt() ?: "--"} km/h", label = t("Wind"))
            }
        }
    }
}

@Composable
private fun WeatherItem(icon: ImageVector, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
    }
}

@Composable
private fun SoilHealthCard(state: DashboardState, t: (String) -> String) {
    val soil = state.soilData
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(text = t("Soil Health"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SoilNutrientGauge("N", soil?.nitrogen?.toInt() ?: 60, 120f, "kg/ha")
                SoilNutrientGauge("P", soil?.phosphorus?.toInt() ?: 30, 60f, "kg/ha")
                SoilNutrientGauge("K", soil?.potassium?.toInt() ?: 25, 50f, "kg/ha")
                SoilNutrientGauge("M", soil?.moisture?.toInt() ?: 25, 40f, "%")
            }
        }
    }
}

@Composable
private fun SoilNutrientGauge(label: String, value: Int, maxValue: Float, unit: String) {
    val progress = (value / maxValue).coerceIn(0f, 1f)
    val color = when {
        progress >= 0.7f -> GreenGo
        progress >= 0.4f -> OrangeCaution
        else -> RedWait
    }
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(56.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(56.dp)) {
                drawArc(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 6f, cap = StrokeCap.Round)
                )
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    style = Stroke(width = 6f, cap = StrokeCap.Round)
                )
            }
            Text(
                text = "$value",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$label ($unit)",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
    }
}

@Composable
private fun RecommendationsCard(state: DashboardState, t: (String) -> String) {
    val index = state.sowingIndex
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(text = t("Recommendation"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = index?.recommendation ?: "No recommendation available", style = MaterialTheme.typography.bodyLarge)
            if (!index?.reasons.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                index?.reasons?.forEach { reason ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(if (reason.contains("too") || reason.contains("deficient") || reason.contains("Bad")) RedWait else GreenGo))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = reason, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}