package com.bk.mmovies.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
        primary = Purple80,
        secondary = PurpleGrey80,
        tertiary = Pink80,
        background = DarkBackground,
        surface = DarkBackground
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