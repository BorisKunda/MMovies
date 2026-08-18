package com.bk.mmovies.ui.screen.auth.screencomponents

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.bk.mmovies.ui.theme.MMoviesTheme
import com.bk.mmovies.util.logDebug

@Preview(showBackground = true)
@Composable
private fun AuthScreenMainPreview() {
    MMoviesTheme {
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthScreenPreview() {
    MMoviesTheme {
        AuthWelcomeScreen(
                onLoginClicked = { username, password ->
                    logDebug(
                            "AuthScreenPreview",
                            "onLoginClicked username=$username password=$password"
                            )
                },
                onRegisterOnTmdbClicked = {
                    logDebug(
                            "AuthScreenPreview",
                            "onRegisterOnTmdbClicked"
                            )
                },
                onContinueAsGuestClicked = {
                    logDebug(
                            "AuthScreenPreview",
                            "onContinueAsGuestClicked"
                            )
                }
                         )
    }
}

@Preview(
        showBackground = true,
        locale = "iw",
        name = "Hebrew (RTL)"
        )
@Composable
private fun AuthScreenPreviewHebrew() {
    MMoviesTheme {
        AuthWelcomeScreen(
                onLoginClicked = { username, password ->
                    logDebug(
                            "AuthScreenPreviewHebrew",
                            "onLoginClicked username=$username password=$password"
                            )
                },
                onRegisterOnTmdbClicked = {
                    logDebug(
                            "AuthScreenPreviewHebrew",
                            "onRegisterOnTmdbClicked"
                            )
                },
                onContinueAsGuestClicked = {
                    logDebug(
                            "AuthScreenPreviewHebrew",
                            "onContinueAsGuestClicked"
                            )
                }
                         )
    }
}

@Preview(
        showBackground = true,
        locale = "ru",
        name = "Russian (long text)"
        )
@Composable
private fun AuthScreenPreviewRussian() {
    MMoviesTheme {
        AuthWelcomeScreen(
                onLoginClicked = { username, password ->
                    logDebug(
                            "AuthScreenPreviewRussian",
                            "onLoginClicked username=$username password=$password"
                            )
                },
                onRegisterOnTmdbClicked = {
                    logDebug(
                            "AuthScreenPreviewRussian",
                            "onRegisterOnTmdbClicked"
                            )
                },
                onContinueAsGuestClicked = {
                    logDebug(
                            "AuthScreenPreviewRussian",
                            "onContinueAsGuestClicked"
                            )
                }
                         )
    }
}
