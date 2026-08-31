package com.bk.mmovies.ui.screen.terms.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bk.mmovies.ui.screen.terms.TermsScreen
import com.bk.mmovies.ui.theme.MMoviesTheme
import com.bk.mmovies.ui.theme.PhoneSizePreviews

@PhoneSizePreviews
@Composable
private fun TermsScreenPreview() {
    MMoviesTheme {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                TermsScreen(onBack = {})
            }
        }
    }
}