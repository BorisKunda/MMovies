package com.bk.mmovies.ui.screen.details.moviedetails.screencomponents

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.bk.mmovies.R
import com.bk.mmovies.data.source.remote.POSTER_PATH_SIZE_SEGMENT_LIST_ITEM
import com.bk.mmovies.data.source.remote.POSTER_PATH_SIZE_SEGMENT_LIST_ITEM_ZOOM
import com.bk.mmovies.domain.model.CastMemberModel
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.domain.model.MovieDetailsModel
import com.bk.mmovies.ui.component.ActorDetailsDialog
import com.bk.mmovies.ui.component.CastMemberItem
import com.bk.mmovies.ui.component.CrewMemberRow
import com.bk.mmovies.ui.component.FavoriteStarButton
import com.bk.mmovies.ui.component.GenreChip
import com.bk.mmovies.ui.component.MetaRow
import com.bk.mmovies.ui.component.MultiNameCrewRow
import com.bk.mmovies.ui.component.OverviewSection
import com.bk.mmovies.ui.component.TrailerSection
import com.bk.mmovies.ui.component.UserScoreView

// --- Spacing scale -----------------------------------------------------------
// A single consistent step keeps the vertical rhythm tight and deliberate
// instead of the uneven 10/28/24 gaps the old layout used.
private val spacingUnit = 12.dp           // base step
private val spacingUnitLarge = 24.dp      // 2x base, for section breaks

private val screenPadding = 16.dp
private val posterWidth = 130.dp
private val posterHeight = 195.dp         // kept at a true 2:3 poster ratio
private val posterCornerShape = 8.dp
private val zoomedPosterPadding = 24.dp
private val favoriteStarPadding = 6.dp
private val infoColumnStartPadding = 20.dp
private val userScoreRingSize =
        32.dp     // UserScoreView scales its numeral and ring with this
private val userScoreRowSpacing = 12.dp

// A long title must not out-grow the poster it sits beside, or the Row's
// CenterVertically ends up centring the poster against a wall of text.
private const val TITLE_MAX_LINES = 3

private val chipSpacing = 8.dp

private const val SECONDARY_TEXT_ALPHA = 0.85f

private val castSectionTitleLetterSpacing = 1.sp
private val castItemSpacing = 16.dp

