package com.aetheris.android.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Aetheris Design System Colors
object AetherisColors {
    // Backgrounds
    val Background = Color(0xFF09090B)
    val Surface = Color(0xFF18181B)
    val SurfaceElevated = Color(0xFF27272A)
    val SurfaceHighest = Color(0xFF3F3F46)

    // Borders
    val Border = Color(0xFF3F3F46)
    val BorderSubtle = Color(0xFF27272A)

    // Text
    val TextPrimary = Color(0xFFFAFAFA)
    val TextSecondary = Color(0xFFA1A1AA)
    val TextMuted = Color(0xFF71717A)

    // Accent
    val Accent = Color(0xFF10B981)
    val AccentDarker = Color(0xFF059669)
    val AccentLight = Color(0xFF34D399)

    // Status
    val Success = Color(0xFF10B981)
    val Warning = Color(0xFFF59E0B)
    val Error = Color(0xFFEF4444)
    val Info = Color(0xFF3B82F6)

    // Light theme
    val LightBackground = Color(0xFFF8FAFC)
    val LightSurface = Color(0xFFFFFFFF)
    val LightSurfaceElevated = Color(0xFFF1F5F9)
    val LightBorder = Color(0xFFE2E8F0)
    val LightTextPrimary = Color(0xFF0F172A)
    val LightTextSecondary = Color(0xFF475569)
    val LightTextMuted = Color(0xFF94A3B8)
}

// Top-level color shortcuts for use outside of Composable context
val AetherisAccent = AetherisColors.Accent
val AetherisBackground = AetherisColors.Background
val AetherisSurface = AetherisColors.Surface

private val DarkColorScheme = darkColorScheme(
    primary = AetherisColors.Accent,
    onPrimary = Color.White,
    primaryContainer = AetherisColors.AccentDarker,
    onPrimaryContainer = Color.White,
    secondary = AetherisColors.Info,
    onSecondary = Color.White,
    background = AetherisColors.Background,
    onBackground = AetherisColors.TextPrimary,
    surface = AetherisColors.Surface,
    onSurface = AetherisColors.TextPrimary,
    surfaceVariant = AetherisColors.SurfaceElevated,
    onSurfaceVariant = AetherisColors.TextSecondary,
    error = AetherisColors.Error,
    onError = Color.White,
    outline = AetherisColors.Border,
    outlineVariant = AetherisColors.BorderSubtle,
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF059669),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1FAE5),
    onPrimaryContainer = Color(0xFF065F46),
    secondary = Color(0xFF2563EB),
    onSecondary = Color.White,
    background = AetherisColors.LightBackground,
    onBackground = AetherisColors.LightTextPrimary,
    surface = AetherisColors.LightSurface,
    onSurface = AetherisColors.LightTextPrimary,
    surfaceVariant = AetherisColors.LightSurfaceElevated,
    onSurfaceVariant = AetherisColors.LightTextSecondary,
    error = AetherisColors.Error,
    onError = Color.White,
    outline = AetherisColors.LightBorder,
    outlineVariant = Color(0xFFF1F5F9),
)

@Composable
fun AetherisTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AetherisTypography,
        content = content
    )
}
