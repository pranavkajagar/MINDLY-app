package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class MindlyGlassColors(
    val background: Color,
    val surface: Color,
    val glassCardBackground: Color,
    val glassCardBorder: Color,
    val glassCardBorderGlow: Color,
    val accentGlow: Color,
    val accentContainer: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val isDark: Boolean
)

val LocalMindlyColors = staticCompositionLocalOf {
    MindlyGlassColors(
        background = AmoledBlack,
        surface = DarkSurfaceNearBlack,
        glassCardBackground = DarkSurfaceGlass,
        glassCardBorder = PurpleBorderSubtle,
        glassCardBorderGlow = PurpleBorderGlow,
        accentGlow = PurpleGlowPrimary,
        accentContainer = PurpleGlowContainer,
        textPrimary = TextPrimaryDark,
        textSecondary = TextSecondaryDark,
        textMuted = TextMutedDark,
        isDark = true
    )
}

private val DarkColorScheme = darkColorScheme(
    primary = PurpleGlowPrimary,
    onPrimary = Color.White,
    primaryContainer = PurpleGlowContainer,
    onPrimaryContainer = PurpleGlowSecondary,
    secondary = PurpleGlowSecondary,
    onSecondary = Color.Black,
    tertiary = BlueAccentPrimary,
    background = AmoledBlack,
    onBackground = TextPrimaryDark,
    surface = DarkSurfaceNearBlack,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = TextSecondaryDark,
    outline = PurpleBorderSubtle,
    error = EmergencyRed,
    errorContainer = EmergencyRedContainer
)

private val LightColorScheme = lightColorScheme(
    primary = BlueAccentPrimary,
    onPrimary = Color.White,
    primaryContainer = BlueAccentContainer,
    onPrimaryContainer = BlueAccentSecondary,
    secondary = BlueAccentSecondary,
    onSecondary = Color.White,
    tertiary = PurpleGlowPrimary,
    background = LightBackground,
    onBackground = TextPrimaryLight,
    surface = PureWhite,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightSurfaceElevated,
    onSurfaceVariant = TextSecondaryLight,
    outline = BlueBorderSubtle,
    error = EmergencyRed,
    errorContainer = EmergencyRedLightContainer
)

@Composable
fun MindlyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val mindlyGlassColors = if (darkTheme) {
        MindlyGlassColors(
            background = AmoledBlack,
            surface = DarkSurfaceNearBlack,
            glassCardBackground = DarkSurfaceGlass,
            glassCardBorder = PurpleBorderSubtle,
            glassCardBorderGlow = PurpleBorderGlow,
            accentGlow = PurpleGlowPrimary,
            accentContainer = PurpleGlowContainer,
            textPrimary = TextPrimaryDark,
            textSecondary = TextSecondaryDark,
            textMuted = TextMutedDark,
            isDark = true
        )
    } else {
        MindlyGlassColors(
            background = LightBackground,
            surface = PureWhite,
            glassCardBackground = LightSurfaceGlass,
            glassCardBorder = BlueBorderSubtle,
            glassCardBorderGlow = BlueBorderGlow,
            accentGlow = BlueAccentPrimary,
            accentContainer = BlueAccentContainer,
            textPrimary = TextPrimaryLight,
            textSecondary = TextSecondaryLight,
            textMuted = TextMutedLight,
            isDark = false
        )
    }

    CompositionLocalProvider(LocalMindlyColors provides mindlyGlassColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
