package com.example.localert_app.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.localert_app.data.entity.Reminder
import com.example.localert_app.ui.viewmodel.ReminderViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationReminderScreen(
    onNavigateBack: () -> Unit,
    viewModel: ReminderViewModel = hiltViewModel()
) {
    var showAddReminderDialog by remember { mutableStateOf(false) }
    var showMap by remember { mutableStateOf(false) }
    var showRadiusSelector by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var selectedRadius by remember { mutableStateOf(100f) }
    var newTitle by remember { mutableStateOf("") }
    var newMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

    val reminders by viewModel.reminders.collectAsState()
    val locationReminders = reminders.filter { it.isLocationBased }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 2f)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Location Reminders") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showMap = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Location Reminder")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (showMap) {
            GoogleMap(
                modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    selectedLocation = latLng
                        showRadiusSelector = true
                }
            ) {
                selectedLocation?.let { location ->
                        Circle(
                            center = location,
                            radius = selectedRadius.toDouble(),
                            fillColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            strokeColor = MaterialTheme.colorScheme.primary
                        )
                    Marker(
                        state = MarkerState(position = location),
                        title = "Selected Location"
                    )
                }
            }

                if (showRadiusSelector) {
                    Column(
                modifier = Modifier
                    .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Adjust Radius: ${selectedRadius.toInt()}m")
                        Slider(
                            value = selectedRadius,
                            onValueChange = { selectedRadius = it },
                            valueRange = 50f..1000f,
                            steps = 19
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextButton(onClick = { 
                                showMap = false
                                showRadiusSelector = false
                                selectedLocation = null
                            }) {
                                Text("Cancel")
                            }
                            Button(onClick = { 
                                showMap = false
                                showRadiusSelector = false
                                showAddReminderDialog = true
                            }) {
                                Text("Continue")
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
            ) {
                items(locationReminders) { reminder ->
                    LocationReminderItem(
                        reminder = reminder,
                        onDelete = { viewModel.deleteReminder(reminder) }
                    )
                    }
                }
            }
        }

        if (showAddReminderDialog) {
            AlertDialog(
                onDismissRequest = { 
                    showAddReminderDialog = false
                    selectedLocation = null
                },
                title = { Text("Add Location Reminder") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = newTitle,
                            onValueChange = { newTitle = it },
                            label = { Text("Title") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newMessage,
                            onValueChange = { newMessage = it },
                            label = { Text("Message") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (newTitle.isNotBlank() && selectedLocation != null) {
                                viewModel.insertReminder(
                                    Reminder(
                                    title = newTitle,
                                    message = newMessage,
                                        latitude = selectedLocation?.latitude,
                                        longitude = selectedLocation?.longitude,
                                        radius = selectedRadius,
                                        isLocationBased = true
                                    )
                                )
                                newTitle = ""
                                newMessage = ""
                                selectedLocation = null
                                showAddReminderDialog = false
                            }
                        }
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { 
                        showAddReminderDialog = false
                        selectedLocation = null
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun LocationReminderItem(
    reminder: Reminder,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = reminder.title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = reminder.message,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Location: (${reminder.latitude}, ${reminder.longitude})",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Radius: ${reminder.radius?.toInt() ?: 0}m",
                style = MaterialTheme.typography.bodySmall
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Reminder")
                }
            }
        }
    }
} 