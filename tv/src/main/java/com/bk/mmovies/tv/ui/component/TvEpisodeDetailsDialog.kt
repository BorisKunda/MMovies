package com.bk.mmovies.tv.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import com.bk.mmovies.data.source.remote.STILL_PATH_SIZE_SEGMENT
import com.bk.mmovies.data.source.remote.STILL_PATH_SIZE_SEGMENT_ZOOM
import com.bk.mmovies.domain.model.EpisodeModel
import com.bk.mmovies.tv.R
import com.bk.mmovies.ui.component.UserScoreView
import kotlinx.coroutines.launch

private val dialogWidthFraction = 0.6f
private val dialogHeightFraction = 0.85f
private val dialogCornerShape = 20.dp
private val photoHeight = 260.dp
private val contentPadding = 28.dp
private val titleToMetaSpacing = 8.dp
private val metaToOverviewSpacing = 16.dp
private val scoreRingSize = 32.dp
private val scoreRowSpacing = 12.dp
private val crewSectionTopSpacing = 20.dp
private val crewRowSpacing = 4.dp

private const val SCRIM_ALPHA = 0.7f
private const val SECONDARY_TEXT_ALPHA = 0.85f

// How many pixels a single D-pad UP/DOWN press scrolls the dialog - a plain
// Modifier.verticalScroll only responds to touch drag/fling, never D-pad key
// events, and the only focusable element here is the close button, so
// without this the body (meta/overview/crew) was completely unreachable by
// remote.
private const val SCROLL_STEP_PX = 400f

// A Dialog is a separate window, and its own first composition can race this
// requestFocus() call against the close button's focusRequester() modifier
// actually attaching - a single attempt can silently land on nothing,
// leaving the close button (the only focusable element here) unreachable by
// D-pad. Retrying across a few frames is a harmless no-op once focus is
// already there - same pattern used by the details screens' own initial-
// focus grab.
private const val FOCUS_REQUEST_RETRY_FRAMES = 5

/** TV counterpart of :app's EpisodeDetailsDialog - a scrollable popup, not a separate screen/route. */
@Composable
fun TvEpisodeDetailsDialog(episode: EpisodeModel, onDismiss: () -> Unit) {
    val closeFocusRequester = remember { FocusRequester() }
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        repeat(FOCUS_REQUEST_RETRY_FRAMES) {
            runCatching { closeFocusRequester.requestFocus() }
            withFrameNanos {}
        }
    }

    Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false)
          ) {
        BoxWithConstraints {
            Surface(
                    modifier = Modifier
                            .fillMaxWidth(dialogWidthFraction)
                            .heightIn(max = maxHeight * dialogHeightFraction)
                            .onKeyEvent { keyEvent ->
                                if (keyEvent.type != KeyEventType.KeyDown) return@onKeyEvent false
                                val step = when (keyEvent.key) {
                                    Key.DirectionDown -> SCROLL_STEP_PX
                                    Key.DirectionUp   -> -SCROLL_STEP_PX
                                    else              -> return@onKeyEvent false
                                }
                                coroutineScope.launch { scrollState.animateScrollBy(step) }
                                true
                            },
                    shape = RoundedCornerShape(dialogCornerShape),
                    color = MaterialTheme.colorScheme.surface
                   ) {
                Column(
                        modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(scrollState)
                      ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        // The season-list thumbnail only ever needed a w300 still,
                        // but that upscales to a visibly blurry mess at this
                        // dialog's size - swap in the larger TMDB size just for this.
                        val largeStillUrl = episode.stillUrl.replace(
                                STILL_PATH_SIZE_SEGMENT,
                                STILL_PATH_SIZE_SEGMENT_ZOOM
                                                                    )
                        AsyncImage(
                                model = largeStillUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .height(photoHeight)
                                        .clip(
                                                RoundedCornerShape(
                                                        topStart = dialogCornerShape,
                                                        topEnd = dialogCornerShape
                                                                   )
                                             )
                                  )
                        IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                        .focusRequester(closeFocusRequester)
                                  ) {
                            Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.action_close),
                                    tint = Color.White,
                                    modifier = Modifier
                                            .clip(RoundedCornerShape(50))
                                            .background(Color.Black.copy(alpha = SCRIM_ALPHA))
                                )
                        }
                    }
                    Column(modifier = Modifier.padding(contentPadding)) {
                        Text(
                                text = stringResource(
                                        R.string.details_episode_number_title,
                                        episode.episodeNumber,
                                        episode.name
                                                     ),
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )

                        val metaText = listOf(episode.runtime, episode.airDate)
                                .filter { it.isNotBlank() }
                                .joinToString(" • ")
                        if (metaText.isNotBlank()) {
                            Text(
                                    text = metaText,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = SECONDARY_TEXT_ALPHA),
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(top = titleToMetaSpacing)
                                )
                        }

                        if (episode.rating > 0) {
                            Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = titleToMetaSpacing)
                               ) {
                                UserScoreView(score = episode.rating, size = scoreRingSize)
                                Spacer(modifier = Modifier.width(scoreRowSpacing))
                                Text(
                                        text = stringResource(R.string.user_score_label),
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = SECONDARY_TEXT_ALPHA),
                                        style = MaterialTheme.typography.labelLarge
                                    )
                            }
                        }

                        if (episode.overview.isNotBlank()) {
                            Text(
                                    text = episode.overview,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = SECONDARY_TEXT_ALPHA),
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(top = metaToOverviewSpacing)
                                )
                        }

                        if (episode.director != null || episode.writers.isNotEmpty()) {
                            Column(
                                    modifier = Modifier.padding(top = crewSectionTopSpacing),
                                    verticalArrangement = Arrangement.spacedBy(crewRowSpacing)
                                  ) {
                                episode.director?.let {
                                    Text(
                                            text = "${stringResource(R.string.details_director_role)}: ${it.name}",
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = SECONDARY_TEXT_ALPHA),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                }
                                if (episode.writers.isNotEmpty()) {
                                    Text(
                                            text = "${stringResource(R.string.details_writers_label)} " +
                                                    episode.writers.joinToString(", ") { it.name },
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = SECONDARY_TEXT_ALPHA),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
