package com.bk.mmovies.ui.screen.catalog.preview.localization

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bk.mmovies.ui.screen.catalog.UserProfileUiState
import com.bk.mmovies.ui.screen.catalog.screencomponents.UserProfileBar
import com.bk.mmovies.ui.theme.MMoviesTheme
import com.bk.mmovies.ui.theme.RussianPhoneSizePreviews

// Russian content too, since the point of this one is length rather than
// direction: the display name is the only text this bar renders.
private val previewUserProfileRussian = UserProfileUiState(
        name = "Александра Морозова",
        imageUrl = "https://image.tmdb.org/t/p/w185/8qXWJx6mxvXynMhmXPWXTLDdrUB.jpg",
        isGuest = false
                                                            )

@RussianPhoneSizePreviews
@Composable
private fun CatalogHeaderBarPreviewRussian() {
    MMoviesTheme {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                UserProfileBar(userProfileState = previewUserProfileRussian, onLogout = {})
            }
        }
    }
}
