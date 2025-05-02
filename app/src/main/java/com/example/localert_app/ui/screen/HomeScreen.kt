package com.example.localert_app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.widget.Toast

@Composable
fun HomeScreen(
    onNotesClick: () -> Unit,
    onLocationReminderClick: () -> Unit,
    onTimeReminderClick: () -> Unit,
    onSmartRemindersClick: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Localert",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = {
                try {
                    onNotesClick()
                } catch (e: Exception) {
                    Toast.makeText(context, "Error opening Notes: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Notes")
        }

        Button(
            onClick = {
                try {
                    onLocationReminderClick()
                } catch (e: Exception) {
                    Toast.makeText(context, "Error opening Location Reminders: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Location Reminders")
        }

        Button(
            onClick = {
                try {
                    onTimeReminderClick()
                } catch (e: Exception) {
                    Toast.makeText(context, "Error opening Time Reminders: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Time Reminders")
        }

        Button(
            onClick = {
                try {
                    onSmartRemindersClick()
                } catch (e: Exception) {
                    Toast.makeText(context, "Error opening Smart Reminders: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Smart Reminders")
        }
    }
} 