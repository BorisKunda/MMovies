package com.bk.mmovies.ui.component

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import coil3.compose.AsyncImage
import com.bk.mmovies.R
import com.bk.mmovies.ui.theme.MMoviesTheme
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

private val playerHeight = 220.dp
private val trailerButtonIconSize = 20.dp
private val trailerButtonIconTextSpacing = 8.dp
private val thumbnailPlayIconSize = 56.dp
private const val THUMBNAIL_SCRIM_ALPHA = 0.35f

// TMDB gives us a full "watch" URL; the in-app player needs just the "v="
// video id portion of it.
private fun String.toYoutubeVideoId(): String? = Uri.parse(this).getQueryParameter("v")

private fun youtubeThumbnailUrl(videoId: String) = "https://img.youtube.com/vi/$videoId/hqdefault.jpg"

/**
 * Plays a movie/TV trailer in-app using the embedded YouTube WebView player.
 * Some videos refuse in-app playback (YouTube error 152 — the player's
 * origin isn't authorized), so a playback error here falls back to opening
 * the trailer in the YouTube app/browser instead of showing a dead player.
 *
 * Shows our own plain thumbnail + play button first rather than YouTube's
 * cued-video card (title, channel name, share icon, "Watch on YouTube"
 * badge) — once tapped, the real player takes over with its native
 * play/pause controls (YouTube doesn't fully suppress its own tap-to-play
 * affordance even with controls hidden, so drawing a second one on top just
 * produced two play icons stacked on each other).
 *
 * Rotating the device to landscape while playing expands the player to fill
 * the whole screen, matching how a video naturally wants to be watched.
 */
@Composable
fun TrailerSection(trailerUrl: String, modifier: Modifier = Modifier, pause: Boolean = false) {
    val videoId = remember(trailerUrl) { trailerUrl.toYoutubeVideoId() }
    // Rotating recreates the Activity (no configChanges override), which
    // wipes plain remember state — rememberSaveable is what lets all three
    // of these survive that, so landscape actually continues into the
    // fullscreen player instead of resetting back to the play-button state.
    var embedFailed by rememberSaveable(trailerUrl) { mutableStateOf(false) }
    var started by rememberSaveable(trailerUrl) { mutableStateOf(false) }
    var resumeSecond by rememberSaveable(trailerUrl) { mutableStateOf(0f) }
    // Not saveable: a live player handle can't survive process/activity
    // recreation anyway, and a fresh player always starts unpaused.
    var activePlayer by remember { mutableStateOf<YouTubePlayer?>(null) }

    // Lets a caller (e.g. a dialog opening over this screen) stop playback
    // without the user having to find/tap the player's own pause control.
    LaunchedEffect(pause, activePlayer) {
        if (pause) {
            activePlayer?.pause()
        }
    }

    // A trailerUrl TMDB gave us should always carry a "v=" video id — but if
    // it somehow doesn't, there's no video to show and no link worth
    // offering either, so this renders nothing rather than a dead fallback.
    if (videoId == null) {
        return
    }

    if (embedFailed) {
        WatchOnYoutubeButton(trailerUrl = trailerUrl, modifier = modifier)
        return
    }

    // IDE Preview has no WebView/network access to actually play video.
    if (LocalInspectionMode.current) {
        YouTubePlayerPlaceholder(modifier)
        return
    }

    if (!started) {
        ThumbnailPlayButton(
                videoId = videoId,
                onClick = { started = true },
                modifier = modifier
                        .fillMaxWidth()
                        .height(playerHeight)
                           )
        return
    }

    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    if (isLandscape) {
        FullscreenYoutubePlayer(
                videoId = videoId,
                startSeconds = resumeSecond,
                onError = { embedFailed = true },
                onTimeUpdate = { resumeSecond = it },
                onReady = { activePlayer = it }
                               )
    } else {
        EmbeddedYoutubePlayer(
                videoId = videoId,
                startSeconds = resumeSecond,
                onError = { embedFailed = true },
                onTimeUpdate = { resumeSecond = it },
                onReady = { activePlayer = it },
                modifier = modifier
                        .fillMaxWidth()
                        .height(playerHeight)
                             )
    }
}

