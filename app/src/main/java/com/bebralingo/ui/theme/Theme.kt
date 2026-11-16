package com.bebralingo.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.sp

/**
 * Светлая и тёмная цветовые схемы Material 3.
 * Dynamic color отключён по умолчанию, чтобы сохранить фирменную палитру Bebralingo.
 */

private val LightColorScheme = lightColorScheme(
    primary = Green,
    secondary = Blue,
    tertiary = Orange,
    error = Red,
    surface = SurfaceLight,
    background = BackgroundLight,
    surfaceVariant = Color(0xFFF0F0F0),
    onSurface = TextPrimary,
    onBackground = TextPrimary,
    surfaceContainerHighest = Color(0xFFE8E8E8)
)

private val DarkColorScheme = darkColorScheme(
    primary = Green,
    secondary = Blue,
    tertiary = Orange,
    error = Red,
    surface = SurfaceDark,
    background = BackgroundDark,
    surfaceVariant = SurfaceDarkElevated,
    onSurface = TextOnDark,
    onBackground = TextOnDark,
    surfaceContainerHighest = SurfaceDarkElevated
)

/**
 * Основной Theme-композируемый.
 * При желании можно включить dynamicColor = true для Material You.
 */
@Composable
fun BebralingoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && darkTheme -> {
            dynamicDarkColorScheme(LocalContext.current)
        }
        dynamicColor && !darkTheme -> {
            dynamicLightColorScheme(LocalContext.current)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
