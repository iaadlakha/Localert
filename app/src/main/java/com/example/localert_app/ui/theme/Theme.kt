package com.example.localert_app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF1A1D29),      // Midnight Blue
    primaryContainer = Color(0xFF2A2D39), // Darker Midnight Blue
    secondary = Color(0xFF6C757D),    // Slate Gray
    secondaryContainer = Color(0xFF5A6268), // Darker Slate Gray
    tertiary = Color(0xFFD4AF37),     // Soft Gold
    tertiaryContainer = Color(0xFFBFA030), // Darker Soft Gold
    background = Color(0xFF1A1D29),   // Midnight Blue
    surface = Color(0xFF2A2D39),      // Darker Midnight Blue
    surfaceVariant = Color(0xFF3A3D49), // Lighter Surface
    onPrimary = Color(0xFFFAFAFA),    // Ivory White
    onSecondary = Color(0xFFFAFAFA),  // Ivory White
    onTertiary = Color(0xFF1A1D29),   // Midnight Blue
    onBackground = Color(0xFFFAFAFA), // Ivory White
    onSurface = Color(0xFFFAFAFA),    // Ivory White
    onSurfaceVariant = Color(0xFFE0E0E0), // Light Gray
    error = Color(0xFFA4161A),        // Carmine Red
    errorContainer = Color(0xFF8B1216) // Darker Carmine Red
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1A1D29),      // Midnight Blue
    primaryContainer = Color(0xFF2A2D39), // Darker Midnight Blue
    secondary = Color(0xFF6C757D),    // Slate Gray
    secondaryContainer = Color(0xFF5A6268), // Darker Slate Gray
    tertiary = Color(0xFFD4AF37),     // Soft Gold
    tertiaryContainer = Color(0xFFBFA030), // Darker Soft Gold
    background = Color(0xFFFAFAFA),   // Ivory White
    surface = Color(0xFFFFFFFF),      // Pure White
    surfaceVariant = Color(0xFFF5F5F5), // Light Gray
    onPrimary = Color(0xFFFAFAFA),    // Ivory White
    onSecondary = Color(0xFFFAFAFA),  // Ivory White
    onTertiary = Color(0xFF1A1D29),   // Midnight Blue
    onBackground = Color(0xFF121212), // Jet Black
    onSurface = Color(0xFF121212),    // Jet Black
    onSurfaceVariant = Color(0xFF444444), // Graphite
    error = Color(0xFFA4161A),        // Carmine Red
    errorContainer = Color(0xFF8B1216) // Darker Carmine Red
)

@Composable
fun LocalertTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}