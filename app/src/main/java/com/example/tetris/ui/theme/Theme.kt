package com.example.tetris.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = AccentColor,
    background = MenuBackground,
    surface = BoardBackground,
    onPrimary = MenuBackground,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
)

@Composable
fun InfiniteTetrisTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
