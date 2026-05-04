package com.raithabharosa.hub.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val icon: ImageVector, val label: String) {
    data object Onboarding : Screen("onboarding", Icons.Default.Home, "Home")
    data object Dashboard : Screen("dashboard", Icons.Default.Dashboard, "Dashboard")
    data object Input : Screen("input", Icons.Default.Edit, "Input")
    data object Calendar : Screen("calendar", Icons.Default.CalendarMonth, "Calendar")
    data object History : Screen("history", Icons.Default.History, "History")
}

val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.Input,
    Screen.Calendar,
    Screen.History
)