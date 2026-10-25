package com.bk.mmovies.demomobile.ui.screen.details.screencomponents

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import com.bk.mmovies.core.R as CoreR
import com.bk.mmovies.demomobile.R
import com.bk.mmovies.demomobile.ui.component.rememberMockImageModel
import com.bk.mmovies.demomobile.ui.screen.catalog.screencomponents.FavoriteStarButton
import com.bk.mmovies.domain.model.CastMemberModel
import com.bk.mmovies.domain.model.MovieDetailsModel
import com.bk.mmovies.ui.component.UserScoreView

private val spacingUnit = 12.dp
private val spacingUnitLarge = 24.dp

private val screenPadding = 16.dp
private val posterWidth = 130.dp
private val posterHeight = 195.dp
private val posterCornerShape = 8.dp
private val zoomedPosterPadding = 24.dp
private val favoriteStarPadding = 6.dp
private val infoColumnStartPadding = 20.dp
private val userScoreRingSize = 32.dp
private val userScoreRowSpacing = 12.dp

private const val TITLE_MAX_LINES = 3

private val chipSpacing = 8.dp

private const val SECONDARY_TEXT_ALPHA = 0.85f

private val castSectionTitleLetterSpacing = 1.sp
private val castItemSpacing = 16.dp

private val crewRowSpacing = 8.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MockItemDetailsScreenContent(
        movieDetails: MovieDetailsModel,
        personBiographies: Map<Int, String>,
        onTrailerClicked: () -> Unit,
        modifier: Modifier = Modifier,
        onFavoriteClicked: () -> Unit = {}
                                ) {
    var isPosterZoomed by remember { mutableStateOf(false) }
    var selectedPerson by remember { mutableStateOf<CastMemberModel?>(null) }

    Column(
            modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
          ) {
        Column(modifier = Modifier.padding(screenPadding)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                        modifier = Modifier.size(
                                width = posterWidth,
                                height = posterHeight
                                                )
                   ) {
                    AsyncImage(
                            model = rememberMockImageModel(movieDetails.posterUrl),
                            placeholder = painterResource(CoreR.drawable.placeholder),
                            error = painterResource(CoreR.drawable.placeholder),
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
                    FavoriteStarButton(
                            isFavorite = movieDetails.isFavorite,
                            onClick = onFavoriteClicked,
                            modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(favoriteStarPadding)
                                      )
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
                            text = movieDetails.releaseDate
                           )
                    MetaRow(
                            icon = Icons.Default.Schedule,
                            text = movieDetails.runtime
                           )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        UserScoreView(
                                score = movieDetails.userScore,
                                size = userScoreRingSize
                                     )
                        Spacer(modifier = Modifier.width(userScoreRowSpacing))
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

        if (movieDetails.overview.isNotBlank()) {
            OverviewSection(
                    overview = movieDetails.overview,
                    backdropUrl = movieDetails.backdropUrl
                           )
        }

        MockTrailerSection(
                posterUrl = movieDetails.posterUrl,
                onClick = onTrailerClicked,
                modifier = Modifier.padding(
                        start = screenPadding,
                        end = screenPadding,
                        bottom = screenPadding
                                           )
                          )

        if (movieDetails.director != null || movieDetails.writers.isNotEmpty()) {
            CrewSection(
                    director = movieDetails.director,
                    writers = movieDetails.writers,
                    onCrewMemberClicked = { selectedPerson = it }
                       )
        }

        if (movieDetails.cast.isNotEmpty()) {
            CastSection(
                    cast = movieDetails.cast,
                    onCastMemberClicked = { selectedPerson = it }
                       )
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
                        model = rememberMockImageModel(movieDetails.posterUrl),
                        placeholder = painterResource(CoreR.drawable.placeholder),
                        error = painterResource(CoreR.drawable.placeholder),
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

    selectedPerson?.let { person ->
        MockActorDetailsDialog(
                castMember = person,
                biography = personBiographies[person.id].orEmpty(),
                onDismiss = { selectedPerson = null }
                              )
    }
}

@Composable
private fun CrewSection(
        director: CastMemberModel?,
        writers: List<CastMemberModel>,
        onCrewMemberClicked: (CastMemberModel) -> Unit,
        modifier: Modifier = Modifier
                       ) {
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
                    onClick = { onCrewMemberClicked(it) }
                         )
        }
        if (writers.size > 1) {
            MultiNameCrewRow(
                    role = stringResource(R.string.details_writers_label),
                    crewMembers = writers,
                    onCrewMemberClicked = onCrewMemberClicked
                             )
        } else {
            writers.forEach { writer ->
                CrewMemberRow(
                        role = stringResource(R.string.details_writer_role),
                        crewMember = writer,
                        onClick = { onCrewMemberClicked(writer) }
                             )
            }
        }
    }
}

@Composable
private fun CastSection(
        cast: List<CastMemberModel>,
        onCastMemberClicked: (CastMemberModel) -> Unit,
        modifier: Modifier = Modifier
                       ) {
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
        LazyRow(
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = spacingUnit),
                horizontalArrangement = Arrangement.spacedBy(castItemSpacing)
               ) {
            items(cast, key = { it.id }) { castMember ->
                CastMemberItem(
                        castMember = castMember,
                        onClick = { onCastMemberClicked(castMember) }
                              )
            }
        }
    }
}
