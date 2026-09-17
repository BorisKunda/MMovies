package com.bk.mmovies.tv.ui.details.moviedetails

import android.widget.Toast
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.bk.mmovies.domain.model.CastMemberModel
import com.bk.mmovies.domain.model.Category
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.domain.model.MovieDetailsModel
import com.bk.mmovies.tv.R
import com.bk.mmovies.tv.ui.component.TvActorDetailsDialog
import com.bk.mmovies.tv.ui.component.TvCastMemberItem
import com.bk.mmovies.tv.ui.component.TvFavoriteStarButton
import com.bk.mmovies.tv.ui.component.TvGenericErrorScreen
import com.bk.mmovies.tv.ui.component.TvGenreChip
import com.bk.mmovies.tv.ui.component.TvMetaRow
import com.bk.mmovies.tv.ui.component.TvTrailerSection
import com.bk.mmovies.ui.component.LoaderView
import com.bk.mmovies.ui.component.PoweredByTmdbFooter
import com.bk.mmovies.ui.component.UserScoreView
import com.bk.mmovies.AppDevice
import kotlinx.coroutines.launch

// How many pixels a single D-pad UP/DOWN press scrolls the page.
private const val SCROLL_STEP_PX = 400f

// How many frames to retry the initial requestFocus() for - see its
// LaunchedEffect's own comment for why a single attempt can race.
private const val FOCUS_REQUEST_RETRY_FRAMES = 5

private val posterWidth = 180.dp
private val posterHeight = 270.dp
private val screenPadding = 32.dp

