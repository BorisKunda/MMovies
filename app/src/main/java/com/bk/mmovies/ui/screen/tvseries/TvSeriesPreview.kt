package com.bk.mmovies.ui.screen.tvseries

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bk.mmovies.ui.theme.MMoviesTheme

@Preview(showBackground = true)
@Composable
private fun TvSeriesScreenPreview() {
    MMoviesTheme {
        Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background
                ) {
            TvSeriesScreen()
        }
    }
}
