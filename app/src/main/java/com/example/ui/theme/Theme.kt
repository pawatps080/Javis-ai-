package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val JarvisColorScheme = darkColorScheme(
    primary = JarvisCyan,
    onPrimary = JarvisDarkBg,
    primaryContainer = JarvisSurface,
    onPrimaryContainer = JarvisCyanLight,
    secondary = JarvisGold,
    onSecondary = JarvisDarkBg,
    secondaryContainer = JarvisSurfaceBright,
    onSecondaryContainer = JarvisTextGold,
    tertiary = JarvisCrimson,
    onTertiary = JarvisDarkBg,
    background = JarvisDarkBg,
    onBackground = JarvisTextPrimary,
    surface = JarvisPanelBg,
    onSurface = JarvisTextPrimary,
    surfaceVariant = JarvisSurface,
    onSurfaceVariant = JarvisTextSecondary,
    outline = JarvisBorder,
    outlineVariant = JarvisBorderMuted
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = JarvisColorScheme,
        typography = Typography,
        content = content
    )
}

