package com.bk.mmovies.ui.theme

import androidx.compose.ui.graphics.Color

// Material Design grey palette (m3.material.io) — standard tonal shades.
val Gray200 = Color(0xFFEEEEEE)
val Gray300 = Color(0xFFE0E0E0)
val Gray400 = Color(0xFFBDBDBD)
val Gray500 = Color(0xFF9E9E9E)
val Gray600 = Color(0xFF757575)
val Gray700 = Color(0xFF616161)
val Gray800 = Color(0xFF424242)
val Gray900 = Color(0xFF212121)

val DarkBackground = Color(0xFF1C1B1F)

// The raised surface used by movie cards, their loading placeholders and the
// score badge — previously duplicated as a literal in three separate files.
// Sits between Gray800 and Gray900: darker than Gray800, but still clearly
// lighter than DarkBackground so cards stay visible.
val CardSurface = Color(0xFF383838)

val primaryButtonContainerColor = Color.White
val primaryButtonContentColor = Color(0xFF1C1B1F)
val primaryButtonDisabledContainerColor = primaryButtonContainerColor.copy(alpha = 0.4f)
val primaryButtonDisabledContentColor = primaryButtonContentColor.copy(alpha = 0.5f)