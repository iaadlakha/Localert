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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToNotes: () -> Unit,
    onNavigateToLocationReminder: () -> Unit,
    onNavigateToTimeReminder: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val fabScale = remember { Animatable(0.8f) }
    LaunchedEffect(Unit) {
        fabScale.animateTo(1f, animationSpec = tween(600))
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Localert", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)) },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_settings),
                            contentDescription = "Settings",
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
                onClick = onNavigateToNotes,
                modifier = Modifier.graphicsLayer(scaleX = fabScale.value, scaleY = fabScale.value),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_notes),
                    contentDescription = "Add Note",
                    tint = Color.White
                )
            }
        },
        containerColor = Color.Transparent
    ) { padding ->
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
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(28.dp, Alignment.Top),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome to Localert!",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
                ) {
                    HomeFeatureCard(
                        title = "Notes",
                        description = "Create and manage your notes",
                        onClick = onNavigateToNotes,
                        color = Color(0xFF42A5F5),
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
                        color = Color(0xFF26C6DA),
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
                        color = Color(0xFF66BB6A),
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
            .shadow(12.dp, shape = MaterialTheme.shapes.large)
            .heightIn(min = 100.dp)
            .padding(vertical = 8.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.95f)),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                )
            }
        }
    }
} 