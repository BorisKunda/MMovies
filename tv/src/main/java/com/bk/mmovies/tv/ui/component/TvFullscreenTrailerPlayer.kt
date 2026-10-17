package com.bk.mmovies.tv.ui.component

import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

private const val VIDEO_ASPECT_RATIO = 16f / 9f
private const val SEEK_STEP_SECONDS = 10f

@Composable
fun TvFullscreenTrailerPlayer(
        videoId: String,
        onDismissRequest: () -> Unit,
        onPlaybackError: () -> Unit
                             ) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val keyHandlerFocusRequester = remember { FocusRequester() }
    var activePlayer by remember { mutableStateOf<YouTubePlayer?>(null) }
    var currentSecond by remember { mutableFloatStateOf(0f) }
    var isPlaying by remember { mutableStateOf(true) }

    Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(usePlatformDefaultWidth = false)
          ) {
        val dialogWindow = (LocalView.current.parent as? DialogWindowProvider)?.window
        DisposableEffect(dialogWindow) {
            dialogWindow?.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
                                   )
            dialogWindow?.setBackgroundDrawable(null)
            dialogWindow?.setElevation(0f)
            onDispose {}
        }
        LaunchedEffect(Unit) { keyHandlerFocusRequester.requestFocus() }

        Box(
                modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                        .focusRequester(keyHandlerFocusRequester)
                        .onPreviewKeyEvent { keyEvent ->
                            if (keyEvent.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                            val player = activePlayer ?: return@onPreviewKeyEvent false
                            when (keyEvent.key) {
                                Key.DirectionCenter, Key.Enter, Key.MediaPlayPause -> {
                                    if (isPlaying) player.pause() else player.play()
                                    true
                                }
                                Key.DirectionLeft, Key.MediaRewind                 -> {
                                    player.seekTo((currentSecond - SEEK_STEP_SECONDS).coerceAtLeast(0f))
                                    true
                                }
                                Key.DirectionRight, Key.MediaFastForward           -> {
                                    player.seekTo(currentSecond + SEEK_STEP_SECONDS)
                                    true
                                }
                                else                                               -> false
                            }
                        }
                        .focusable(),
                contentAlignment = Alignment.Center
           ) {
            AndroidView(
                    modifier = Modifier.aspectRatio(VIDEO_ASPECT_RATIO),
                    factory = { context ->
                        YouTubePlayerView(context).apply {
                            descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
                            lifecycleOwner.lifecycle.addObserver(this)
                            enableAutomaticInitialization = false
                            val playerOptions = IFramePlayerOptions.Builder(context)
                                    .controls(0)
                                    .rel(0)
                                    .ivLoadPolicy(3)
                                    .ccLoadPolicy(0)
                                    .fullscreen(0)
                                    .build()
                            initialize(
                                    object : AbstractYouTubePlayerListener() {
                                        override fun onReady(youTubePlayer: YouTubePlayer) {
                                            youTubePlayer.loadVideo(
                                                    videoId,
                                                    0f
                                                                   )
                                            activePlayer = youTubePlayer
                                        }

                                        override fun onCurrentSecond(
                                                youTubePlayer: YouTubePlayer,
                                                second: Float
                                                                    ) {
                                            currentSecond = second
                                        }

                                        override fun onStateChange(
                                                youTubePlayer: YouTubePlayer,
                                                state: PlayerConstants.PlayerState
                                                                  ) {
                                            when (state) {
                                                PlayerConstants.PlayerState.PLAYING -> isPlaying = true
                                                PlayerConstants.PlayerState.PAUSED  -> isPlaying = false
                                                PlayerConstants.PlayerState.ENDED   -> onDismissRequest()
                                                else                                -> Unit
                                            }
                                        }

                                        override fun onError(
                                                youTubePlayer: YouTubePlayer,
                                                error: PlayerConstants.PlayerError
                                                            ) {
                                            onPlaybackError()
                                        }
                                    },
                                    playerOptions
                                      )
                        }
                    },
                    onRelease = { playerView ->
                        lifecycleOwner.lifecycle.removeObserver(playerView)
                        playerView.release()
                    }
                       )
        }
    }
}
