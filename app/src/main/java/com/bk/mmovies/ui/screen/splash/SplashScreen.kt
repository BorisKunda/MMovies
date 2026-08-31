package com.bk.mmovies.ui.screen.splash

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bk.mmovies.R
import com.bk.mmovies.connectivity.openDeviceInternetSettings
import com.bk.mmovies.connectivity.openWebPage
import com.bk.mmovies.data.source.remote.TMDB_WEBPAGE_API_GETTING_STARTED_URL
import com.bk.mmovies.data.source.remote.TMDB_WEBPAGE_API_SETTINGS_URL
import com.bk.mmovies.ui.component.InvalidApiKeyView
import com.bk.mmovies.ui.component.LoaderView
import com.bk.mmovies.ui.component.NoInternetView
import com.bk.mmovies.ui.component.PoweredByTmdbFooter
import com.bk.mmovies.util.logDebug

private const val TAG = "SplashScreen"

@Composable
fun SplashScreen(
        onNavigateToAuthScreen: () -> Unit,
        onNavigateToMoviesScreen: () -> Unit,
        onNavigateToTerms: () -> Unit = {}
                ) {
    val context = LocalContext.current
    val viewModel: SplashViewModel = hiltViewModel()
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

    Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
       ) {
        when (screenState) {
            SplashScreenState.Loading                                           -> {
                LoaderView()
            }

            SplashScreenState.Offline                                           -> {
                NoInternetView {
                    openDeviceInternetSettings(context)
                }
            }

            SplashScreenState.MissingApiKey -> {
                InvalidApiKeyView(
                        onOpenTmdbSettingsClicked = {
                            openWebPage(
                                    context,
                                    TMDB_WEBPAGE_API_SETTINGS_URL
                                       )
                        },
                        onApiKeyHelpClicked = {
                            openWebPage(
                                    context,
                                    TMDB_WEBPAGE_API_GETTING_STARTED_URL
                                       )
                        },
                        onSaveClicked = { key: String ->
                            viewModel.saveApiKey(key)
                            logDebug(
                                    TAG,
                                    "onSaveClicked key present: ${key.isNotBlank()}, length: ${key.length}"
                                    )
                        },
                        missingKey = true
                                 )
            }
        }

        PoweredByTmdbFooter(
                modifier = Modifier.align(Alignment.BottomCenter),
                onClick = onNavigateToTerms
                           )
    }

    LaunchedEffect(Unit) {
        logDebug(
                TAG,
                "LAUNCHED"
                )
    }

    LaunchedEffect(Unit) {
        viewModel.goToAuthEvent.collect {
            onNavigateToAuthScreen()
        }
    }
    LaunchedEffect(Unit) {
        viewModel.goToMoviesEvent.collect {
            onNavigateToMoviesScreen()
        }
    }
    val invalidApiKeyToastMessage = stringResource(R.string.toast_invalid_api_key)
    LaunchedEffect(Unit) {
        viewModel.invalidApiKeyToastEvent.collect {
            Toast.makeText(
                    context,
                    invalidApiKeyToastMessage,
                    Toast.LENGTH_SHORT
                          )
                    .show()
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

