package com.example.localert_app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToNotes: () -> Unit,
    onNavigateToLocationReminder: () -> Unit,
    onNavigateToTimeReminder: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Localert") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
                .padding(padding)
            .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Notes Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = onNavigateToNotes
            ) {
                Column(
            modifier = Modifier
                .fillMaxWidth()
                        .padding(16.dp)
        ) {
                    Text(
                        text = "Notes",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Create and manage your notes",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Location Reminder Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = onNavigateToLocationReminder
            ) {
                Column(
            modifier = Modifier
                .fillMaxWidth()
                        .padding(16.dp)
        ) {
                    Text(
                        text = "Location Reminders",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Set reminders based on location",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Time Reminder Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = onNavigateToTimeReminder
            ) {
                Column(
            modifier = Modifier
                .fillMaxWidth()
                        .padding(16.dp)
        ) {
                    Text(
                        text = "Time Reminders",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Set reminders based on time",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
} 