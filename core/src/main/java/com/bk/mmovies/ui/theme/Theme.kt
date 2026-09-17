package com.bk.mmovies.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
        primary = Gray300,
        onPrimary = DarkBackground,
        secondary = Gray400,
        tertiary = Gray500,
        background = DarkBackground,
        surface = DarkBackground,
        onSurface = Gray200,
        surfaceVariant = Gray800,
        onSurfaceVariant = Gray400,
        surfaceContainer = Gray900
                                             )

@Composable
fun MMoviesTheme(
        content: @Composable () -> Unit
                ) {
    MaterialTheme(
            colorScheme = DarkColorScheme,
            typography = Typography,
            content = content
                 )
}