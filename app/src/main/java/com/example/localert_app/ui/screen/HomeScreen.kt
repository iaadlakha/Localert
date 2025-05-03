package com.example.localert_app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.draw.shadow
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
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun DigitalClock() {
    var currentTime by remember { mutableStateOf(LocalTime.now()) }
    
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = LocalTime.now()
            delay(1000)
        }
    }
    
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val formattedTime = currentTime.format(timeFormatter)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .shadow(4.dp, shape = MaterialTheme.shapes.medium),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = formattedTime,
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToNotes: () -> Unit,
    onNavigateToLocationReminder: () -> Unit,
    onNavigateToTimeReminder: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val systemUiController = rememberSystemUiController()
    val colorScheme = MaterialTheme.colorScheme
    
    SideEffect {
        systemUiController.setStatusBarColor(
            color = colorScheme.primary,
            darkIcons = false
        )
    }

    val fabScale = remember { Animatable(0.8f) }
    LaunchedEffect(Unit) {
        fabScale.animateTo(1f, animationSpec = tween(600))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Localert",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_settings),
                            contentDescription = "Settings",
                            tint = colorScheme.tertiary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.primary,
                    titleContentColor = colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToNotes,
                modifier = Modifier.graphicsLayer(scaleX = fabScale.value, scaleY = fabScale.value),
                containerColor = colorScheme.tertiary,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 12.dp
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_notes),
                    contentDescription = "Add Note",
                    tint = colorScheme.onTertiary
                )
            }
        },
        containerColor = colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DigitalClock()
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )
                
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
                ) {
                    HomeFeatureCard(
                        title = "Notes",
                        description = "Create and manage your notes",
                        onClick = onNavigateToNotes,
                        color = colorScheme.primaryContainer,
                        iconRes = R.drawable.ic_notes
                    )
                }
                
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
                ) {
                    HomeFeatureCard(
                        title = "Location Reminders",
                        description = "Set reminders based on location",
                        onClick = onNavigateToLocationReminder,
                        color = colorScheme.secondaryContainer,
                        iconRes = R.drawable.ic_location
                    )
                }
                
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
                ) {
                    HomeFeatureCard(
                        title = "Time Reminders",
                        description = "Set reminders based on time",
                        onClick = onNavigateToTimeReminder,
                        color = colorScheme.tertiaryContainer,
                        iconRes = R.drawable.ic_time
                    )
                }
            }
        }
    }
}

@Composable
fun HomeFeatureCard(
    title: String,
    description: String,
    onClick: () -> Unit,
    color: Color,
    iconRes: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, shape = MaterialTheme.shapes.medium)
            .heightIn(min = 80.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = color
        ),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                )
            }
        }
    }
} 