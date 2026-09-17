package com.bk.mmovies.tv.ui.webview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.webkit.CookieManager
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import java.io.ByteArrayInputStream
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.bk.mmovies.tv.R
import com.bk.mmovies.tv.ui.component.TvGenericErrorScreen

/**
 * TV equivalent of app's ArticleWebViewScreen, generalized to open any
 * external [url] (TMDB/Gemini account pages) in an in-app WebView - Google TV
 * Streamer devices ship with no browser app to hand ACTION_VIEW off to. A
 * real TvNavigation destination - D-pad Back pops it, matching the rest of
 * the TV nav graph. News articles no longer route here - see
 * NewsDetailsTvScreen, which renders a real fetched article body natively
 * instead of handing off to the source site.
 */
@Composable
fun WebViewTvScreen(url: String, onBack: () -> Unit) {
    BackHandler(onBack = onBack)

    // Hoisted up to this screen's own root, not WebViewTvContent's, so the
    // scroll handler below (on this same Box) can reach it.
    var webView by remember { mutableStateOf<WebView?>(null) }

    Box(
            modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .onKeyEvent { keyEvent ->
                        if (keyEvent.type != KeyEventType.KeyDown) return@onKeyEvent false
                        val delta = when (keyEvent.key) {
                            Key.DirectionDown -> SCROLL_STEP_PX
                            Key.DirectionUp   -> -SCROLL_STEP_PX
                            else              -> return@onKeyEvent false
                        }
                        val view = webView ?: return@onKeyEvent false
                        view.scrollBy(0, delta)
                        true
                    }
       ) {
        if (!isValidHttpUrl(url)) {
            TvGenericErrorScreen(
                    message = stringResource(R.string.news_webview_invalid_url_message),
                    onRetryClicked = onBack
                                 )
        } else {
            WebViewTvContent(
                    url = toHttpsUrl(url),
                    onWebViewReady = { webView = it }
                            )
        }
    }
}

// How many pixels a single D-pad UP/DOWN press scrolls the page - a remote
// has no scroll wheel, and most of these pages have no obviously-focusable
// element near the top for the WebView's own native D-pad link-navigation to
// land on, which made the page feel completely unresponsive. Scrolling the
// WebView directly on the key press, rather than relying on that native
// focus-based navigation, fixes it.
private const val SCROLL_STEP_PX = 400

private fun isValidHttpUrl(url: String): Boolean =
        url.isNotBlank() && (url.startsWith("http://") || url.startsWith("https://"))

// Some source urls are stale plain-http even for pages that only really
// serve https, and the app declares no network security config allowing
// cleartext - upgrading the scheme keeps that policy intact while still
// opening the page.
private fun toHttpsUrl(url: String): String =
        if (url.startsWith("http://")) "https://${url.removePrefix("http://")}" else url

// There's no D-pad-friendly way to tap a cookie-consent banner's tiny "Accept"
// button on a TV remote, so this best-effort-clicks it via JS instead. It only
// covers the handful of consent-management vendors most news sites use
// (OneTrust, Cookiebot, Sourcepoint/Quantcast, Didomi, TrustArc) plus a few
// generic selector guesses - sites outside that list will still show their
// banner unhandled.
private const val COOKIE_CONSENT_AUTO_ACCEPT_JS = """
(function() {
    var selectors = [
        '#onetrust-accept-btn-handler',
        '#CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll',
        '#CybotCookiebotDialogBodyButtonAccept',
        '.qc-cmp2-summary-buttons button[mode="primary"]',
        '.sp_choice_type_11',
        '#didomi-notice-agree-button',
        '#truste-consent-button',
        '[aria-label="Accept all"]',
        '[aria-label="Accept cookies"]',
        'button[id*="accept" i]',
        'button[class*="accept" i]'
    ];
    for (var i = 0; i < selectors.length; i++) {
        var el = document.querySelector(selectors[i]);
        if (el) { el.click(); break; }
    }
})();
"""

// Consent banners are frequently injected by a script that loads a moment
// after onPageFinished fires, so a single injection right on page-finish
// often runs before the banner exists. Re-running it a few times over a
// couple of seconds catches those late arrivals without polling forever.
private val COOKIE_CONSENT_RETRY_DELAYS_MS = longArrayOf(0L, 600L, 1500L, 3000L)

private fun WebView.autoAcceptCookieConsent() {
    val handler = Handler(Looper.getMainLooper())
    COOKIE_CONSENT_RETRY_DELAYS_MS.forEach { delay ->
        handler.postDelayed({ evaluateJavascript(COOKIE_CONSENT_AUTO_ACCEPT_JS, null) }, delay)
    }
}

// Backstop for setNeedInitialFocus(false) above and disableClicksKeepScrolling()
// below - some sites draw their own focus/hover ring via CSS regardless of how
// focus got there, so this strips it outright rather than relying on nothing
// ever focusing an element in the first place.
private const val STRIP_FOCUS_OUTLINE_JS = """
(function() {
    var style = document.createElement('style');
    style.textContent = '*:focus, *:focus-visible { outline: none !important; box-shadow: none !important; }';
    document.head.appendChild(style);
})();
"""

private fun WebView.stripFocusOutline() {
    evaluateJavascript(STRIP_FOCUS_OUTLINE_JS, null)
}

// The article is read-only here - no login, no comment forms, no in-page
// navigation the app wants to follow. Left alone, WebView's own D-pad
// spatial navigation would let OK/DPAD_CENTER click links and buttons and
// LEFT/RIGHT jump focus between them, which reads as accidental/broken on a
// remote. Swallowing everything except UP/DOWN (for scrolling, handled by
// this screen's own key handler) and BACK (still needed to exit) disables
// that without touching the scroll behavior.
private fun WebView.disableClicksKeepScrolling() {
    setOnKeyListener { _, keyCode, _ ->
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_BACK -> false
            else                                                                       -> true
        }
    }
}

