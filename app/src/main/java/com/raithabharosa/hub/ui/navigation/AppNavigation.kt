package com.raithabharosa.hub.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.raithabharosa.hub.data.repository.FarmerRepository
import com.raithabharosa.hub.domain.usecase.CalculateSowingIndexUseCase
import com.raithabharosa.hub.domain.usecase.GenerateActionPlanUseCase
import com.raithabharosa.hub.ui.screens.calendar.CalendarScreen
import com.raithabharosa.hub.ui.screens.dashboard.DashboardScreen
import com.raithabharosa.hub.ui.screens.history.HistoryScreen
import com.raithabharosa.hub.ui.screens.input.InputScreen
import com.raithabharosa.hub.ui.screens.onboarding.OnboardingScreen
import com.raithabharosa.hub.ui.screens.settings.SettingsScreen
import com.raithabharosa.hub.ui.screens.trends.TrendScreen
import com.raithabharosa.hub.ThemeManager
import com.raithabharosa.hub.data.local.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking

@Composable
fun AppNavigation(
    navController: NavHostController,
    repository: FarmerRepository,
    calculateSowingIndex: CalculateSowingIndexUseCase,
    generateActionPlan: GenerateActionPlanUseCase,
    showKannadaLabels: Boolean = false,
    onThemeToggle: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var hasFarmer by remember { 
        mutableStateOf(
            runBlocking(Dispatchers.IO) {
                repository.getCurrentFarmer().first() != null
            }
        )
    }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        repository.getCurrentFarmer().collect { farmer ->
            hasFarmer = farmer != null
        }
    }

    val startDestination = if (isLoading) Screen.Onboarding.route 
                          else if (hasFarmer) Screen.Dashboard.route 
                          else Screen.Onboarding.route

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier.fillMaxSize()
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                repository = repository,
                onComplete = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                repository = repository,
                calculateSowingIndex = calculateSowingIndex,
                showKannadaLabels = showKannadaLabels
            )
        }

        composable(Screen.Input.route) {
            InputScreen(
                repository = repository,
                showKannadaLabels = showKannadaLabels
            )
        }

        composable(Screen.Calendar.route) {
            CalendarScreen(
                repository = repository,
                generateActionPlan = generateActionPlan,
                showKannadaLabels = showKannadaLabels
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                repository = repository,
                showKannadaLabels = showKannadaLabels
            )
        }

        composable(Screen.Trends.route) {
            TrendScreen(
                repository = repository,
                showKannadaLabels = showKannadaLabels
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                repository = repository,
                showKannadaLabels = showKannadaLabels,
                onThemeToggle = { onThemeToggle?.invoke() },
                onBackClick = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}