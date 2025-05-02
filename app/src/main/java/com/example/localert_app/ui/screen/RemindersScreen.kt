package com.example.localert_app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.localert_app.data.entity.Reminder
import com.example.localert_app.ui.viewmodel.LocationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    onBackClick: () -> Unit,
    viewModel: LocationViewModel = hiltViewModel()
) {
    var showAddReminderDialog by remember { mutableStateOf(false) }
    var newReminderTitle by remember { mutableStateOf("") }
    var newReminderMessage by remember { mutableStateOf("") }
    var newReminderLatitude by remember { mutableStateOf("") }
    var newReminderLongitude by remember { mutableStateOf("") }
    var newReminderRadius by remember { mutableStateOf("100") }
    val reminders by viewModel.reminders.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Smart Reminders") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddReminderDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Reminder")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(reminders) { reminder ->
                ReminderCard(
                    reminder = reminder,
                    onDelete = { viewModel.deleteReminder(reminder) }
                )
            }
        }

        if (showAddReminderDialog) {
            AlertDialog(
                onDismissRequest = { showAddReminderDialog = false },
                title = { Text("Add New Location Reminder") },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newReminderTitle,
                            onValueChange = { newReminderTitle = it },
                            label = { Text("Reminder Title") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newReminderMessage,
                            onValueChange = { newReminderMessage = it },
                            label = { Text("Reminder Message") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newReminderLatitude,
                            onValueChange = { newReminderLatitude = it },
                            label = { Text("Latitude") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newReminderLongitude,
                            onValueChange = { newReminderLongitude = it },
                            label = { Text("Longitude") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newReminderRadius,
                            onValueChange = { newReminderRadius = it },
                            label = { Text("Radius (meters)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (newReminderTitle.isNotBlank() && 
                                newReminderMessage.isNotBlank() && 
                                newReminderLatitude.isNotBlank() && 
                                newReminderLongitude.isNotBlank() && 
                                newReminderRadius.isNotBlank()) {
                                try {
                                    viewModel.addReminder(
                                        title = newReminderTitle,
                                        message = newReminderMessage,
                                        latitude = newReminderLatitude.toDouble(),
                                        longitude = newReminderLongitude.toDouble(),
                                        radius = newReminderRadius.toFloat()
                                    )
                                    newReminderTitle = ""
                                    newReminderMessage = ""
                                    newReminderLatitude = ""
                                    newReminderLongitude = ""
                                    newReminderRadius = "100"
                                    showAddReminderDialog = false
                                } catch (e: NumberFormatException) {
                                    // Handle invalid number format
                                }
                            }
                        }
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddReminderDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderCard(
    reminder: Reminder,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = reminder.title ?: "Location Reminder",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = reminder.message,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Location: (${reminder.latitude}, ${reminder.longitude})",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Radius: ${reminder.radius}m",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Reminder")
            }
        }
    }
} 