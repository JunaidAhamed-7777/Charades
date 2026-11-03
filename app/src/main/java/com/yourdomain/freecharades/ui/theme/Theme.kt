package com.yourdomain.freecharades.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** The only palette owner. Screens consume Material semantic roles. */
object AppColors {
    val Background = Color(0xFF0E0E10)
    val Surface = Color(0xFF19191D)
    val RaisedSurface = Color(0xFF232328)
    val Foreground = Color(0xFFFFFFFF)
    val Muted = Color(0xFF9F9FA5)
    val Accent = Color(0xFF8FA7E8)
    val Outline = Color(0xFF46464E)
    val ImageScrim = Background.copy(alpha = .20f)
    val ImageLabelScrim = Background.copy(alpha = .75f)
}

private val DarkColors = darkColorScheme(
    primary = AppColors.Accent,
    onPrimary = AppColors.Background,
    primaryContainer = AppColors.RaisedSurface,
    onPrimaryContainer = AppColors.Foreground,
    secondary = AppColors.Accent,
    onSecondary = AppColors.Background,
    secondaryContainer = AppColors.RaisedSurface,
    onSecondaryContainer = AppColors.Foreground,
    tertiary = AppColors.Accent,
    background = AppColors.Background,
    onBackground = AppColors.Foreground,
    surface = AppColors.Surface,
    onSurface = AppColors.Foreground,
    surfaceVariant = AppColors.RaisedSurface,
    onSurfaceVariant = AppColors.Muted,
    outline = AppColors.Outline,
    outlineVariant = AppColors.Outline,
    error = AppColors.Foreground,
    onError = AppColors.Background,
    surfaceTint = AppColors.Surface
)

@Composable
fun FreeCharadesTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DarkColors, typography = Typography, shapes = Shapes, content = content)
}
