package com.example.localert_app.ui.screen

import android.content.Intent
import android.media.RingtoneManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.localert_app.ui.viewmodel.SettingsViewModel
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.painterResource
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import com.example.localert_app.R
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.runtime.SideEffect


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val systemUiController = rememberSystemUiController()
    val statusBarColor = Color(0xFF2196F3)
    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = false
        )
    }
    val context = LocalContext.current
    val currentRingtone by viewModel.currentRingtone.collectAsState()

    val ringtonePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            result.data?.getParcelableExtra<android.net.Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)?.let { uri ->
                viewModel.setRingtoneUri(uri)
            }
        }
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
                    title = { Text("Settings", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_settings),
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
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.fillMaxSize()
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
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
                                text = "Reminder Ringtone",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = currentRingtone ?: "Default",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Black.copy(alpha = 0.85f)
                                )
                                Button(
                                    onClick = {
                                        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                                            putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
                                            putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Reminder Sound")
                                            putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, viewModel.getCurrentRingtoneUri())
                                            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
                                            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
                                        }
                                        ringtonePickerLauncher.launch(intent)
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Text("Change", color = MaterialTheme.colorScheme.onPrimary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
} 