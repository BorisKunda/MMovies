package com.bk.mmovies.tv.ui.details.tvseriesdetails

import android.widget.Toast
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.bk.mmovies.AppDevice
import com.bk.mmovies.domain.model.CastMemberModel
import com.bk.mmovies.domain.model.EpisodeModel
import com.bk.mmovies.domain.model.SeasonModel
import com.bk.mmovies.domain.model.SeriesAirDateLabel
import com.bk.mmovies.domain.model.TvSeriesDetailsModel
import com.bk.mmovies.tv.R
import com.bk.mmovies.tv.ui.component.TvActorDetailsDialog
import com.bk.mmovies.tv.ui.component.TvCastMemberItem
import com.bk.mmovies.tv.ui.component.TvEpisodeDetailsDialog
import com.bk.mmovies.tv.ui.component.TvFavoriteStarButton
import com.bk.mmovies.tv.ui.component.TvGenericErrorScreen
import com.bk.mmovies.tv.ui.component.TvGenreChip
import com.bk.mmovies.tv.ui.component.TvMetaRow
import com.bk.mmovies.tv.ui.component.TvTrailerSection
import com.bk.mmovies.ui.component.LoaderView
import com.bk.mmovies.ui.component.PoweredByTmdbFooter
import com.bk.mmovies.ui.component.UserScoreView
import kotlinx.coroutines.launch

// How many pixels a single D-pad UP/DOWN press scrolls the page.
private const val SCROLL_STEP_PX = 400f

// How many frames to retry the initial requestFocus() for - see its
// LaunchedEffect's own comment for why a single attempt can race.
private const val FOCUS_REQUEST_RETRY_FRAMES = 5

private val posterWidth = 180.dp
private val posterHeight = 270.dp
private val screenPadding = 32.dp
private val episodeStillWidth = 220.dp
private val episodeStillHeight = 124.dp

@Composable
fun TvSeriesDetailsTvScreen(
        seriesId: Int,
        viewModel: TvSeriesDetailsTvViewModel = hiltViewModel()
                           ) {
    val context = LocalContext.current
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()
    val favoriteFocusRequester = remember { FocusRequester() }
    val trailerFocusRequester = remember { FocusRequester() }
    val castFocusRequester = remember { FocusRequester() }
    var selectedCastMember by remember { mutableStateOf<CastMemberModel?>(null) }
    var selectedEpisode by remember { mutableStateOf<EpisodeModel?>(null) }

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    // See MovieDetailsTvScreen's identical setup and comment for why this
    // steps through explicit FocusRequesters rather than FocusManager's
    // spatial moveFocus() search, which never found a candidate anywhere in
    // this tree. One requester per season here (rather than a fixed few
    // constants) since a series can have anywhere from one season to dozens -
    // each one's first episode (if it has any) becomes its own stop.
    val seasonFocusRequesters = remember(screenState) {
        val content = screenState as? TvSeriesDetailsTvScreenState.Content
        List(content?.seasons?.size ?: 0) { FocusRequester() }
    }
    var stopIndex by remember { mutableIntStateOf(-1) }
    val stops = remember(screenState, viewModel.canToggleFavorite) {
        buildList {
            val content = screenState as? TvSeriesDetailsTvScreenState.Content ?: return@buildList
            if (viewModel.canToggleFavorite) add(favoriteFocusRequester)
            if (content.tvSeriesDetailsModel.trailerUrl != null) add(trailerFocusRequester)
            if (content.tvSeriesDetailsModel.cast.isNotEmpty()) add(castFocusRequester)
            content.seasons.forEachIndexed { index, season ->
                if (season.episodes.isNotEmpty()) add(seasonFocusRequesters[index])
            }
        }
    }
    // No on-screen back button anymore (the remote's own BACK key, already
    // wired up in TvNavigation, covers it) - so the first stop takes initial
    // focus directly instead of a back button.
    LaunchedEffect(stops) {
        if (stopIndex == -1 && stops.isNotEmpty()) {
            stopIndex = 0
            // Same requestFocus() race as MovieDetailsTvScreen - see its
            // identical LaunchedEffect for the full explanation of why a
            // single attempt (even wrapped in runCatching) isn't enough.
            repeat(FOCUS_REQUEST_RETRY_FRAMES) {
                runCatching { stops[0].requestFocus() }
                withFrameNanos {}
            }
        }
    }

    LaunchedEffect(seriesId) { viewModel.loadTvSeriesDetails(seriesId) }
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
            is TvSeriesDetailsTvScreenState.Loading -> LoaderView(AppDevice.TV)
            is TvSeriesDetailsTvScreenState.Error    -> TvGenericErrorScreen(
                    message = state.errorMessage,
                    onRetryClicked = viewModel::retry
                                                                             )
            is TvSeriesDetailsTvScreenState.Content  -> TvSeriesDetailsTvScreenContent(
                    tvSeriesDetails = state.tvSeriesDetailsModel,
                    seasons = state.seasons,
                    scrollState = scrollState,
                    showFavoriteStar = viewModel.canToggleFavorite,
                    onFavoriteClicked = viewModel::onFavoriteClicked,
                    onCastMemberClicked = { selectedCastMember = it },
                    onEpisodeClicked = { selectedEpisode = it },
                    favoriteFocusRequester = favoriteFocusRequester,
                    trailerFocusRequester = trailerFocusRequester,
                    castFocusRequester = castFocusRequester,
                    seasonFocusRequesters = seasonFocusRequesters
                                                                                       )
        }
    }

    selectedCastMember?.let { castMember ->
        TvActorDetailsDialog(castMember = castMember, onDismiss = { selectedCastMember = null })
    }
    selectedEpisode?.let { episode ->
        TvEpisodeDetailsDialog(episode = episode, onDismiss = { selectedEpisode = null })
    }
}

