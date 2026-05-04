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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(
    navController: NavHostController,
    repository: FarmerRepository,
    calculateSowingIndex: CalculateSowingIndexUseCase,
    generateActionPlan: GenerateActionPlanUseCase,
    showKannadaLabels: Boolean = false,
    modifier: Modifier = Modifier
) {
    var hasFarmer by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val farmer = repository.getCurrentFarmer().first()
                hasFarmer = farmer != null
                isLoading = false
            }
        } catch (e: Exception) {
            isLoading = false
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
            InputScreen(repository = repository)
        }

        composable(Screen.Calendar.route) {
            CalendarScreen(
                repository = repository,
                generateActionPlan = generateActionPlan
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(repository = repository)
        }
    }
}