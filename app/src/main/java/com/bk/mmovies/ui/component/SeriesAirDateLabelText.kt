package com.bk.mmovies.ui.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.bk.mmovies.domain.model.SeriesAirDateLabel

// Shared by the catalog list row and the search result row so a TV series'
// air-date status (Ended/Ongoing/Upcoming) reads identically everywhere,
// rather than each screen falling back to the raw unformatted API date.
@Composable
fun SeriesAirDateLabelText(
        label: SeriesAirDateLabel,
        color: Color,
        style: TextStyle,
        modifier: Modifier = Modifier
                          ) {
    when (label) {
        is SeriesAirDateLabel.Ended    -> {
            if (label.yearRange.isNotBlank()) {
                Text(text = label.yearRange, color = color, style = style, modifier = modifier)
            }
        }
        is SeriesAirDateLabel.Ongoing  -> {
            if (label.text.isNotBlank()) {
                Text(text = label.text, color = color, style = style, modifier = modifier)
            }
        }
        is SeriesAirDateLabel.Upcoming -> {
            Text(text = label.premiereLabel, color = color, style = style, modifier = modifier)
            Text(text = label.dateText, color = color, style = style, modifier = modifier)
        }
    }
}
