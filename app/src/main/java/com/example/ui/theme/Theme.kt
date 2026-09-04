package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = Color(0xFF00363F),
    primaryContainer = Color(0xFF004E5B),
    onPrimaryContainer = Color(0xFF97F0FF),
    secondary = CyberCyanMuted,
    onSecondary = Color(0xFF00344F),
    secondaryContainer = Color(0xFF004C70),
    onSecondaryContainer = Color(0xFFCCE5FF),
    tertiary = AccentSuccess,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = TextPrimaryDark,
    surface = DarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    error = AccentDanger,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = CyberBlueDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC7E7FF),
    onPrimaryContainer = Color(0xFF001E2F),
    secondary = CyberCyanMuted,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD4F2FF),
    onSecondaryContainer = Color(0xFF001F29),
    tertiary = AccentSuccess,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = Color(0xFF0F172A),
    surface = LightSurface,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF475569),
    error = AccentDanger,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek privacy cyber dark theme
    dynamicColor: Boolean = false, // Keep high-contrast cyber theme consistent
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

