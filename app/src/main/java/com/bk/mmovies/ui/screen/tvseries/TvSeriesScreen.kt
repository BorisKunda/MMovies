package com.bk.mmovies.ui.screen.tvseries

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.bk.mmovies.R

@Composable
fun TvSeriesScreen() {
    Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
       ) {
        Text(
                text = stringResource(R.string.tv_series_coming_soon),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
    }
}
