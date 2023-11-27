package com.pascalrieder.proteincounter.ui.theme

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
    // Primary colors
    primary = RallyGreen,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF61CEA4),
    onPrimaryContainer = Color(0xFF306650),

    // Secondary colors
    secondary = RallyDarkGreen,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF08A396),
    onSecondaryContainer = Color(0xFF034942),

    // Tertiary colors
    tertiary = RallyOrange,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFF8B7D),
    onTertiaryContainer = Color(0xFFD83220),


    // Error colors
    error = Color.Red,

    // Background and Surface colors
    background = Color(0xFF373740),
    onBackground = Color.White,
    surface = Color(0xFF373740),
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(

)

@Composable
fun ProteinCounterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}