@Composable
private fun ThumbnailPlayButton(videoId: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
            modifier = modifier.clickable(onClick = onClick),
            contentAlignment = Alignment.Center
       ) {
        AsyncImage(
                model = youtubeThumbnailUrl(videoId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                  )
        Box(
                modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = THUMBNAIL_SCRIM_ALPHA))
           )
        Icon(
                imageVector = Icons.Filled.PlayCircle,
                contentDescription = stringResource(R.string.details_watch_trailer_action),
                tint = Color.White,
                modifier = Modifier.size(thumbnailPlayIconSize)
            )
    }
}

@Composable
private fun EmbeddedYoutubePlayer(
        videoId: String,
        startSeconds: Float,
        onError: () -> Unit,
        onTimeUpdate: (Float) -> Unit,
        onReady: (YouTubePlayer) -> Unit = {},
        modifier: Modifier = Modifier
                                  ) {
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(
            modifier = modifier,
            factory = { context ->
                YouTubePlayerView(context).apply {
                    lifecycleOwner.lifecycle.addObserver(this)
                    // Skip the default auto-init so we can hand it player
                    // options: related-videos/annotations/captions off and
                    // the built-in fullscreen button disabled (rotation
                    // already drives our own fullscreen). controls(0) was
                    // tried to get a single custom play/pause button, but
                    // YouTube still shows its own tap-to-play affordance
                    // underneath regardless — that produced two play icons
                    // stacked on top of each other, so native controls are
                    // the single source of truth instead.
                    enableAutomaticInitialization = false
                    val playerOptions = IFramePlayerOptions.Builder(context)
                            .controls(1)
                            .rel(0)
                            .ivLoadPolicy(3)
                            .ccLoadPolicy(0)
                            .fullscreen(0)
                            .build()
                    initialize(
                            object : AbstractYouTubePlayerListener() {
                                override fun onReady(player: YouTubePlayer) {
                                    // The user already tapped our own play
                                    // button to get here, so starting
                                    // playback immediately is the expected
                                    // outcome, not clutter. startSeconds
                                    // picks up where rotation left off
                                    // instead of always restarting at 0.
                                    player.loadVideo(videoId, startSeconds)
                                    onReady(player)
                                }

                                override fun onCurrentSecond(player: YouTubePlayer, second: Float) {
                                    // Rounded so this only writes ~once a
                                    // second (the HTML bridge reports every
                                    // 100ms) — sub-second resume precision
                                    // isn't worth the extra recomposition.
                                    onTimeUpdate(second.toInt().toFloat())
                                }

                                override fun onError(
                                        player: YouTubePlayer,
                                        error: PlayerConstants.PlayerError
                                                     ) {
                                    onError()
                                }
                            },
                            playerOptions
                              )
                }
            },
            // Without this the view stayed registered on lifecycleOwner
            // forever: AndroidView only calls factory/update, never anything
            // on disposal by default. FullscreenYoutubePlayer discards and
            // recreates this view on every resize (see key(boxSize) below),
            // so without releasing here each resize left behind another
            // leaked WebView still listening for lifecycle callbacks.
            onRelease = { view ->
                lifecycleOwner.lifecycle.removeObserver(view)
                view.release()
            }
               )
}

