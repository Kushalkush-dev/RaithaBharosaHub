package com.raithabharosa.hub

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.raithabharosa.hub.data.local.AppDatabase
import com.raithabharosa.hub.data.repository.FarmerRepository
import com.raithabharosa.hub.domain.usecase.CalculateSowingIndexUseCase
import com.raithabharosa.hub.domain.usecase.GenerateActionPlanUseCase
import com.raithabharosa.hub.ui.navigation.AppNavigation
import com.raithabharosa.hub.ui.theme.GreenGo
import com.raithabharosa.hub.ui.theme.RaithaBharosaHubTheme

class MainActivity : ComponentActivity() {
    
    private var locationPermissionGranted = false
    
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        locationPermissionGranted = permissions.values.any { it }
        AppDatabase.updateLocationPermission(locationPermissionGranted)
    }

    private var themeKey by mutableStateOf(0)
    
    private fun updateThemeKey() {
        themeKey++
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = FarmerRepository(database)
        val calculateSowingIndex = CalculateSowingIndexUseCase()
        val generateActionPlan = GenerateActionPlanUseCase()
        
        checkLocationPermission()
        
        setContent {
            val navController = rememberNavController()
            
            RecomposeTheme(
                key = themeKey,
                repository = repository,
                calculateSowingIndex = calculateSowingIndex,
                generateActionPlan = generateActionPlan,
                navController = navController,
                requestLocationPermission = { requestLocationPermission() },
                onThemeChanged = { updateThemeKey() }
            )
        }
    }
    
    private fun checkLocationPermission() {
        locationPermissionGranted = ContextCompat.checkSelfPermission(
            this, 
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        AppDatabase.updateLocationPermission(locationPermissionGranted)
        
        if (!locationPermissionGranted) {
            requestLocationPermission()
        }
    }
    
    private fun requestLocationPermission() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}

data class NavItem(val route: String, val icon: ImageVector, val label: String, val labelKn: String)

val bottomNavItems = listOf(
    NavItem("dashboard", Icons.Default.Dashboard, "Dashboard", "ಡ್ಯಾಶ್"),
    NavItem("input", Icons.Default.Edit, "Input", "ಇನ್ಪುಟ್"),
    NavItem("chat", Icons.Default.Chat, "Sahaya", "ಸಹಾಯ"),
    NavItem("calendar", Icons.Default.CalendarMonth, "Calendar", "ಕ್ಯಾ"),
    NavItem("trends", Icons.Default.ShowChart, "Trends", "ಟ್ರೆಂಡ್"),
    NavItem("history", Icons.Default.Grass, "Crops", "ಬೆಳೆ")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    repository: FarmerRepository,
    calculateSowingIndex: CalculateSowingIndexUseCase,
    generateActionPlan: GenerateActionPlanUseCase,
    navController: androidx.navigation.NavHostController,
    requestLocationPermission: (() -> Unit)? = null,
    onThemeChanged: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var showKannadaLabels by remember { mutableStateOf(LanguageManager.isKannada(navController.context)) }

    val toggleTheme = {
        ThemeManager.setTheme(context, !ThemeManager.isDarkTheme(context))
        onThemeChanged()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Raitha Bharosa Hub") },
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate("settings")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = {
                            showKannadaLabels = !showKannadaLabels
                            LanguageManager.setLanguage(
                                navController.context, 
                                if (showKannadaLabels) LanguageManager.LANGUAGE_KANNADA else LanguageManager.LANGUAGE_ENGLISH
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Toggle Language",
                            tint = GreenGo
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route ?: ""
            
            val showBottomNav = currentRoute != "onboarding" && currentRoute.isNotEmpty() && currentRoute != "settings"
            
            if (showBottomNav) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.route
NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { 
                                Text(
                                    if (showKannadaLabels) item.labelKn else item.label,
                                    maxLines = 1,
                                    style = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                                ) 
                            },
                            selected = selected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo("dashboard") { saveState = true }
                                        restoreState = true
                                    }
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                unselectedIconColor = Color.Gray,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = GreenGo
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        AppNavigation(
            navController = navController,
            repository = repository,
            calculateSowingIndex = calculateSowingIndex,
            generateActionPlan = generateActionPlan,
            showKannadaLabels = showKannadaLabels,
            onThemeToggle = toggleTheme,
            modifier = Modifier.padding(padding)
        )
    }
}

@Composable
private fun RecomposeTheme(
    key: Int,
    repository: FarmerRepository,
    calculateSowingIndex: CalculateSowingIndexUseCase,
    generateActionPlan: GenerateActionPlanUseCase,
    navController: androidx.navigation.NavHostController,
    requestLocationPermission: (() -> Unit)? = null,
    onThemeChanged: () -> Unit
) {
    androidx.compose.runtime.key(key) {
        val isDarkTheme = ThemeManager.isDarkTheme(androidx.compose.ui.platform.LocalContext.current)
        
        RaithaBharosaHubTheme(darkTheme = isDarkTheme) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                MainApp(
                    repository = repository,
                    calculateSowingIndex = calculateSowingIndex,
                    generateActionPlan = generateActionPlan,
                    navController = navController,
                    requestLocationPermission = requestLocationPermission,
                    onThemeChanged = onThemeChanged
                )
            }
        }
    }
}