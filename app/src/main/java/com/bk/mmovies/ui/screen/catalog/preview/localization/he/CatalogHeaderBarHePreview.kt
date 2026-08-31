package com.bk.mmovies.ui.screen.catalog.preview.localization

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bk.mmovies.ui.screen.catalog.UserProfileUiState
import com.bk.mmovies.ui.screen.catalog.screencomponents.UserProfileBar
import com.bk.mmovies.ui.theme.HebrewPhoneSizePreviews
import com.bk.mmovies.ui.theme.MMoviesTheme

// Hebrew content too (not just the UI chrome) so the preview exercises real
// RTL text shaping for the display name.
private val previewUserProfileHebrew = UserProfileUiState(
        name = "אלכס מורגן",
        imageUrl = "https://image.tmdb.org/t/p/w185/8qXWJx6mxvXynMhmXPWXTLDdrUB.jpg",
        isGuest = false
                                                           )

@HebrewPhoneSizePreviews
@Composable
private fun CatalogHeaderBarPreviewHebrew() {
    MMoviesTheme {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                UserProfileBar(userProfileState = previewUserProfileHebrew, onLogout = {})
            }
        }
    }
}