private val crewRowSpacing = 8.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MovieDetailsScreenContent(
        movieDetails: MovieDetailsModel,
        category: MovieCategory,
        modifier: Modifier = Modifier,
        showFavoriteStar: Boolean = false,
        onFavoriteClicked: () -> Unit = {}
                             ) {
    // Upcoming releases routinely lack a score, runtime, backdrop or overview
    // on TMDB, so this category gets a "coming soon" presentation instead of
    // hiding fields the way the sparse-data fallback does for other categories.
    val isUpcoming = category == MovieCategory.UpcomingMovieCategory
    // Unreleased movies can't be favorited/rated, matching the same rule
    // applied to the grid's poster star.
    val showStar = showFavoriteStar && !isUpcoming
    val dateTbaLabel = stringResource(R.string.details_date_tba)
    val runtimeTbaLabel = stringResource(R.string.details_runtime_tba)
    val overviewTbaLabel = stringResource(R.string.details_overview_tba)
    val castTbaLabel = stringResource(R.string.details_cast_tba)

    // Tapping a cast/crew member opens ActorDetailsDialog over this screen —
    // the trailer would otherwise keep playing audio underneath it.
    var isCrewDialogOpen by remember { mutableStateOf(false) }
    var isCastDialogOpen by remember { mutableStateOf(false) }
    // Same long-press-to-zoom affordance as the catalog list's poster.
    var isPosterZoomed by remember { mutableStateOf(false) }

    Column(
            modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())   // never let content overflow the screen
          ) {
        Column(modifier = Modifier.padding(screenPadding)) {
            // Center the info block against the poster so the shorter text
            // column no longer leaves a void beside the poster's lower half.
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                        modifier = Modifier.size(
                                width = posterWidth,
                                height = posterHeight
                                                )
                   ) {
                    AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                    .data(movieDetails.posterUrl)
                                    .crossfade(true)
                                    .build(),
                            placeholder = painterResource(R.drawable.placeholder),
                            error = painterResource(R.drawable.placeholder),
                            contentDescription = movieDetails.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(posterCornerShape))
                                    .combinedClickable(
                                            onLongClickLabel = stringResource(R.string.movie_poster_zoom_action),
                                            onClick = {},
                                            onLongClick = { isPosterZoomed = true }
                                                      )
                              )
                    if (showStar) {
                        FavoriteStarButton(
                                isFavorite = movieDetails.isFavorite,
                                onClick = onFavoriteClicked,
                                modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(favoriteStarPadding)
                                           )
                    }
                }
                Column(
                        modifier = Modifier
                                .weight(1f)
                                .padding(start = infoColumnStartPadding),
                        verticalArrangement = Arrangement.spacedBy(spacingUnit),
                        horizontalAlignment = Alignment.Start
                      ) {
                    Text(
                            text = movieDetails.title,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = TITLE_MAX_LINES,
                            overflow = TextOverflow.Ellipsis
                        )
                    MetaRow(
                            icon = Icons.Default.CalendarMonth,
                            text = movieDetails.releaseDate.ifBlank { if (isUpcoming) dateTbaLabel else "" }
                           )
                    MetaRow(
                            icon = Icons.Default.Schedule,
                            text = movieDetails.runtime.ifBlank { if (isUpcoming) runtimeTbaLabel else "" }
                           )
                    // Upcoming movies have no votes yet, so the score ring
                    // would only ever show a meaningless zero.
                    if (!isUpcoming) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            UserScoreView(
                                    score = movieDetails.userScore,
                                    size = userScoreRingSize
                                         )
                            Spacer(modifier = Modifier.width(userScoreRowSpacing))
                            // Same caption treatment as the list badge in MovieRow,
                            // just sized for this screen.
                            Text(
                                    text = stringResource(R.string.user_score_label),
                                    color = MaterialTheme.colorScheme.onSurface.copy(
                                            alpha = SECONDARY_TEXT_ALPHA
                                                                                    ),
                                    style = MaterialTheme.typography.labelLarge
                                )
                        }
                    }
                }
            }

            // An empty FlowRow would still apply its top padding, leaving a
            // floating 24dp gap under the header for movies with no genres.
            if (movieDetails.genres.isNotEmpty()) {
                FlowRow(
                        modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = spacingUnitLarge),
                        horizontalArrangement = Arrangement.spacedBy(chipSpacing),
                        verticalArrangement = Arrangement.spacedBy(chipSpacing)
                       ) {
                    movieDetails.genres.forEach { genre ->
                        GenreChip(genre)
                    }
                }
            }
        }

        // Without an overview the card would collapse to the label plus its
        // padding — a thin letterboxed strip of backdrop with nothing in it.
        // Upcoming movies are the exception: the card still renders with a
        // "To be announced" placeholder instead of disappearing entirely.
        val overviewText = movieDetails.overview.ifBlank { if (isUpcoming) overviewTbaLabel else "" }
        if (overviewText.isNotBlank()) {
            OverviewSection(
                    overview = overviewText,
                    backdropUrl = movieDetails.backdropUrl
                           )
        }

        // Only movies TMDB actually has a YouTube trailer for get the
        // player — an upcoming release or a sparse catalog entry often has
        // no video yet.
        if (movieDetails.trailerUrl != null) {
            TrailerSection(
                    trailerUrl = movieDetails.trailerUrl,
                    pause = isCrewDialogOpen || isCastDialogOpen,
                    modifier = Modifier.padding(
                            start = screenPadding,
                            end = screenPadding,
                            bottom = screenPadding
                                                )
                           )
        }

        if (movieDetails.director != null || movieDetails.writers.isNotEmpty()) {
            CrewSection(
                    director = movieDetails.director,
                    writers = movieDetails.writers,
                    onDialogOpenChanged = { isCrewDialogOpen = it }
                       )
        }

        // Upcoming movies routinely have no cast credited yet on TMDB; show
        // a TBA line instead of silently dropping the section like the
        // sparse-data fallback does for other categories.
        if (movieDetails.cast.isNotEmpty()) {
            CastSection(cast = movieDetails.cast, onDialogOpenChanged = { isCastDialogOpen = it })
        } else if (isUpcoming) {
            CastSection(cast = emptyList(), tbaLabel = castTbaLabel)
        }
    }

    if (isPosterZoomed) {
        Dialog(
                onDismissRequest = { isPosterZoomed = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
              ) {
            Box(
                    modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.9f))
                            .clickable { isPosterZoomed = false },
                    contentAlignment = Alignment.Center
               ) {
                AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                                .data(
                                        movieDetails.posterUrl.replace(
                                                POSTER_PATH_SIZE_SEGMENT_LIST_ITEM,
                                                POSTER_PATH_SIZE_SEGMENT_LIST_ITEM_ZOOM
                                                                      )
                                     )
                                .crossfade(true)
                                .build(),
                        placeholder = painterResource(R.drawable.placeholder),
                        error = painterResource(R.drawable.placeholder),
                        contentDescription = movieDetails.title,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                                .fillMaxWidth()
                                .padding(zoomedPosterPadding)
                                .aspectRatio(posterWidth / posterHeight)
                                .clip(RoundedCornerShape(posterCornerShape))
                          )
            }
        }
    }
}

