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
import com.example.model.ThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF00E5FF),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF181E3D),
    onPrimaryContainer = Color(0xFF00E5FF),
    secondary = Color(0xFFFFB300),
    onSecondary = Color.Black,
    tertiary = Color(0xFFFF2A6D),
    background = GameBackgroundDark,
    surface = GameSurfaceDark,
    onBackground = Color(0xFFF0F4F8),
    onSurface = Color(0xFFF0F4F8),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0091EA),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE1E7F0),
    onPrimaryContainer = Color(0xFF0091EA),
    secondary = Color(0xFFFF8F00),
    onSecondary = Color.White,
    tertiary = Color(0xFFD81B60),
    background = GameBackgroundLight,
    surface = GameSurfaceLight,
    onBackground = Color(0xFF1A1C29),
    onSurface = Color(0xFF1A1C29),
)

@Composable
fun MyApplicationTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = false, // Set false to ensure high-contrast custom game palette
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }

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

