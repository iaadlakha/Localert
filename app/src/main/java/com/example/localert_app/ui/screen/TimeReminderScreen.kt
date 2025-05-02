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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
    onNavigateBack: () -> Unit,
    viewModel: ReminderViewModel = hiltViewModel()
) {
    var showAddReminderDialog by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newMessage by remember { mutableStateOf("") }
    var selectedDateTime by remember { mutableStateOf(Calendar.getInstance()) }
    val context = LocalContext.current

    val reminders by viewModel.reminders.collectAsState()
    val editingReminder by viewModel.editingReminder.collectAsState()
    val timeReminders = reminders.filter { !it.isLocationBased }

    // Reset form when editing reminder changes
    LaunchedEffect(editingReminder) {
        if (editingReminder != null) {
            newTitle = editingReminder!!.title
            newMessage = editingReminder!!.message
            selectedDateTime = Calendar.getInstance().apply {
                time = editingReminder!!.createdAt
            }
            showAddReminderDialog = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Time Reminders") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        newTitle = ""
                        newMessage = ""
                        selectedDateTime = Calendar.getInstance()
                        showAddReminderDialog = true 
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
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(timeReminders) { reminder ->
                    TimeReminderItem(
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
                title = { Text(if (editingReminder != null) "Edit Time Reminder" else "Add Time Reminder") },
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
                        Button(
                            onClick = {
                                val datePickerDialog = DatePickerDialog(
                                    context,
                                    { _: DatePicker, year: Int, month: Int, day: Int ->
                                        selectedDateTime.set(Calendar.YEAR, year)
                                        selectedDateTime.set(Calendar.MONTH, month)
                                        selectedDateTime.set(Calendar.DAY_OF_MONTH, day)
                                        TimePickerDialog(
                                            context,
                                            { _, hour: Int, minute: Int ->
                                                selectedDateTime.set(Calendar.HOUR_OF_DAY, hour)
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
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Select Date and Time")
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (newTitle.isNotBlank()) {
                                if (editingReminder != null) {
                                    viewModel.updateReminder(
                                        editingReminder!!.copy(
                                            title = newTitle,
                                            message = newMessage,
                                            createdAt = Date(selectedDateTime.timeInMillis)
                                        )
                                    )
                                } else {
                                    viewModel.insertReminder(
                                        Reminder(
                                            title = newTitle,
                                            message = newMessage,
                                            createdAt = Date(selectedDateTime.timeInMillis),
                                            isLocationBased = false
                                        )
                                    )
                                }
                                newTitle = ""
                                newMessage = ""
                                selectedDateTime = Calendar.getInstance()
                                showAddReminderDialog = false
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
fun TimeReminderItem(
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
                text = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                    .format(reminder.createdAt),
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