// News sites are typically 60-80% ad/tracker requests by count - this blocks
// the well-known networks before they ever load, which is most of what makes
// these pages feel heavy and cluttered on a TV's weaker GPU/CPU. Deliberately
// NOT exhaustive (no attempt at a full adblock-style list) and deliberately
// leaves cdn.cookielaw.org etc. alone since the cookie-consent auto-accept
// above depends on that script actually loading.
private val BLOCKED_AD_TRACKER_HOST_SUFFIXES = listOf(
        "doubleclick.net",
        "googlesyndication.com",
        "googleadservices.com",
        "google-analytics.com",
        "googletagmanager.com",
        "googletagservices.com",
        "adservice.google.com",
        "taboola.com",
        "outbrain.com",
        "criteo.com",
        "criteo.net",
        "adnxs.com",
        "scorecardresearch.com",
        "chartbeat.com",
        "chartbeat.net",
        "moatads.com",
        "adsrvr.org",
        "pubmatic.com",
        "rubiconproject.com",
        "casalemedia.com",
        "openx.net",
        "bidswitch.net",
        "amazon-adsystem.com",
        "quantserve.com",
        "comscore.com",
        "hotjar.com",
        "mixpanel.com",
        "permutive.com",
        "branch.io"
                                                       )

private fun isBlockedAdTrackerRequest(request: WebResourceRequest): Boolean {
    val host = request.url.host ?: return false
    return BLOCKED_AD_TRACKER_HOST_SUFFIXES.any { host == it || host.endsWith(".$it") }
}

private val emptyWebResourceResponse: WebResourceResponse
    get() = WebResourceResponse("text/plain", "utf-8", ByteArrayInputStream(ByteArray(0)))

private sealed interface WebViewLoadState {
    data object Loading : WebViewLoadState
    data object Content : WebViewLoadState
    data object Error : WebViewLoadState
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun WebViewTvContent(url: String, onWebViewReady: (WebView?) -> Unit) {
    var loadState by remember { mutableStateOf<WebViewLoadState>(WebViewLoadState.Loading) }
    var reloadKey by remember { mutableStateOf(0) }

    // WebView in-page history isn't relevant on a leanback remote the way it
    // is on mobile (no gesture nav) - Back here always exits the screen,
    // handled by the outer BackHandler in WebViewTvScreen.

    Box(modifier = Modifier.fillMaxSize()) {
        when (loadState) {
            is WebViewLoadState.Error -> {
                TvGenericErrorScreen(
                        message = stringResource(R.string.news_webview_error_message),
                        onRetryClicked = {
                            loadState = WebViewLoadState.Loading
                            reloadKey++
                        }
                                     )
            }

            else                      -> {
                key(reloadKey) {
                    AndroidView(
                            modifier = Modifier.fillMaxSize(),
                            factory = { context ->
                                WebView(context).apply {
                                    settings.javaScriptEnabled = true
                                    settings.domStorageEnabled = true
                                    // News sites are almost universally mobile/desktop-widths,
                                    // not built for a 10-foot TV layout - without these, the
                                    // WebView renders at a fixed ~980px viewport and the page
                                    // shows zoomed-in/cut off with no pinch-to-zoom on a remote.
                                    settings.useWideViewPort = true
                                    settings.loadWithOverviewMode = true
                                    // Without this, WebView auto-focuses/highlights the page's
                                    // first interactive element (a link, a text field) the
                                    // instant it finishes loading - on its own, no key press
                                    // involved. That's on top of (not fixed by)
                                    // disableClicksKeepScrolling() below, which only stops keys
                                    // from moving focus around, not this automatic initial one.
                                    settings.setNeedInitialFocus(false)
                                    // Lets a site's own consent cookie (set by the JS click
                                    // below) actually persist to disk, so its banner doesn't
                                    // reappear on the next article from the same site.
                                    val webView = this
                                    CookieManager.getInstance().apply {
                                        setAcceptCookie(true)
                                        setAcceptThirdPartyCookies(webView, true)
                                    }
                                    disableClicksKeepScrolling()
                                    webViewClient = object : WebViewClient() {
                                        override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                                            loadState = WebViewLoadState.Loading
                                        }

                                        override fun onPageFinished(view: WebView, url: String?) {
                                            if (loadState != WebViewLoadState.Error) {
                                                loadState = WebViewLoadState.Content
                                            }
                                            view.autoAcceptCookieConsent()
                                            view.stripFocusOutline()
                                            CookieManager.getInstance().flush()
                                        }

                                        override fun onReceivedError(
                                                view: WebView,
                                                request: WebResourceRequest,
                                                error: WebResourceError
                                                                     ) {
                                            if (request.isForMainFrame) {
                                                loadState = WebViewLoadState.Error
                                            }
                                        }

                                        override fun shouldInterceptRequest(
                                                view: WebView,
                                                request: WebResourceRequest
                                                                            ): WebResourceResponse? =
                                                if (isBlockedAdTrackerRequest(request)) emptyWebResourceResponse else null
                                    }
                                    onWebViewReady(this)
                                    loadUrl(url)
                                }
                            },
                            // Without this, backing out of the article leaves the WebView
                            // (and its renderer-process resources) alive and unreferenced -
                            // a leak that compounds with every article opened in a session.
                            onRelease = { view ->
                                onWebViewReady(null)
                                view.destroy()
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
