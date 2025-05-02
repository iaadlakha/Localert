package com.example.localert_app.ui.screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.localert_app.data.entity.Reminder
import com.example.localert_app.ui.viewmodel.ReminderViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeReminderScreen(
    onBackClick: () -> Unit,
    viewModel: ReminderViewModel = hiltViewModel()
) {
    var showAddReminderDialog by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newMessage by remember { mutableStateOf("") }
    var selectedDateTime by remember { mutableStateOf(Calendar.getInstance()) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Time Reminders") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val reminders by viewModel.reminders.collectAsState()
            val timeReminders = reminders.filter { !it.isLocationBased }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(timeReminders) { reminder ->
                    TimeReminderItem(
                        reminder = reminder,
                        onDelete = { viewModel.deleteReminder(reminder) }
                    )
                }
            }
        }

        if (showAddReminderDialog) {
            AlertDialog(
                onDismissRequest = { showAddReminderDialog = false },
                title = { Text("Add Time Reminder") },
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
                            value = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                                .format(selectedDateTime.time),
                            onValueChange = { },
                            label = { Text("Date and Time") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        val datePickerDialog = DatePickerDialog(
                                            context,
                                            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                                                selectedDateTime.set(year, month, dayOfMonth)
                                                // Show Time Picker after Date is selected
                                                TimePickerDialog(
                                                    context,
                                                    { _, hourOfDay: Int, minute: Int ->
                                                        selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                                        selectedDateTime.set(Calendar.MINUTE, minute)
                                                    },
                                                    selectedDateTime.get(Calendar.HOUR_OF_DAY),
                                                    selectedDateTime.get(Calendar.MINUTE),
                                                    true
                                                ).show()
                                            },
                                            selectedDateTime.get(Calendar.YEAR),
                                            selectedDateTime.get(Calendar.MONTH),
                                            selectedDateTime.get(Calendar.DAY_OF_MONTH)
                                        )
                                        datePickerDialog.show()
                                    }
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Select Date and Time")
                                }
                            }
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (newTitle.isNotBlank()) {
                                viewModel.insertReminder(
                                    Reminder(
                                        title = newTitle,
                                        message = newMessage,
                                        createdAt = Date(selectedDateTime.timeInMillis),
                                        isLocationBased = false
                                    )
                                )
                                newTitle = ""
                                newMessage = ""
                                selectedDateTime = Calendar.getInstance()
                                showAddReminderDialog = false
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

@Composable
fun TimeReminderItem(
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
                text = reminder.title ?: "Untitled",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = reminder.message,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                    .format(reminder.createdAt),
                style = MaterialTheme.typography.bodySmall
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDelete) {
                    Text("Delete")
                }
            }
        }
    }
} 