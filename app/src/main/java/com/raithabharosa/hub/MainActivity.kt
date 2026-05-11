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
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Language
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = FarmerRepository(database)
        val calculateSowingIndex = CalculateSowingIndexUseCase()
        val generateActionPlan = GenerateActionPlanUseCase()
        
        checkLocationPermission()
        
        setContent {
            RaithaBharosaHubTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainApp(
                        repository = repository,
                        calculateSowingIndex = calculateSowingIndex,
                        generateActionPlan = generateActionPlan,
                        requestLocationPermission = { requestLocationPermission() }
                    )
                }
            }
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
    NavItem("calendar", Icons.Default.CalendarMonth, "Calendar", "ಕ್ಯಾಲೆಂಡರ್"),
    NavItem("trends", Icons.Default.ShowChart, "Trends", "ಟ್ರೆಂಡ್"),
    NavItem("history", Icons.Default.Grass, "My Crops", "ಬೆಳೆಗಳು")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    repository: FarmerRepository,
    calculateSowingIndex: CalculateSowingIndexUseCase,
    generateActionPlan: GenerateActionPlanUseCase,
    requestLocationPermission: (() -> Unit)? = null
) {
    val navController = rememberNavController()
    var showKannadaLabels by remember { mutableStateOf(LanguageManager.isKannada(navController.context)) }
    var isDarkTheme by remember { mutableStateOf(ThemeManager.isDarkTheme(navController.context)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Raitha Bharosa Hub") },
                actions = {
                    IconButton(onClick = { isDarkTheme = !isDarkTheme; ThemeManager.setTheme(navController.context, isDarkTheme) }) {
                        Icon(
                            imageVector = Icons.Default.Dashboard,
                            contentDescription = "Toggle Theme",
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
            
            val showBottomNav = currentRoute != "onboarding" && currentRoute.isNotEmpty()
            
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
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
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
                                selectedTextColor = Color.White,
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
            modifier = Modifier.padding(padding)
        )
    }
}