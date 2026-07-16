package com.bk.mmovies.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bk.mmovies.R

@Composable
fun NoInternetView(
    modifier: Modifier = Modifier, onSettingsButtonClicked: () -> Unit
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Color(0xFF1C1B1F)
            )
            .windowInsetsPadding(
                WindowInsets.safeDrawing
            )
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(
            modifier = Modifier.weight(0.7f)
        )

        Image(
            painter = painterResource(
                R.drawable.ic_no_connection
            ),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .aspectRatio(1f),
            contentScale = ContentScale.Fit
        )

        Spacer(
            modifier = Modifier.weight(0.75f)
        )

        Text(
            text = "No internet connection",
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Text(
            text = "Check your Wi-Fi or mobile data connection, then try again.",
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.weight(1.3f)
        )

        PrimaryButton(imageVector = Icons.Outlined.Settings, "Open network settings") {
            onSettingsButtonClicked()
        }

        Spacer(
            modifier = Modifier.height(40.dp)
        )
    }
}

@Composable
@Preview
fun NoConnectionViewPreview() {
    NoInternetView() { }
}