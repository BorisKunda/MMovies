package com.bk.mmovies.ui.screen.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bk.mmovies.connectivity.openDeviceInternetSettings
import com.bk.mmovies.connectivity.openWebPage
import com.bk.mmovies.ui.component.InvalidApiKeyView
import com.bk.mmovies.ui.component.LoaderView
import com.bk.mmovies.ui.component.NoInternetView
import com.bk.mmovies.util.logDebug

private const val TAG = "SplashScreen"

@Composable
fun SplashScreen(
        onNavigateToMovies: () -> Unit,
                ) {
    val context = LocalContext.current
    val viewModel: SplashViewModel = hiltViewModel()
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

    Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
       ) {
        when (screenState) {
            SplashScreenState.Loading          -> {
                LoaderView()
            }

            SplashScreenState.Offline          -> {
                NoInternetView {
                    openDeviceInternetSettings(context)
                }
            }

            is SplashScreenState.InvalidApiKey -> {
                InvalidApiKeyView(
                        onOpenTmdbSettingsClicked = {
                            openWebPage(
                                    context,
                                    "https://www.themoviedb.org/settings/api"
                                       )
                        },
                        onApiKeyHelpClicked = {
                            openWebPage(
                                    context,
                                    "https://developer.themoviedb.org/docs/getting-started"
                                       )
                        },
                        onSaveClicked = { key: String ->
                            viewModel.saveApiKey(key)
                            logDebug(
                                    TAG,
                                    "onSaveClicked key: $key"
                                    )
                        },
                        false
                                 )
            }

            SplashScreenState.MissingApiKey    -> {
                InvalidApiKeyView(
                        onOpenTmdbSettingsClicked = {
                            openWebPage(
                                    context,
                                    "https://www.themoviedb.org/settings/api"
                                       )
                        },
                        onApiKeyHelpClicked = {
                            openWebPage(
                                    context,
                                    "https://developer.themoviedb.org/docs/getting-started"
                                       )
                        },
                        onSaveClicked = { key: String ->
                            viewModel.saveApiKey(key)
                            logDebug(
                                    TAG,
                                    "onSaveClicked key: $key"
                                    )
                        },
                        true
                                 )
            }
        }
    }

    LaunchedEffect(Unit) {
        logDebug(
                TAG,
                "LAUNCHED"
                )
    }

    LaunchedEffect(Unit) {
        viewModel.goToMoviesEvent.collect {
            onNavigateToMovies()
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            logDebug(
                    TAG,
                    "DISPOSED"
                    )
        }
    }
}