@Composable
fun TvSeriesDetailsTvScreenContent(
        tvSeriesDetails: TvSeriesDetailsModel,
        seasons: List<SeasonModel>,
        modifier: Modifier = Modifier,
        scrollState: ScrollState = rememberScrollState(),
        showFavoriteStar: Boolean = false,
        onFavoriteClicked: () -> Unit = {},
        onCastMemberClicked: (CastMemberModel) -> Unit = {},
        onEpisodeClicked: (EpisodeModel) -> Unit = {},
        favoriteFocusRequester: FocusRequester? = null,
        trailerFocusRequester: FocusRequester? = null,
        castFocusRequester: FocusRequester? = null,
        seasonFocusRequesters: List<FocusRequester> = emptyList()
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
                        model = tvSeriesDetails.posterUrl,
                        contentDescription = tvSeriesDetails.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(10.dp))
                          )
                if (showFavoriteStar) {
                    TvFavoriteStarButton(
                            isFavorite = tvSeriesDetails.isFavorite,
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
                        text = tvSeriesDetails.title,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                TvMetaRow(icon = Icons.Default.CalendarMonth, text = airDateLabelText(tvSeriesDetails.airDateLabel))
                TvMetaRow(icon = Icons.Default.Tv, text = tvSeriesDetails.seasonsLabel)
                if (tvSeriesDetails.userScore > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        UserScoreView(score = tvSeriesDetails.userScore, size = 36.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                                text = stringResource(R.string.user_score_label),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                                style = MaterialTheme.typography.labelLarge
                            )
                    }
                }
                if (tvSeriesDetails.genres.isNotEmpty()) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        tvSeriesDetails.genres.forEach { TvGenreChip(it) }
                    }
                }
            }
        }

        if (tvSeriesDetails.overview.isNotBlank()) {
            Column(modifier = Modifier.padding(top = 32.dp)) {
                Text(
                        text = stringResource(R.string.details_overview_label),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                Text(
                        text = tvSeriesDetails.overview,
                        modifier = Modifier.padding(top = 12.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                        style = MaterialTheme.typography.bodyLarge
                    )
            }
        }

        val trailerUrl = tvSeriesDetails.trailerUrl
        if (trailerUrl != null) {
            TvTrailerSection(
                    trailerUrl = trailerUrl,
                    modifier = Modifier
                            .padding(top = 32.dp)
                            .let { if (trailerFocusRequester != null) it.focusRequester(trailerFocusRequester) else it }
                            )
        }

        if (tvSeriesDetails.creators.isNotEmpty()) {
            Column(modifier = Modifier.padding(top = 32.dp)) {
                Text(
                        text = stringResource(R.string.details_tv_crew_label),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    tvSeriesDetails.creators.forEach { CrewNameRow(it) }
                }
            }
        }

        if (tvSeriesDetails.cast.isNotEmpty()) {
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
                    itemsIndexed(tvSeriesDetails.cast, key = { _, it -> it.id }) { index, it ->
                        TvCastMemberItem(
                                it.name, it.character, it.profileUrl,
                                focusRequester = if (index == 0) castFocusRequester else null,
                                onClick = { onCastMemberClicked(it) }
                                        )
                    }
                }
            }
        }

        if (seasons.isNotEmpty()) {
            Column(modifier = Modifier.padding(top = 32.dp)) {
                Text(
                        text = stringResource(R.string.details_seasons_label),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                seasons.forEachIndexed { index, season ->
                    TvSeasonSection(
                            season,
                            modifier = Modifier.padding(top = 20.dp),
                            firstEpisodeFocusRequester = seasonFocusRequesters.getOrNull(index),
                            onEpisodeClicked = onEpisodeClicked
                                    )
                }
            }
        }

        PoweredByTmdbFooter(modifier = Modifier.padding(top = 32.dp))
    }
}

