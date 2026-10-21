package com.bk.mmovies.tv.ui.component

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.foundation.focusable
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import coil3.annotation.ExperimentalCoilApi
import coil3.asImage
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.bk.mmovies.data.source.remote.CAST_PROFILE_PATH_SIZE_SEGMENT
import com.bk.mmovies.AppDevice
import com.bk.mmovies.domain.model.CastMemberModel
import com.bk.mmovies.tv.R
import com.bk.mmovies.core.R as CoreR
import com.bk.mmovies.domain.model.PersonDetailsModel
import com.bk.mmovies.ui.component.LoaderView
import com.bk.mmovies.ui.theme.MMoviesTheme

private val dialogWidthFraction = 0.6f
private val dialogHeightFraction = 0.85f
private val dialogCornerShape = 20.dp
private val photoHeight = 320.dp
private val contentPadding = 28.dp
private val nameToCharacterSpacing = 8.dp
private val characterToBodySpacing = 16.dp
private val bioTitleLetterSpacing = 1.sp
private val bioTitleTopSpacing = 4.dp

private const val TV_ACTOR_DIALOG_PROFILE_SIZE_SEGMENT = "h632"
private const val SCRIM_ALPHA = 0.7f
private const val SECONDARY_TEXT_ALPHA = 0.85f

// How many pixels a single D-pad UP/DOWN press scrolls the dialog - a plain
// Modifier.verticalScroll only responds to touch drag/fling, never D-pad key
// events, so the dialog body itself is focusable and takes the initial focus;
// the close button is reached with D-pad RIGHT and left with LEFT/DOWN.
private const val SCROLL_STEP_PX = 400f

// A Dialog is a separate window, and its own first composition can race this
// requestFocus() call against the body's focusRequester() modifier actually
// attaching - a single attempt can silently land on nothing, leaving the
// dialog unreachable by D-pad. Retrying across a few frames is a harmless
// no-op once focus is already there - same pattern used by the details
// screens' own initial-focus grab.
private const val FOCUS_REQUEST_RETRY_FRAMES = 5

private val closeButtonReservedWidth = 56.dp

/** TV counterpart of :app's ActorDetailsDialog - a scrollable popup, not a separate screen/route. */
@Composable
fun TvActorDetailsDialog(
        castMember: CastMemberModel,
        onDismiss: () -> Unit,
        viewModel: TvActorDetailsViewModel = hiltViewModel()
                        ) {
    val state by viewModel.personDetailsState.collectAsStateWithLifecycle()
    val closeFocusRequester = remember { FocusRequester() }
    val contentFocusRequester = remember { FocusRequester() }
    val scrollState = rememberScrollState()

    LaunchedEffect(castMember.id) { viewModel.loadPersonDetails(castMember.id) }
    LaunchedEffect(Unit) {
        repeat(FOCUS_REQUEST_RETRY_FRAMES) {
            runCatching { contentFocusRequester.requestFocus() }
            withFrameNanos {}
        }
    }

    Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false)
          ) {
        TvActorDetailsDialogSurface(
                castMember = castMember,
                state = state,
                closeFocusRequester = closeFocusRequester,
                contentFocusRequester = contentFocusRequester,
                scrollState = scrollState,
                onDismiss = onDismiss,
                onRetry = viewModel::retry
                                   )
    }
}

