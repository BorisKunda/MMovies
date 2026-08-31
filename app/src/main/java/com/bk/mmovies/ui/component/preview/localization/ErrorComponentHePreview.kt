package com.bk.mmovies.ui.component.preview.localization

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bk.mmovies.ui.component.InvalidApiKeyView
import com.bk.mmovies.ui.component.NoInternetView
import com.bk.mmovies.ui.theme.MMoviesTheme
import com.bk.mmovies.util.logDebug

@Preview(showBackground = true, locale = "iw", name = "Hebrew (RTL)")
@Composable
private fun MissingApiKeyViewPreviewHebrew() {
    MMoviesTheme {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                InvalidApiKeyView(
                        onOpenTmdbSettingsClicked = {
                            logDebug(
                                    "MissingApiKeyViewPreviewHebrew",
                                    "onOpenTmdbSettingsClicked"
                                    )
                        },
                        onApiKeyHelpClicked = {
                            logDebug(
                                    "MissingApiKeyViewPreviewHebrew",
                                    "onApiKeyHelpClicked"
                                    )
                        },
                        onSaveClicked = { key: String ->
                            logDebug(
                                    "MissingApiKeyViewPreviewHebrew",
                                    "onSaveClicked key"
                                    )
                        },
                        true
                                 )
            }
        }
    }
}

@Preview(showBackground = true, locale = "iw", name = "Hebrew (RTL)")
@Composable
private fun NoInternetViewPreviewHebrew() {
    MMoviesTheme {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                NoInternetView {
                    logDebug(
                            "NoInternetViewPreviewHebrew",
                            "onSettingsButtonClicked"
                            )
                }
            }
        }
    }
}