// A borderless, fullscreen Dialog rather than reparenting the existing
// player: the WebView can't be moved across window boundaries, and tying
// this to the orientation (not a dismiss action) means rotating back to
// portrait is what closes it — there's nothing else to tap to get out.
@Composable
private fun FullscreenYoutubePlayer(
        videoId: String,
        startSeconds: Float,
        onError: () -> Unit,
        onTimeUpdate: (Float) -> Unit,
        onReady: (YouTubePlayer) -> Unit = {}
                                    ) {
    Dialog(
            onDismissRequest = {},
            properties = DialogProperties(
                    usePlatformDefaultWidth = false,
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                                          )
          ) {
        // usePlatformDefaultWidth = false only lifts the width cap — the
        // dialog's own Window still wraps its content by default, which is
        // what left the player letterboxed and scrollable instead of filling
        // the screen. It has to be resized directly, and the system bars
        // hidden on this Window specifically, since a Dialog opens its own
        // Window on top of the activity's rather than reusing it.
        val view = LocalView.current
        val window = (view.parent as? DialogWindowProvider)?.window
        DisposableEffect(window) {
            window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
            // A Dialog's window is floating by default, which reserves a
            // surface inset margin on every side for its drop shadow (96px
            // on this device, confirmed via `dumpsys window`) — that's what
            // was actually cropping the player's edges. This is computed
            // from the window's elevation at creation time; dropping the
            // background alone didn't clear it, so the elevation itself has
            // to be zeroed.
            window?.setBackgroundDrawable(null)
            window?.setElevation(0f)
            // Without this, the dialog's window also leaves a gap next to
            // this device's camera cutout in landscape (confirmed via
            // `dumpsys window`: frame started at x=80, not x=0) — the real
            // Activity window already opts into this via enableEdgeToEdge(),
            // but a Dialog opens a separate Window that needs it applied
            // again.
            // layoutInDisplayCutoutMode was only added to LayoutParams in API
            // 28 — referencing it on API 23-27 throws NoSuchFieldError.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                window?.let {
                    it.attributes = it.attributes.apply {
                        layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
                    }
                }
            }
            // Resizing and hiding the bars isn't enough on its own: with
            // decorFitsSystemWindows left at its default (true), the window
            // still reserves the status/nav bar's space in its layout even
            // though the bars themselves are hidden — which is exactly what
            // cropped the player's top/bottom edges. This is the same
            // enableEdgeToEdge() opt-in MainActivity's own window gets, but
            // a Dialog opens a separate Window that needs it applied again.
            window?.let { WindowCompat.setDecorFitsSystemWindows(it, false) }
            val insetsController = window?.let { WindowCompat.getInsetsController(it, view) }
            insetsController?.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            insetsController?.hide(WindowInsetsCompat.Type.systemBars())
            onDispose {
                insetsController?.show(WindowInsetsCompat.Type.systemBars())
            }
        }

        // The dialog's window resize above doesn't take effect on this same
        // frame — Android re-measures the window asynchronously. If the
        // WebView were created against that stale, pre-resize size, its
        // player would read that wrong size once at load time (it never
        // re-checks), leaving its overlay chrome — title bar, controls —
        // misaligned relative to the video once the window's real size
        // catches up, even though the video itself scales up fine. Keying
        // on the Box's own measured size forces the WebView to be thrown
        // away and recreated fresh the moment that resize actually lands,
        // so it's never built against the wrong size to begin with.
        var boxSize by remember { mutableStateOf(IntSize.Zero) }
        Box(
                modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                        .onSizeChanged { boxSize = it },
                contentAlignment = Alignment.Center
           ) {
            if (boxSize.width > 0 && boxSize.height > 0) {
                key(boxSize) {
                    // Stretching the WebView to the full (non-16:9) screen
                    // leaves leftover vertical space below the video's
                    // natural height — YouTube's iframe page fills that gap
                    // with its own chrome (channel name, share, related
                    // video, "More videos"/logo bar), which then becomes
                    // visible and scrollable. Capping the player to its real
                    // 16:9 box and centering it removes that leftover space
                    // entirely, so there's nothing left to scroll into.
                    EmbeddedYoutubePlayer(
                            videoId = videoId,
                            startSeconds = startSeconds,
                            onError = onError,
                            onTimeUpdate = onTimeUpdate,
                            onReady = onReady,
                            // aspectRatio alone (no fillMaxWidth) lets it pick
                            // whichever dimension is the limiting one against
                            // the parent's exact fillMaxSize bounds, so it
                            // still letterboxes correctly on a landscape
                            // screen narrower than 16:9, not just wider.
                            modifier = Modifier.aspectRatio(16f / 9f)
                                         )
                }
            }
        }
    }
}

@Composable
private fun YouTubePlayerPlaceholder(modifier: Modifier = Modifier) {
    Box(
            modifier = modifier
                    .fillMaxWidth()
                    .height(playerHeight)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
       ) {
        Icon(
                imageVector = Icons.Filled.PlayCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
    }
}

@Composable
private fun WatchOnYoutubeButton(trailerUrl: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Button(
            onClick = { openInYoutube(context, trailerUrl) },
            modifier = modifier
          ) {
        Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(trailerButtonIconSize)
            )
        Spacer(modifier = Modifier.width(trailerButtonIconTextSpacing))
        Text(text = stringResource(R.string.details_watch_trailer_action))
    }
}

private fun openInYoutube(context: Context, trailerUrl: String) {
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl)))
    } catch (e: ActivityNotFoundException) {
        // No browser/YouTube app to hand the link off to — nothing else we
        // can do here, so fail silently rather than crash.
    }
}

@Preview(showBackground = true)
@Composable
private fun TrailerSectionPreview() {
    MMoviesTheme {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
            TrailerSection(
                    trailerUrl = "https://www.youtube.com/watch?v=aqz-KE-bpKQ",
                    modifier = Modifier.padding(padding)
                           )
        }
    }
}