@Composable
private fun TvActorDetailsDialogSurface(
        castMember: CastMemberModel,
        state: TvPersonDetailsUiState,
        closeFocusRequester: FocusRequester,
        contentFocusRequester: FocusRequester,
        scrollState: ScrollState,
        onDismiss: () -> Unit,
        onRetry: () -> Unit
                                       ) {
    val coroutineScope = rememberCoroutineScope()

    Surface(
            modifier = Modifier
                    .fillMaxWidth(dialogWidthFraction)
                    .fillMaxHeight(dialogHeightFraction)
                    .onKeyEvent { keyEvent ->
                        if (keyEvent.type != KeyEventType.KeyDown) return@onKeyEvent false
                        val step = when (keyEvent.key) {
                            Key.DirectionDown -> SCROLL_STEP_PX
                            Key.DirectionUp   -> -SCROLL_STEP_PX
                            Key.DirectionRight -> {
                                runCatching { closeFocusRequester.requestFocus() }
                                return@onKeyEvent true
                            }
                            else              -> return@onKeyEvent false
                        }
                        coroutineScope.launch { scrollState.animateScrollBy(step) }
                        true
                    }
                    .focusRequester(contentFocusRequester)
                    .focusable(),
            shape = RoundedCornerShape(dialogCornerShape),
            color = MaterialTheme.colorScheme.surface
           ) {
        Column(
                modifier = Modifier
                        .fillMaxSize().padding(10.dp)
                        .verticalScroll(scrollState)
              ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                // The cast row only ever needed a w185 thumbnail, but that
                // upscales to a visibly blurry mess at this dialog's
                // size - swap in the larger TMDB size just for this.
                val largeProfileUrl = castMember.profileUrl.replace(
                        CAST_PROFILE_PATH_SIZE_SEGMENT,
                        TV_ACTOR_DIALOG_PROFILE_SIZE_SEGMENT
                                                                   )
                Row(Modifier.fillMaxWidth()) {
                    val personPlaceholderPainter = painterResource(CoreR.drawable.person_placeholder)

                    AsyncImage(
                            model = largeProfileUrl.takeIf { it.isNotBlank() },
                            error = personPlaceholderPainter,
                            fallback = personPlaceholderPainter,
                            contentDescription = null,

                            modifier = Modifier
                                    .wrapContentWidth()
                                    .height(photoHeight)
                                    .clip(
                                            RoundedCornerShape(
                                                    dialogCornerShape
                                            )
                                    )
                    )
                    Spacer(Modifier.size(20.dp))
                    Column(modifier = Modifier.weight(1f).padding(end = closeButtonReservedWidth)) {
                        Spacer(Modifier.size(15.dp))
                        Text(
                                text = castMember.name,
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                        )
                        if (castMember.character.isNotBlank()) {
                            Text(
                                    text = castMember.character,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = SECONDARY_TEXT_ALPHA),
                                    style = MaterialTheme.typography.headlineSmall,
                                    modifier = Modifier.padding(top = nameToCharacterSpacing)
                            )
                        }
                    }
                }
                IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                                .align(Alignment.TopEnd)
                                .focusRequester(closeFocusRequester)
                                .onPreviewKeyEvent { keyEvent ->
                                    val returnsToContent = keyEvent.key == Key.DirectionLeft || keyEvent.key == Key.DirectionDown
                                    if (keyEvent.type == KeyEventType.KeyDown && returnsToContent) {
                                        runCatching { contentFocusRequester.requestFocus() }
                                        true
                                    } else {
                                        false
                                    }
                                }
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
            Column(modifier = Modifier.padding(horizontal = 10.dp)) {

                when (val currentState = state) {
                    is TvPersonDetailsUiState.Loading -> {
                        Box(
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = characterToBodySpacing),
                                contentAlignment = Alignment.Center
                           ) {
                            LoaderView(AppDevice.TV)
                        }
                    }

                    is TvPersonDetailsUiState.Content -> {
                        Text(
                                text = stringResource(R.string.details_biography_label),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = SECONDARY_TEXT_ALPHA),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = bioTitleLetterSpacing,
                                modifier = Modifier.padding(top = characterToBodySpacing)
                            )
                        Text(
                                text = currentState.personDetails.biography.ifBlank {
                                    stringResource(R.string.details_biography_unavailable)
                                },
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = SECONDARY_TEXT_ALPHA),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(top = bioTitleTopSpacing)
                            )
                    }

                    is TvPersonDetailsUiState.Error -> {
                        Text(
                                text = currentState.errorMessage,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = SECONDARY_TEXT_ALPHA),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = characterToBodySpacing)
                            )
                        Box(modifier = Modifier.padding(top = characterToBodySpacing)) {
                            TvDetailsActionButton(
                                    text = stringResource(R.string.try_again),
                                    onClick = onRetry
                                                  )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Preview(device = "id:tv_1080p")
@Composable
private fun TvActorDetailsDialogSurfacePreview() {
    val actorPhoto = ContextCompat.getDrawable(LocalContext.current, com.bk.mmovies.core.R.drawable.person_placeholder)!!.asImage()
    CompositionLocalProvider(LocalAsyncImagePreviewHandler provides AsyncImagePreviewHandler { actorPhoto }) {
        MMoviesTheme {
            Scaffold(
                    containerColor = MaterialTheme.colorScheme.background
                    ) { paddingValues ->
                Box(
                        modifier = Modifier.padding(paddingValues),
                        contentAlignment = Alignment.Center
                   ) {
                    TvActorDetailsDialogSurface(
                            castMember = CastMemberModel(
                                    id = 1,
                                    name = "Andrew Koji",
                                    character = "Ryu",
                                    profileUrl = ""
                                                        ),
                            state = TvPersonDetailsUiState.Content(
                                    PersonDetailsModel(
                                            id = 1,
                                            name = "Andrew Koji",
                                            biography = "Andrew Koji is a British actor of Japanese descent, best known for playing Ryu in Street Fighter and Hanzo in Warrior.",
                                            birthday = "1987-03-03",
                                            placeOfBirth = "Bristol, England",
                                            profileUrl = ""
                                                       )
                                                                  ),
                            closeFocusRequester = remember { FocusRequester() },
                            contentFocusRequester = remember { FocusRequester() },
                            scrollState = rememberScrollState(),
                            onDismiss = {},
                            onRetry = {}
                                               )
                }
            }
        }
    }
}
