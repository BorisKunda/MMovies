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

@Preview(showBackground = true, locale = "ru", name = "Russian (long text)")
@Composable
private fun MissingApiKeyViewPreviewRussian() {
    MMoviesTheme {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                InvalidApiKeyView(
                        onOpenTmdbSettingsClicked = {
                            logDebug(
                                    "MissingApiKeyViewPreviewRussian",
                                    "onOpenTmdbSettingsClicked"
                                    )
                        },
                        onApiKeyHelpClicked = {
                            logDebug(
                                    "MissingApiKeyViewPreviewRussian",
                                    "onApiKeyHelpClicked"
                                    )
                        },
                        onSaveClicked = { key: String ->
                            logDebug(
                                    "MissingApiKeyViewPreviewRussian",
                                    "onSaveClicked key"
                                    )
                        },
                        true
                                 )
            }
        }
    }
}

@Preview(showBackground = true, locale = "ru", name = "Russian (long text)")
@Composable
private fun NoInternetViewPreviewRussian() {
    MMoviesTheme {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                NoInternetView {
                    logDebug(
                            "NoInternetViewPreviewRussian",
                            "onSettingsButtonClicked"
                            )
                }
            }
        }
    }
}
