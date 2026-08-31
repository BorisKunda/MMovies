package com.bk.mmovies.ui.screen.auth.preview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bk.mmovies.ui.screen.auth.screencomponents.AuthWelcomeScreen
import com.bk.mmovies.ui.theme.MMoviesTheme
import com.bk.mmovies.ui.theme.PhoneSizePreviews
import com.bk.mmovies.util.logDebug

@PhoneSizePreviews
@Composable
private fun AuthScreenPreview() {
    MMoviesTheme {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
            Box(modifier = Modifier.padding(padding)) {
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
    }
}
