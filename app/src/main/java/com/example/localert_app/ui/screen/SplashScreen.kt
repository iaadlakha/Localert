package com.example.localert_app.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 2000),
        label = "Alpha Animation"
    )
    val scaleAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.3f,
        animationSpec = tween(durationMillis = 1000),
        label = "Scale Animation"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2500)
        onSplashFinished()
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
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome to Localert!",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(alphaAnim.value)
                    .scale(scaleAnim.value)
            )
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator(
                modifier = Modifier
                    .size(48.dp)
                    .alpha(alphaAnim.value),
                color = Color.White
            )
        }
    }
} 