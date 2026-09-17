package com.bk.mmovies.tv.ui.splash

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bk.mmovies.AppDevice
import com.bk.mmovies.core.R
import com.bk.mmovies.tv.ui.auth.ApiKeySetupTvScreen
import com.bk.mmovies.ui.component.LoaderView
import com.bk.mmovies.ui.component.PoweredByTmdbFooter

@Composable
fun SplashScreenTv(
        onNavigateToAuthScreen: () -> Unit = {},
        onNavigateToMoviesScreen: () -> Unit = {},
        viewModel: SplashTvViewModel = hiltViewModel()
                  ) {
    val context = LocalContext.current
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

    // A Column with the content Box taking weight(1f) - not a Box overlaying
    // the footer at BottomCenter - so the footer always sits in the layout
    // flow below the content instead of drawn on top of it (see
    // AuthTvScreen's identical fix/comment).
    Column(
            modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
          ) {
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            when (screenState) {
                is SplashScreenState.Loading -> LoaderView(AppDevice.TV)
                is SplashScreenState.MissingApiKey -> ApiKeySetupTvScreen(onSaveClicked = viewModel::saveApiKey)
            }
        }

        PoweredByTmdbFooter(modifier = Modifier.padding(vertical = 10.dp))
    }

    LaunchedEffect(Unit) {
        viewModel.goToAuthEvent.collect { onNavigateToAuthScreen() }
    }
    LaunchedEffect(Unit) {
        viewModel.goToMoviesEvent.collect { onNavigateToMoviesScreen() }
    }
    val invalidApiKeyToastMessage = stringResource(R.string.toast_invalid_api_key)
    LaunchedEffect(Unit) {
        viewModel.invalidApiKeyToastEvent.collect {
            Toast.makeText(context, invalidApiKeyToastMessage, Toast.LENGTH_SHORT).show()
        }
    }
}
