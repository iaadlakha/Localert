package com.example.localert_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.localert_app.ui.screen.*
import com.example.localert_app.ui.theme.LocalertTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LocalertTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onNotesClick = { navController.navigate("notes") },
                onLocationReminderClick = { navController.navigate("location_reminder") },
                onTimeReminderClick = { navController.navigate("time_reminder") },
                onSmartRemindersClick = { navController.navigate("smart_reminder") }
            )
        }
        composable("notes") {
            NotesScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("location_reminder") {
            LocationReminderScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("time_reminder") {
            TimeReminderScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("smart_reminder") {
            SmartReminderScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}