@Composable
private fun CrewSection(
        director: CastMemberModel?,
        writers: List<CastMemberModel>,
        modifier: Modifier = Modifier,
        onDialogOpenChanged: (Boolean) -> Unit = {}
                       ) {
    // Owned here rather than passed down, since only this section's rows
    // can ever open it. Reuses ActorDetailsDialog: director/writer credits
    // carry the same TMDB person id, so the same bio lookup applies.
    var selectedCrewMember by remember { mutableStateOf<CastMemberModel?>(null) }
    LaunchedEffect(selectedCrewMember) { onDialogOpenChanged(selectedCrewMember != null) }

    Column(
            modifier = modifier.padding(
                    start = screenPadding,
                    end = screenPadding,
                    bottom = screenPadding
                                        ),
            verticalArrangement = Arrangement.spacedBy(crewRowSpacing)
          ) {
        Text(
                text = stringResource(R.string.details_crew_label),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = SECONDARY_TEXT_ALPHA),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = castSectionTitleLetterSpacing
            )
        director?.let {
            CrewMemberRow(
                    role = stringResource(R.string.details_director_role),
                    crewMember = it,
                    onClick = { selectedCrewMember = it }
                         )
        }
        if (writers.size > 1) {
            // Repeating "Writer" per row read as noise once a movie credits
            // several — one "Writers:" row with each name still separately
            // clickable reads the same as the single-writer case below.
            MultiNameCrewRow(
                    role = stringResource(R.string.details_writers_label),
                    crewMembers = writers,
                    onCrewMemberClicked = { writer -> selectedCrewMember = writer }
                             )
        } else {
            writers.forEach { writer ->
                CrewMemberRow(
                        role = stringResource(R.string.details_writer_role),
                        crewMember = writer,
                        onClick = { selectedCrewMember = writer }
                             )
            }
        }
    }

    selectedCrewMember?.let { crewMember ->
        ActorDetailsDialog(
                castMember = crewMember,
                onDismiss = { selectedCrewMember = null }
                           )
    }
}

@Composable
private fun CastSection(
        cast: List<CastMemberModel>,
        modifier: Modifier = Modifier,
        tbaLabel: String? = null,
        onDialogOpenChanged: (Boolean) -> Unit = {}
                       ) {
    // Owned here rather than passed down, since only this section's items
    // can ever open it.
    var selectedCastMember by remember { mutableStateOf<CastMemberModel?>(null) }
    LaunchedEffect(selectedCastMember) { onDialogOpenChanged(selectedCastMember != null) }

    Column(
            modifier = modifier.padding(
                    start = screenPadding,
                    end = screenPadding,
                    bottom = screenPadding
                                        )
          ) {
        Text(
                text = stringResource(R.string.details_cast_label),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = SECONDARY_TEXT_ALPHA),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = castSectionTitleLetterSpacing
            )
        if (cast.isNotEmpty()) {
            LazyRow(
                    modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = spacingUnit),
                    horizontalArrangement = Arrangement.spacedBy(castItemSpacing)
                   ) {
                items(cast, key = { it.id }) { castMember ->
                    CastMemberItem(
                            castMember = castMember,
                            onClick = { selectedCastMember = castMember }
                                  )
                }
            }
        } else if (tbaLabel != null) {
            Text(
                    text = tbaLabel,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = SECONDARY_TEXT_ALPHA),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = spacingUnit)
                )
        }
    }

    selectedCastMember?.let { castMember ->
        ActorDetailsDialog(
                castMember = castMember,
                onDismiss = { selectedCastMember = null }
                           )
    }
}

