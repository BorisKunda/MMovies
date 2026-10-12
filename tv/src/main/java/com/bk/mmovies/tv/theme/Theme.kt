package com.bk.mmovies.tv.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bk.mmovies.ui.theme.Gray300

private val DarkBackground = Color(0xFF1C1B1F)

private val TvColorScheme = darkColorScheme(
        primary = Gray300,
        onPrimary = DarkBackground,
        background = DarkBackground,
        surface = DarkBackground,
                                            )

@Composable
fun MMoviesTvTheme(content: @Composable () -> Unit) {
    MaterialTheme(
            colorScheme = TvColorScheme,
            content = content
                 )
}

val tvHeaderIconSize = 20.dp

val tvHeaderFontStyle = TextStyle(
        fontSize = 14.sp,
        lineHeight = 14.sp,
        fontWeight = FontWeight.Bold
                                 )
