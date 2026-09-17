package com.bk.mmovies.tv.ui.splash.preview.localization.he

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bk.mmovies.tv.ui.splash.SplashScreenTv
import com.bk.mmovies.ui.theme.MMoviesTheme

@Preview(device = "id:tv_1080p", locale = "iw", name = "Splash - Hebrew (RTL)")
@Composable
private fun SplashScreenTvHePreview() {
    MMoviesTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            SplashScreenTv()
        }
    }
}
