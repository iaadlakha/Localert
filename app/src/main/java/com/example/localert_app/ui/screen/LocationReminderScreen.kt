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
import androidx.compose.material.icons.filled.Edit
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
    var showMap by remember { mutableStateOf(false) }
    var showAddReminderDialog by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newMessage by remember { mutableStateOf("") }
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var selectedRadius by remember { mutableStateOf(100f) }
    val context = LocalContext.current

    val reminders by viewModel.reminders.collectAsState()
    val editingReminder by viewModel.editingReminder.collectAsState()
    val locationReminders = reminders.filter { it.isLocationBased }

    // Reset form when editing reminder changes
    LaunchedEffect(editingReminder) {
        if (editingReminder != null) {
            newTitle = editingReminder!!.title
            newMessage = editingReminder!!.message
            selectedLocation = LatLng(editingReminder!!.latitude!!, editingReminder!!.longitude!!)
            selectedRadius = editingReminder!!.radius ?: 100f
            showAddReminderDialog = true
        }
    }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationGranted = permissions.entries.all { it.value }
        if (locationGranted) {
            showMap = true
        } else {
            Toast.makeText(context, "Location permission required", Toast.LENGTH_SHORT).show()
        }
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
                    IconButton(onClick = { 
                        newTitle = ""
                        newMessage = ""
                        selectedLocation = null
                        selectedRadius = 100f
                        showMap = true 
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Reminder")
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
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(
                        selectedLocation ?: LatLng(0.0, 0.0),
                        15f
                    )
                }

                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    cameraPositionState = cameraPositionState,
                    onMapClick = { latLng ->
                        selectedLocation = latLng
                        showAddReminderDialog = true
                    }
                ) {
                    selectedLocation?.let { location ->
                        Marker(
                            state = MarkerState(position = location),
                            title = "Selected Location"
                        )
                        Circle(
                            center = location,
                            radius = selectedRadius.toDouble(),
                            fillColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            strokeColor = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(locationReminders) { reminder ->
                    LocationReminderItem(
                        reminder = reminder,
                        onEdit = { viewModel.startEditing(reminder) },
                        onDelete = { viewModel.deleteReminder(reminder) }
                    )
                }
            }
        }

        if (showAddReminderDialog) {
            AlertDialog(
                onDismissRequest = { 
                    showAddReminderDialog = false
                    viewModel.cancelEditing()
                },
                title = { Text(if (editingReminder != null) "Edit Location Reminder" else "Add Location Reminder") },
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
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = selectedRadius.toString(),
                            onValueChange = { 
                                selectedRadius = it.toFloatOrNull() ?: 100f
                            },
                            label = { Text("Radius (meters)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (newTitle.isNotBlank() && selectedLocation != null) {
                                if (editingReminder != null) {
                                    viewModel.updateReminder(
                                        editingReminder!!.copy(
                                            title = newTitle,
                                            message = newMessage,
                                            latitude = selectedLocation!!.latitude,
                                            longitude = selectedLocation!!.longitude,
                                            radius = selectedRadius
                                        )
                                    )
                                } else {
                                    viewModel.insertReminder(
                                        Reminder(
                                            title = newTitle,
                                            message = newMessage,
                                            latitude = selectedLocation!!.latitude,
                                            longitude = selectedLocation!!.longitude,
                                            radius = selectedRadius,
                                            isLocationBased = true
                                        )
                                    )
                                }
                                newTitle = ""
                                newMessage = ""
                                selectedLocation = null
                                selectedRadius = 100f
                                showAddReminderDialog = false
                                showMap = false
                            }
                        }
                    ) {
                        Text(if (editingReminder != null) "Update" else "Add")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { 
                            showAddReminderDialog = false
                            viewModel.cancelEditing()
                        }
                    ) {
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
    onEdit: () -> Unit,
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
                text = "Radius: ${reminder.radius} meters",
                style = MaterialTheme.typography.bodySmall
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Reminder")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Reminder")
                }
            }
        }
    }
} 