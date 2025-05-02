package com.example.localert_app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.localert_app.ui.screen.*

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Notes : Screen("notes")
    object LocationReminder : Screen("location_reminder")
    object TimeReminder : Screen("time_reminder")
    object Settings : Screen("settings")
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToNotes = { navController.navigate(Screen.Notes.route) },
                onNavigateToLocationReminder = { navController.navigate(Screen.LocationReminder.route) },
                onNavigateToTimeReminder = { navController.navigate(Screen.TimeReminder.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }
        composable(Screen.Notes.route) {
            NotesScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Screen.LocationReminder.route) {
            LocationReminderScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Screen.TimeReminder.route) {
            TimeReminderScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Screen.Settings.route) {
            SettingsScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
} 