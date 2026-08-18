package com.bk.mmovies.ui.screen.auth

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bk.mmovies.connectivity.openWebPage
import com.bk.mmovies.data.source.remote.TMDB_WEBPAGE_SIGNUP_URL
import com.bk.mmovies.ui.component.LoaderView
import com.bk.mmovies.ui.screen.auth.screencomponents.AuthWelcomeScreen
import com.bk.mmovies.util.logDebug

private const val TAG = "AuthScreen"


@Composable
fun AuthScreen(onNavigateToMoviesScreen: () -> Unit) {
    val context = LocalContext.current
    val authViewModel = hiltViewModel<AuthViewModel>()
    val state by authViewModel.authScreenState.collectAsStateWithLifecycle()
    when (val currentState = state) {
        AuthScreenState.Unauthenticated, is AuthScreenState.Error -> {
            if (currentState is AuthScreenState.Error) {
                LaunchedEffect(currentState) {
                    Toast.makeText(
                            context,
                            currentState.errorMessage,
                            Toast.LENGTH_SHORT
                                  )
                            .show()
                }
            }
            AuthWelcomeScreen(
                    onLoginClicked = { username, password ->
                        authViewModel.onLoginClicked(username, password)
                    },
                    onRegisterOnTmdbClicked = {
                        openWebPage(
                                context,
                                TMDB_WEBPAGE_SIGNUP_URL
                                   )
                    },
                    onContinueAsGuestClicked = {
                        authViewModel.onContinueAsGuestClicked()
                    }
                             )
        }
        AuthScreenState.Loading                                   -> {
            LoaderView()
        }

    }
    LaunchedEffect(Unit) {
        authViewModel.goToMoviesNavEvent.collect {
            onNavigateToMoviesScreen()
        }
    }

    DisposableEffect(Unit) {
        logDebug(
                TAG,
                "Composed"
                )
        onDispose {
            logDebug(
                    TAG,
                    "Disposed"
                    )
        }
    }
}