@Composable
fun MovieDetailsTvScreen(
        movieId: Int,
        categoryId: Int,
        viewModel: MovieDetailsTvViewModel = hiltViewModel()
                        ) {
    val context = LocalContext.current
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()
    val favoriteFocusRequester = remember { FocusRequester() }
    val trailerFocusRequester = remember { FocusRequester() }
    val castFocusRequester = remember { FocusRequester() }
    var selectedCastMember by remember { mutableStateOf<CastMemberModel?>(null) }

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    // A generic remote has no scroll wheel, so D-pad UP/DOWN needs to move
    // the page too, not just focus - normally that's done by trying
    // FocusManager.moveFocus() and falling back to a manual scroll only when
    // there's nowhere left to focus. That approach silently failed here: even
    // for already-composed, on-screen targets (the favorite star), moveFocus
    // never found a candidate (confirmed via the accessibility tree's
    // "focused" node staying on the back button no matter how many presses
    // fired - the true cause is unclear, but this whole screen's own manual
    // scroll-key handling on the same Down/Up keys focus search would use is
    // a likely factor). So instead of relying on spatial search, this steps
    // through an explicit, ordered list of the real interactive targets
    // (favorite star, trailer, first cast item - whichever actually exist
    // for this movie) via their own FocusRequesters, which is a much more
    // basic, reliable Compose API. Left/Right within the cast row once it has
    // focus is untouched and uses normal focus search, which works fine for
    // that simpler, single-direction case (same as the catalog rows).
    var stopIndex by remember { mutableIntStateOf(-1) }
    val stops = remember(screenState, viewModel.canToggleFavorite) {
        buildList {
            val content = screenState as? MovieDetailsTvScreenState.Content ?: return@buildList
            if (viewModel.canToggleFavorite) add(favoriteFocusRequester)
            if (content.movieDetails.trailerUrl != null) add(trailerFocusRequester)
            if (content.movieDetails.cast.isNotEmpty()) add(castFocusRequester)
        }
    }
    // No on-screen back button anymore (the remote's own BACK key, already
    // wired up in TvNavigation, covers it) - so the first stop takes initial
    // focus directly instead of a back button.
    LaunchedEffect(stops) {
        if (stopIndex == -1 && stops.isNotEmpty()) {
            stopIndex = 0
            // requestFocus() is a no-op (Compose logs "FocusRequester is not
            // initialized" rather than throwing, so runCatching alone can't
            // detect or prevent it) if the target's focusRequester()
            // modifier hasn't attached to a laid-out node yet - a real race
            // on a slow first frame. Retrying across a few frames is safe
            // (calling requestFocus() again once it's already focused is a
            // harmless no-op) and reliably lands once the node attaches,
            // which normally takes only a frame or two.
            repeat(FOCUS_REQUEST_RETRY_FRAMES) {
                runCatching { stops[0].requestFocus() }
                withFrameNanos {}
            }
        }
    }

    LaunchedEffect(movieId) { viewModel.loadMovieDetails(movieId) }
    LaunchedEffect(Unit) {
        viewModel.messageEvent.collect { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
    }

    Box(
            modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .onKeyEvent { keyEvent ->
                        if (keyEvent.type != KeyEventType.KeyDown) return@onKeyEvent false
                        when (keyEvent.key) {
                            Key.DirectionDown -> {
                                if (stopIndex < stops.size - 1) {
                                    stopIndex++
                                    stops[stopIndex].requestFocus()
                                } else {
                                    coroutineScope.launch { scrollState.animateScrollBy(SCROLL_STEP_PX) }
                                }
                                true
                            }
                            Key.DirectionUp   -> {
                                if (stopIndex > 0) {
                                    stopIndex--
                                    stops[stopIndex].requestFocus()
                                } else {
                                    coroutineScope.launch { scrollState.animateScrollBy(-SCROLL_STEP_PX) }
                                }
                                true
                            }
                            else              -> false
                        }
                    }
       ) {
        when (val state = screenState) {
            is MovieDetailsTvScreenState.Loading -> LoaderView(AppDevice.TV)
            is MovieDetailsTvScreenState.Error    -> TvGenericErrorScreen(
                    message = state.errorMessage,
                    onRetryClicked = viewModel::retry
                                                                          )
            is MovieDetailsTvScreenState.Content  -> MovieDetailsTvScreenContent(
                    movieDetails = state.movieDetails,
                    scrollState = scrollState,
                    showFavoriteStar = viewModel.canToggleFavorite,
                    // Upcoming releases have no votes yet, so the score ring
                    // would only ever show a meaningless zero - same rule as
                    // the catalog hero banner and mobile's details screen.
                    showUserScore = Category.fromCategoryId(categoryId) != MovieCategory.UpcomingMovieCategory,
                    onFavoriteClicked = viewModel::onFavoriteClicked,
                    onCastMemberClicked = { selectedCastMember = it },
                    favoriteFocusRequester = favoriteFocusRequester,
                    trailerFocusRequester = trailerFocusRequester,
                    castFocusRequester = castFocusRequester
                                                                                 )
        }
    }

    selectedCastMember?.let { castMember ->
        TvActorDetailsDialog(castMember = castMember, onDismiss = { selectedCastMember = null })
    }
}

@Composable
fun MovieDetailsTvScreenContent(
        movieDetails: MovieDetailsModel,
        modifier: Modifier = Modifier,
        scrollState: ScrollState = rememberScrollState(),
        showFavoriteStar: Boolean = false,
        showUserScore: Boolean = true,
        onFavoriteClicked: () -> Unit = {},
        onCastMemberClicked: (CastMemberModel) -> Unit = {},
        favoriteFocusRequester: FocusRequester? = null,
        trailerFocusRequester: FocusRequester? = null,
        castFocusRequester: FocusRequester? = null
                                ) {
    Column(
            modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(screenPadding)
          ) {
        Row {
            Box(modifier = Modifier.size(width = posterWidth, height = posterHeight)) {
                AsyncImage(
                        model = movieDetails.posterUrl,
                        contentDescription = movieDetails.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(10.dp))
                          )
                if (showFavoriteStar) {
                    TvFavoriteStarButton(
                            isFavorite = movieDetails.isFavorite,
                            onClick = onFavoriteClicked,
                            modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .let { if (favoriteFocusRequester != null) it.focusRequester(favoriteFocusRequester) else it }
                                         )
                }
            }

            Column(
                    modifier = Modifier
                            .weight(1f)
                            .padding(start = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                  ) {
                Text(
                        text = movieDetails.title,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                TvMetaRow(icon = Icons.Default.CalendarMonth, text = movieDetails.releaseDate)
                TvMetaRow(icon = Icons.Default.Schedule, text = movieDetails.runtime)
                if (showUserScore) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        UserScoreView(score = movieDetails.userScore, size = 36.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                                text = stringResource(R.string.user_score_label),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                                style = MaterialTheme.typography.labelLarge
                            )
                    }
                }
                if (movieDetails.genres.isNotEmpty()) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        movieDetails.genres.forEach { TvGenreChip(it) }
                    }
                }
            }
        }

        if (movieDetails.overview.isNotBlank()) {
            Column(modifier = Modifier.padding(top = 32.dp)) {
                Text(
                        text = stringResource(R.string.details_overview_label),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                Text(
                        text = movieDetails.overview,
                        modifier = Modifier.padding(top = 12.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                        style = MaterialTheme.typography.bodyLarge
                    )
            }
        }

        val trailerUrl = movieDetails.trailerUrl
        if (trailerUrl != null) {
            TvTrailerSection(
                    trailerUrl = trailerUrl,
                    modifier = Modifier
                            .padding(top = 32.dp)
                            .let { if (trailerFocusRequester != null) it.focusRequester(trailerFocusRequester) else it }
                            )
        }

        if (movieDetails.director != null || movieDetails.writers.isNotEmpty()) {
            Column(modifier = Modifier.padding(top = 32.dp)) {
                Text(
                        text = stringResource(R.string.details_crew_label),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    movieDetails.director?.let { CrewNameRow(it) }
                    movieDetails.writers.forEach { CrewNameRow(it) }
                }
            }
        }

        if (movieDetails.cast.isNotEmpty()) {
            Column(modifier = Modifier.padding(top = 32.dp)) {
                Text(
                        text = stringResource(R.string.details_cast_label),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                LazyRow(
                        modifier = Modifier.padding(top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                       ) {
                    itemsIndexed(movieDetails.cast, key = { _, it -> it.id }) { index, it ->
                        TvCastMemberItem(
                                it.name, it.character, it.profileUrl,
                                focusRequester = if (index == 0) castFocusRequester else null,
                                onClick = { onCastMemberClicked(it) }
                                        )
                    }
                }
            }
        }

        PoweredByTmdbFooter(modifier = Modifier.padding(top = 32.dp))
    }
}

@Composable
private fun CrewNameRow(member: CastMemberModel) {
    Text(
            text = member.name,
            modifier = Modifier.padding(vertical = 4.dp),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium
        )
}
