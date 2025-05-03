package com.example.localert_app.ui.screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.localert_app.data.entity.Reminder
import com.example.localert_app.ui.viewmodel.ReminderViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.painterResource
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.graphicsLayer
import com.example.localert_app.R
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.runtime.SideEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeReminderScreen(
    onNavigateBack: () -> Unit,
    viewModel: ReminderViewModel = hiltViewModel()
) {
    val systemUiController = rememberSystemUiController()
    val statusBarColor = Color(0xFF2196F3)
    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = false
        )
    }
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

    val fabScale = remember { Animatable(0.8f) }
    LaunchedEffect(Unit) {
        fabScale.animateTo(1f, animationSpec = tween(600))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2196F3), // Blue
                        Color(0xFF21CBF3)  // Light Blue
                    )
                )
            )
    ) {
    Scaffold(
        topBar = {
            TopAppBar(
                    title = { Text("Time Reminders", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.primary
                            )
                    }
                },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        newTitle = ""
                        newMessage = ""
                        selectedDateTime = Calendar.getInstance()
                        showAddReminderDialog = true
                    },
                    modifier = Modifier.graphicsLayer(scaleX = fabScale.value, scaleY = fabScale.value),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_time),
                        contentDescription = "Add Reminder",
                        tint = Color.White
                    )
                    }
            },
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(timeReminders) { reminder ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
                        ) {
                    TimeReminderItem(
                        reminder = reminder,
                                onEdit = { viewModel.startEditing(reminder) },
                        onDelete = { viewModel.deleteReminder(reminder) }
                    )
                        }
                    }
                }
            }
        }

        if (showAddReminderDialog) {
            AlertDialog(
                onDismissRequest = {
                    showAddReminderDialog = false
                    viewModel.cancelEditing()
                },
                shape = RoundedCornerShape(24.dp),
                title = { Text(if (editingReminder != null) "Edit Time Reminder" else "Add Time Reminder", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
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
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                            Text("Select Date and Time", color = MaterialTheme.colorScheme.onPrimary)
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
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = reminder.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = reminder.message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black.copy(alpha = 0.85f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                    .format(reminder.createdAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onEdit) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_edit_24),
                        contentDescription = "Edit Reminder",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_delete_24),
                        contentDescription = "Delete Reminder",
                        tint = Color.Red
                    )
                }
            }
        }
    }
} 