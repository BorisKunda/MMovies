package com.bk.mmovies.ui.screen.catalog.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bk.mmovies.ui.screen.catalog.UserProfileUiState
import com.bk.mmovies.ui.screen.catalog.screencomponents.UserProfileBar
import com.bk.mmovies.ui.theme.MMoviesTheme
import com.bk.mmovies.ui.theme.PhoneSizePreviews

private val previewUserProfile = UserProfileUiState(
        name = "Alex Morgan",
        imageUrl = "https://image.tmdb.org/t/p/w185/8qXWJx6mxvXynMhmXPWXTLDdrUB.jpg",
        isGuest = false
                                                    )

@PhoneSizePreviews
@Composable
private fun CatalogHeaderBarPreview() {
    CatalogHeaderBarPreviewFrame(previewUserProfile)
}

@Preview(showBackground = true, name = "Guest")
@Composable
private fun CatalogHeaderBarGuestPreview() {
    CatalogHeaderBarPreviewFrame(previewUserProfile.copy(isGuest = true))
}

@Composable
private fun CatalogHeaderBarPreviewFrame(userProfileState: UserProfileUiState) {
    MMoviesTheme {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                UserProfileBar(userProfileState = userProfileState, onLogout = {})
            }
        }
    }
}
