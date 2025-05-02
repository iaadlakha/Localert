package com.example.localert_app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.localert_app.ui.screen.HomeScreen
import com.example.localert_app.ui.screen.LocationReminderScreen
import com.example.localert_app.ui.screen.NotesScreen
import com.example.localert_app.ui.screen.RemindersScreen
import com.example.localert_app.ui.screen.TimeReminderScreen

@Composable
fun AppNavigation(navController: androidx.navigation.NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onNotesClick = { navController.navigate("notes") },
                onLocationReminderClick = { navController.navigate("locationReminder") },
                onTimeReminderClick = { navController.navigate("timeReminder") },
                onSmartRemindersClick = { navController.navigate("smartReminders") }
            )
        }
        composable("notes") {
            NotesScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("locationReminder") {
            LocationReminderScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("timeReminder") {
            TimeReminderScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("smartReminders") {
            RemindersScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
} 