package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkCyberColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color(0xFF00363F),
    primaryContainer = Color(0xFF004D5A),
    onPrimaryContainer = Color(0xFF99F6FF),
    secondary = NeonViolet,
    onSecondary = Color(0xFF381E72),
    secondaryContainer = Color(0xFF4F378B),
    onSecondaryContainer = Color(0xFFEADDFF),
    tertiary = NeonGreen,
    onTertiary = Color(0xFF003920),
    error = NeonRed,
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
    background = CyberNavyDark,
    onBackground = TextPrimary,
    surface = CyberNavy,
    onSurface = TextPrimary,
    surfaceVariant = CyberCharcoal,
    onSurfaceVariant = TextSecondary,
    outline = CyberSurfaceStroke
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek cybersecurity dark mode
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkCyberColorScheme,
        typography = Typography,
        content = content
    )
}