@Composable
private fun TvSeasonSection(
        season: SeasonModel,
        modifier: Modifier = Modifier,
        firstEpisodeFocusRequester: FocusRequester? = null,
        onEpisodeClicked: (EpisodeModel) -> Unit = {}
                            ) {
    Column(modifier = modifier) {
        Text(
                text = season.name,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        val metaText = listOf(season.episodeCountLabel, season.airDate).filter { it.isNotBlank() }.joinToString(" • ")
        if (metaText.isNotBlank()) {
            Text(
                    text = metaText,
                    modifier = Modifier.padding(top = 4.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall
                )
        }
        if (season.episodes.isNotEmpty()) {
            LazyRow(
                    modifier = Modifier.padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                   ) {
                itemsIndexed(season.episodes, key = { _, it -> it.id }) { index, episode ->
                    TvEpisodeItem(
                            episode,
                            focusRequester = if (index == 0) firstEpisodeFocusRequester else null,
                            onClick = { onEpisodeClicked(episode) }
                                 )
                }
            }
        }
    }
}

@Composable
private fun TvEpisodeItem(
        episode: EpisodeModel,
        modifier: Modifier = Modifier,
        focusRequester: FocusRequester? = null,
        onClick: () -> Unit = {}
                          ) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Column(modifier = modifier.width(episodeStillWidth)) {
        AsyncImage(
                model = episode.stillUrl,
                contentDescription = episode.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                        .width(episodeStillWidth)
                        .height(episodeStillHeight)
                        .clip(RoundedCornerShape(8.dp))
                        .then(
                                if (isFocused) {
                                    Modifier.border(3.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                                } else {
                                    Modifier
                                }
                             )
                        .let { if (focusRequester != null) it.focusRequester(focusRequester) else it }
                        .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
                  )
        Text(
                text = stringResource(R.string.details_episode_number_title, episode.episodeNumber, episode.name),
                modifier = Modifier.padding(top = 8.dp),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        val metaText = listOf(episode.runtime, episode.airDate).filter { it.isNotBlank() }.joinToString(" • ")
        if (metaText.isNotBlank()) {
            Text(
                    text = metaText,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall
                )
        }
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

private fun airDateLabelText(label: SeriesAirDateLabel): String = when (label) {
    is SeriesAirDateLabel.Ended    -> label.yearRange
    is SeriesAirDateLabel.Ongoing  -> label.text
    is SeriesAirDateLabel.Upcoming -> "${label.premiereLabel} ${label.dateText}".trim()
}
