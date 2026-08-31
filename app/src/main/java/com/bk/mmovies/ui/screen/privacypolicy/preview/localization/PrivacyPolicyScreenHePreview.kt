package com.bk.mmovies.ui.screen.privacypolicy.preview.localization

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bk.mmovies.ui.screen.privacypolicy.PrivacyPolicyScreen
import com.bk.mmovies.ui.theme.HebrewPhoneSizePreviews
import com.bk.mmovies.ui.theme.MMoviesTheme

@HebrewPhoneSizePreviews
@Composable
private fun PrivacyPolicyScreenPreviewHebrew() {
    MMoviesTheme {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                PrivacyPolicyScreen(onBack = {})
            }
        }
    }
}
