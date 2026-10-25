package com.bk.mmovies.demotv.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.bk.mmovies.AppDevice
import com.bk.mmovies.ui.component.LoaderView
import com.bk.mmovies.ui.component.PoweredByTmdbFooter
import kotlinx.coroutines.delay

private const val SPLASH_DURATION_MS = 1500L

@Composable
fun SplashScreenTv(onNavigateToMoviesScreen: () -> Unit = {}) {
    Column(
            modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
          ) {
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            LoaderView(AppDevice.TV)
        }

        PoweredByTmdbFooter(modifier = Modifier.alpha(0f).padding(vertical = 10.dp))
    }

    LaunchedEffect(Unit) {
        delay(SPLASH_DURATION_MS)
        onNavigateToMoviesScreen()
    }
}
