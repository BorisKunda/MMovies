package com.bk.mmovies.tv.ui.component

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.bk.mmovies.tv.R
import androidx.core.net.toUri

private const val THUMBNAIL_ASPECT_RATIO = 16f / 9f
private val thumbnailPlayIconSize = 64.dp
private val focusBorderWidth = 3.dp
private const val THUMBNAIL_SCRIM_ALPHA = 0.35f

/** Android TV/Google TV's own YouTube app - deep-linking here plays the video directly. */
private const val YOUTUBE_TV_PACKAGE = "com.google.android.youtube.tv"

private fun String.toYoutubeVideoId(): String? =
        try {
            Uri.parse(this).getQueryParameter("v")
        } catch (e: Exception) {
            null
        }

// YouTube doesn't guarantee maxresdefault.jpg exists for every video, so the
// thumbnail falls back to hqdefault (always present, just lower-res) on load
// failure rather than showing a broken image.
private fun youtubeMaxResThumbnailUrl(videoId: String) = "https://img.youtube.com/vi/$videoId/maxresdefault.jpg"
private fun youtubeStandardThumbnailUrl(videoId: String) = "https://img.youtube.com/vi/$videoId/hqdefault.jpg"

/**
 * The details screen's trailer section: a thumbnail with a play affordance
 * (D-pad center to open). Selecting it hands the trailer off to the YouTube
 * app rather than playing it embedded - TV playback belongs to YouTube's own
 * player, not a WebView-backed one inside MMovies.
 */
@Composable
fun TvTrailerSection(trailerUrl: String, modifier: Modifier = Modifier) {
    val videoId = remember(trailerUrl) { trailerUrl.toYoutubeVideoId() } ?: return
    val context = LocalContext.current
    var isFocused by remember { mutableStateOf(false) }
    var thumbnailUrl by remember(videoId) { mutableStateOf(youtubeMaxResThumbnailUrl(videoId)) }

    Box(
            modifier = modifier
                    .fillMaxWidth()
                    .aspectRatio(THUMBNAIL_ASPECT_RATIO)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .then(
                            if (isFocused) {
                                Modifier.border(
                                        width = focusBorderWidth,
                                        color = Color.White
                                                )
                            } else {
                                Modifier
                            }
                         )
                    // onFocusChanged only observes the focus target that comes
                    // AFTER it in the chain, so it must precede clickable().
                    .onFocusChanged { focusState -> isFocused = focusState.isFocused }
                    .clickable {
                        val watchUrl = "https://www.youtube.com/watch?v=$videoId"
                        val tvAppIntent = Intent(Intent.ACTION_VIEW,
                                                 watchUrl.toUri()).apply {
                            setPackage(YOUTUBE_TV_PACKAGE)
                        }
                        try {
                            context.startActivity(tvAppIntent)
                        } catch (e: ActivityNotFoundException) {
                            context.startActivity(Intent(Intent.ACTION_VIEW,
                                                         watchUrl.toUri()))
                        }
                    },
            contentAlignment = Alignment.Center
       ) {
        AsyncImage(
                model = thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                onError = { thumbnailUrl = youtubeStandardThumbnailUrl(videoId) },
                modifier = Modifier.fillMaxSize()
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
                modifier = Modifier.height(thumbnailPlayIconSize)
            )
    }
}
