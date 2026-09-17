package com.bk.mmovies.tv.ui.auth.preview.localization.he

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bk.mmovies.tv.ui.auth.ApiKeySetupTvScreen
import com.bk.mmovies.ui.theme.MMoviesTheme

@Preview(device = "id:tv_1080p", locale = "iw", name = "Api Key Setup - Hebrew (RTL)")
@Composable
private fun ApiKeySetupTvScreenHePreview() {
    MMoviesTheme {
        Scaffold(
                containerColor = MaterialTheme.colorScheme.background
                ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                ApiKeySetupTvScreen()
            }
        }
    }
}
