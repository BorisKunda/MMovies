package com.bk.mmovies.tv.ui.component

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

/**
 * A plain-text "Powered by X" footer for providers with no logo asset in
 * this module (Guardian, Gemini) - [PoweredByTmdbFooter] stays TMDB-specific
 * (it renders TMDB's real logo image), this is the fallback for everyone
 * else, always clickable through to that provider's terms/privacy page.
 */
@Composable
fun TvProviderFooter(
        label: String,
        modifier: Modifier = Modifier
                    ) {
    Row(
            modifier = modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .focusable(false)
                    .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
       ) {
        Text(
                text = label,
                color = Color.White,
                textDecoration = TextDecoration.Underline,
                style = MaterialTheme.typography.bodySmall
            )
    }
}
