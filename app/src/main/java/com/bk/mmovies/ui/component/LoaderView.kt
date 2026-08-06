package com.bk.mmovies.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.bk.mmovies.R
import com.bk.mmovies.util.logDebug

private const val TAG = "LoaderView"

@Composable
fun LoaderView() {
    val composition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(R.raw.loading_screen)
                                                )

    LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.padding(50.dp)
                   )

    LaunchedEffect(Unit) {
        logDebug(
                TAG,
                "LAUNCHED"
                )
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

@Preview
@Composable
private fun LoaderViewPreview() {
    LoaderView()
}