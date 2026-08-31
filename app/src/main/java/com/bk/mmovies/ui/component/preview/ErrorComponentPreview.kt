package com.bk.mmovies.ui.component.preview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bk.mmovies.R
import com.bk.mmovies.ui.component.ErrorView
import com.bk.mmovies.ui.component.InvalidApiKeyView
import com.bk.mmovies.ui.component.NoInternetView
import com.bk.mmovies.ui.component.PrimaryButton
import com.bk.mmovies.ui.theme.MMoviesTheme
import com.bk.mmovies.util.logDebug

@Preview(showBackground = true)
@Composable
private fun InvalidApiKeyViewPreview() {
    MMoviesTheme {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                InvalidApiKeyView(
                        onOpenTmdbSettingsClicked = {
                            logDebug(
                                    "InvalidApiKeyViewPreview",
                                    "onOpenTmdbSettingsClicked"
                                    )
                        },
                        onApiKeyHelpClicked = {
                            logDebug(
                                    "InvalidApiKeyViewPreview",
                                    "onApiKeyHelpClicked"
                                    )
                        },
                        onSaveClicked = { key: String ->
                            logDebug(
                                    "InvalidApiKeyViewPreview",
                                    "onSaveClicked key"
                                    )
                        },
                        false
                                 )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MissingApiKeyViewPreview() {
    MMoviesTheme {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                InvalidApiKeyView(
                        onOpenTmdbSettingsClicked = {
                            logDebug(
                                    "MissingApiKeyViewPreview",
                                    "onOpenTmdbSettingsClicked"
                                    )
                        },
                        onApiKeyHelpClicked = {
                            logDebug(
                                    "MissingApiKeyViewPreview",
                                    "onApiKeyHelpClicked"
                                    )
                        },
                        onSaveClicked = { key: String ->
                            logDebug(
                                    "MissingApiKeyViewPreview",
                                    "onSaveClicked key"
                                    )
                        },
                        true
                                 )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NoInternetViewPreview() {
    MMoviesTheme {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                NoInternetView {
                    logDebug(
                            "NoInternetViewPreview",
                            "onSettingsButtonClicked"
                            )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GenericErrorViewPreview() {
    MMoviesTheme {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
            ErrorView(
                    modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                    errorImageResId = R.drawable.ic_error,
                    errorTitle = "Error",
                    errorMessage = "Something went wrong..",
                    {
                        PrimaryButton(
                                imageResId = null,
                                label = "Try again",
                                {
                                    logDebug(
                                            "GenericErrorViewPreview",
                                            "Try again clicked"
                                            )
                                },
                                true
                                     )
                    },
                    useSpaceBetween = true,
                    tag = "GenericErrorViewPreview"
                     )
        }
    }
}
