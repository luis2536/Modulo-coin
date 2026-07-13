package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val TacticalColorScheme = darkColorScheme(
    primary = NeonTeal,
    secondary = CardBackground,
    tertiary = BorderColor,
    background = Obsidian,
    surface = SlateGray,
    onPrimary = Obsidian,
    onSecondary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TacticalColorScheme,
        typography = Typography,
        content = content
    )
}
