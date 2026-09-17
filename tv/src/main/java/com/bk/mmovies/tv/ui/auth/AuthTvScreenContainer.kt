package com.bk.mmovies.tv.ui.auth

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bk.mmovies.AppDevice
import com.bk.mmovies.ui.component.LoaderView

// Mirrors app's AuthScreen wrapper: drives AuthTvScreen (UI-only) with
// AuthTvViewModel, toasting on error and navigating away on success.
@Composable
fun AuthTvScreenContainer(
        onNavigateToMoviesScreen: () -> Unit,
        viewModel: AuthTvViewModel = hiltViewModel()
                          ) {
    val context = LocalContext.current
    val state by viewModel.authScreenState.collectAsStateWithLifecycle()

    when (val currentState = state) {
        is AuthTvScreenState.Error -> {
            LaunchedEffect(currentState) {
                Toast.makeText(context, currentState.errorMessage, Toast.LENGTH_SHORT).show()
            }
            AuthTvScreen(
                    onLoginClicked = viewModel::onLoginClicked,
                    onContinueAsGuestClicked = viewModel::onContinueAsGuestClicked
                        )
        }
        AuthTvScreenState.Unauthenticated -> {
            AuthTvScreen(
                    onLoginClicked = viewModel::onLoginClicked,
                    onContinueAsGuestClicked = viewModel::onContinueAsGuestClicked
                        )
        }
        AuthTvScreenState.Loading -> {
            LoaderView(AppDevice.TV)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.goToMoviesNavEvent.collect { onNavigateToMoviesScreen() }
    }
}
