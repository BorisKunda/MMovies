package com.bk.mmovies.ui.screen.news

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.bk.mmovies.R
import com.bk.mmovies.ui.component.GenericErrorScreen

/**
 * In-app WebView screen that opens a NewsAPI article's original publisher
 * [articleUrl]. NewsAPI never returns the full article body (see the feature
 * README), so this is the only way to let the user read past the
 * excerpt/description - it is not a substitute for fetching real content.
 *
 * Rendered by [com.bk.mmovies.ui.screen.catalog.CatalogScreen] as a
 * full-screen overlay above its own Scaffold (see its `openArticleUrl`
 * state), rather than as its own NavHost destination.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleWebViewScreen(articleUrl: String, onClose: () -> Unit) {
    // Without this, back here fell through to the host's own BackHandler
    // (AppNavigation's), which pops the nav backstack - and since this
    // screen isn't a backstack entry itself, that closes the whole app
    // instead of just this overlay. ArticleWebViewContent's own
    // BackHandler(enabled = canGoBack) is registered later in the
    // composition and takes priority while the WebView has in-page history.
    BackHandler(onBack = onClose)

    Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                        title = {
                            Text(
                                    text = stringResource(R.string.news_webview_title),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.titleMedium
                                )
                        },
                        navigationIcon = {
                            IconButton(onClick = onClose) {
                                Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = stringResource(R.string.news_webview_close_action),
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.background
                                                                  )
                         )
            }
            ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (!isValidHttpUrl(articleUrl)) {
                GenericErrorScreen(stringResource(R.string.news_webview_invalid_url_message)) { onClose() }
            } else {
                ArticleWebViewContent(toHttpsUrl(articleUrl))
            }
        }
    }
}

private fun isValidHttpUrl(url: String): Boolean =
        url.isNotBlank() && (url.startsWith("http://") || url.startsWith("https://"))

// NewsAPI's crawled url field is sometimes stale plain-http even for
// publishers (e.g. deadline.com) that redirect to and only really serve
// https - and the app declares no network security config allowing
// cleartext, so WebView blocks the request before it ever reaches DNS.
// Upgrading the scheme here (rather than permitting cleartext app-wide)
// keeps that policy intact while still opening the article.
private fun toHttpsUrl(url: String): String =
        if (url.startsWith("http://")) "https://${url.removePrefix("http://")}" else url

private sealed interface WebViewLoadState {
    data object Loading : WebViewLoadState
    data object Content : WebViewLoadState
    data object Error : WebViewLoadState
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun ArticleWebViewContent(articleUrl: String) {
    var loadState by remember { mutableStateOf<WebViewLoadState>(WebViewLoadState.Loading) }
    var canGoBack by remember { mutableStateOf(false) }
    var webView by remember { mutableStateOf<WebView?>(null) }
    var reloadKey by remember { mutableStateOf(0) }

    BackHandler(enabled = canGoBack) {
        webView?.goBack()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (loadState) {
            is WebViewLoadState.Error -> {
                GenericErrorScreen(stringResource(R.string.news_webview_error_message)) {
                    loadState = WebViewLoadState.Loading
                    reloadKey++
                }
            }

            else                      -> {
                // Keying on reloadKey forces AndroidView to tear down and
                // re-run factory{} on retry, which is a simpler way to force
                // a fresh WebView/load than juggling an update{} callback.
                key(reloadKey) {
                    AndroidView(
                            modifier = Modifier.fillMaxSize(),
                            factory = { context ->
                                WebView(context).apply {
                                    settings.javaScriptEnabled = true
                                    settings.domStorageEnabled = true
                                    webViewClient = object : WebViewClient() {
                                        override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                                            loadState = WebViewLoadState.Loading
                                            canGoBack = view.canGoBack()
                                        }

                                        override fun onPageFinished(view: WebView, url: String?) {
                                            if (loadState != WebViewLoadState.Error) {
                                                loadState = WebViewLoadState.Content
                                            }
                                            canGoBack = view.canGoBack()
                                        }

                                        override fun onReceivedError(
                                                view: WebView,
                                                request: WebResourceRequest,
                                                error: WebResourceError
                                                                     ) {
                                            // Only a failure of the top-level page (not a
                                            // sub-resource like an ad/tracker script) should
                                            // block the whole article behind the error view.
                                            if (request.isForMainFrame) {
                                                loadState = WebViewLoadState.Error
                                            }
                                        }
                                    }
                                    webView = this
                                    loadUrl(articleUrl)
                                }
                            }
                               )
                }

                if (loadState is WebViewLoadState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}
