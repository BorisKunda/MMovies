package com.bk.mmovies.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.bk.mmovies.AppDevice
import com.bk.mmovies.core.R

private val tvPadding = 150.dp
private val phonePadding = 50.dp

@Composable
fun LoaderView(device: AppDevice = AppDevice.PHONE) {
    val composition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(R.raw.loading_screen)
                                                )

    val loaderPadding = when (device) {
        AppDevice.TV    -> tvPadding
        AppDevice.PHONE -> phonePadding
    }

    LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.padding(loaderPadding)
                   )
}

@Preview
@Composable
fun LoaderPreview() {
    LoaderView(AppDevice.TV)
}

