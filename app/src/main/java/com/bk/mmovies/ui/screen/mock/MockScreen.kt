package com.bk.mmovies.ui.screen.mock

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView



@Composable
fun MockScreen() {
    val context = LocalContext.current
    Box(
            Modifier.fillMaxSize(),
            content = {
                AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { context ->
                            WebView(context).apply {

                                webViewClient = WebViewClient()

                                settings.javaScriptEnabled = true

                                loadUrl("https://www.google.com/")
                            }
                        }
                           )
            })
}

