package com.bk.mmovies.ui.screen.details.tvseriesdetails.screencomponents

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.bk.mmovies.R
import com.bk.mmovies.domain.model.CastMemberModel
import com.bk.mmovies.domain.model.SeasonModel
import com.bk.mmovies.domain.model.SeriesAirDateLabel
import com.bk.mmovies.domain.model.TvSeriesDetailsModel
import com.bk.mmovies.ui.component.ActorDetailsDialog
import com.bk.mmovies.ui.component.FavoriteStarButton
import com.bk.mmovies.ui.component.TrailerSection
import com.bk.mmovies.ui.component.UserScoreView

// --- Spacing scale -----------------------------------------------------------
// Mirrors DetailsScreenContent (the movie details layout) so both details
// screens read as the same design.
private val spacingUnit = 12.dp
private val spacingUnitLarge = 24.dp

private val screenPadding = 16.dp
private val posterWidth = 130.dp
private val posterHeight = 195.dp
private val posterCornerShape = 8.dp
private val favoriteStarPadding = 6.dp
private val infoColumnStartPadding = 20.dp
private val metaIconSize = 18.dp
private val metaIconTextSpacing = 8.dp
private val userScoreRingSize = 32.dp
private val userScoreRowSpacing = 12.dp

private const val TITLE_MAX_LINES = 3

private val chipSpacing = 8.dp
private val chipCornerShape = 8.dp
private val chipHorizontalPadding = 12.dp
private val chipVerticalPadding = 6.dp

private val overviewTopSpacing = spacingUnitLarge - screenPadding
private val backdropCornerShape = 12.dp
private val backdropContentPadding = 20.dp

private val overviewTitleLetterSpacing = 1.sp

private const val CHIP_CONTAINER_ALPHA = 0.08f
private const val CHIP_BORDER_ALPHA = 0.14f
private val chipBorderWidth = 1.dp

private const val SCRIM_TOP_ALPHA = 0.55f
private const val SCRIM_BOTTOM_ALPHA = 0.85f

private const val SECONDARY_TEXT_ALPHA = 0.85f
private const val TERTIARY_TEXT_ALPHA = 0.70f

private val castSectionTitleLetterSpacing = 1.sp
private val castItemWidth = 84.dp
private val castImageSize = 72.dp
private val castItemSpacing = 16.dp
private val castImageNameSpacing = 8.dp
private val castNameCharacterSpacing = 2.dp
private const val CAST_ITEM_TEXT_MAX_LINES = 2

private val crewRowSpacing = 8.dp
private val crewRoleNameSpacing = 6.dp
private val crewCreatorNameSpacing = 4.dp

private val seasonRowSpacing = 16.dp
private val seasonPosterWidth = 64.dp
private val seasonPosterHeight = 96.dp
private val seasonPosterCornerShape = 6.dp
private val seasonRowTextStartPadding = 12.dp
private val seasonRowMetaTopSpacing = 4.dp

@Composable
fun TvSeriesDetailsScreenContent(
        tvSeriesDetails: TvSeriesDetailsModel,
        modifier: Modifier = Modifier,
        showFavoriteStar: Boolean = false,
        onFavoriteClicked: () -> Unit = {},
        onSeasonClicked: (seasonNumber: Int) -> Unit = {}
                                 ) {
    // Tapping a cast/crew member opens ActorDetailsDialog over this screen —
    // the trailer would otherwise keep playing audio underneath it.
    var isCrewDialogOpen by remember { mutableStateOf(false) }
    var isCastDialogOpen by remember { mutableStateOf(false) }

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
                            model = ImageRequest.Builder(LocalContext.current)
                                    .data(tvSeriesDetails.posterUrl)
                                    .crossfade(true)
                                    .build(),
                            placeholder = painterResource(R.drawable.placeholder),
                            error = painterResource(R.drawable.placeholder),
                            contentDescription = tvSeriesDetails.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(posterCornerShape))
                              )
                    if (showFavoriteStar) {
                        FavoriteStarButton(
                                isFavorite = tvSeriesDetails.isFavorite,
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
                            text = tvSeriesDetails.title,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = TITLE_MAX_LINES,
                            overflow = TextOverflow.Ellipsis
                        )
                    AirDateMetaRow(
                            icon = Icons.Default.CalendarMonth,
                            label = tvSeriesDetails.airDateLabel
                                  )
                    MetaRow(
                            icon = Icons.Default.Tv,
                            text = tvSeriesDetails.seasonsLabel
                           )
                    // An upcoming/unaired series has no votes yet, so the
                    // score ring would only ever show a meaningless zero —
                    // same rule the movie details screen applies by category.
                    if (tvSeriesDetails.userScore > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            UserScoreView(
                                    score = tvSeriesDetails.userScore,
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
            }

            if (tvSeriesDetails.genres.isNotEmpty()) {
                FlowRow(
                        modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = spacingUnitLarge),
                        horizontalArrangement = Arrangement.spacedBy(chipSpacing),
                        verticalArrangement = Arrangement.spacedBy(chipSpacing)
                       ) {
                    tvSeriesDetails.genres.forEach { genre ->
                        GenreChip(genre)
                    }
                }
            }
        }

        if (tvSeriesDetails.overview.isNotBlank()) {
            OverviewSection(
                    overview = tvSeriesDetails.overview,
                    backdropUrl = tvSeriesDetails.backdropUrl
                           )
        }

        // Only series TMDB actually has a YouTube trailer for get the
        // player — a freshly-announced or sparse catalog entry often has no
        // video yet.
        if (tvSeriesDetails.trailerUrl != null) {
            TrailerSection(
                    trailerUrl = tvSeriesDetails.trailerUrl,
                    pause = isCrewDialogOpen || isCastDialogOpen,
                    modifier = Modifier.padding(
                            start = screenPadding,
                            end = screenPadding,
                            bottom = screenPadding
                                                )
                           )
        }

        if (tvSeriesDetails.creators.isNotEmpty()) {
            CrewSection(
                    creators = tvSeriesDetails.creators,
                    onDialogOpenChanged = { isCrewDialogOpen = it }
                       )
        }

        if (tvSeriesDetails.cast.isNotEmpty()) {
            CastSection(cast = tvSeriesDetails.cast, onDialogOpenChanged = { isCastDialogOpen = it })
        }

        if (tvSeriesDetails.seasons.isNotEmpty()) {
            SeasonsSection(seasons = tvSeriesDetails.seasons, onSeasonClicked = onSeasonClicked)
        }
    }
}

@Composable
private fun CrewSection(
        creators: List<CastMemberModel>,
        modifier: Modifier = Modifier,
        onDialogOpenChanged: (Boolean) -> Unit = {}
                       ) {
    // Owned here rather than passed down, since only this section's rows
    // can ever open it. Reuses ActorDetailsDialog: creator credits carry
    // the same TMDB person id, so the same bio lookup applies.
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
                text = stringResource(R.string.details_tv_crew_label),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = SECONDARY_TEXT_ALPHA),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = castSectionTitleLetterSpacing
            )
        if (creators.size > 1) {
            // Repeating "Creator" per row read as noise once a show credits
            // several — one "Creators:" row with each name still separately
            // clickable reads the same as the single-creator case below.
            CreatorsRow(
                    creators = creators,
                    onCreatorClicked = { creator -> selectedCrewMember = creator }
                       )
        } else {
            creators.forEach { creator ->
                CrewMemberRow(
                        role = stringResource(R.string.details_creator_role),
                        crewMember = creator,
                        onClick = { selectedCrewMember = creator }
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
private fun CreatorsRow(
        creators: List<CastMemberModel>,
        onCreatorClicked: (CastMemberModel) -> Unit,
        modifier: Modifier = Modifier
                       ) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
                text = stringResource(R.string.details_creators_label),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = TERTIARY_TEXT_ALPHA),
                style = MaterialTheme.typography.labelMedium
            )
        Spacer(modifier = Modifier.width(crewRoleNameSpacing))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(crewCreatorNameSpacing)) {
            creators.forEachIndexed { index, creator ->
                val nameText = if (index < creators.lastIndex) "${creator.name}," else creator.name
                Text(
                        text = nameText,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable(
                                onClickLabel = creator.name
                                                      ) { onCreatorClicked(creator) }
                    )
            }
        }
    }
}

@Composable
private fun CrewMemberRow(
        role: String,
        crewMember: CastMemberModel,
        onClick: () -> Unit,
        modifier: Modifier = Modifier
                         ) {
    Row(
            modifier = modifier.clickable(onClickLabel = crewMember.name, onClick = onClick),
            verticalAlignment = Alignment.CenterVertically
       ) {
        Text(
                text = role,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = TERTIARY_TEXT_ALPHA),
                style = MaterialTheme.typography.labelMedium
            )
        Spacer(modifier = Modifier.width(crewRoleNameSpacing))
        Text(
                text = crewMember.name,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
    }
}

@Composable
private fun CastSection(
        cast: List<CastMemberModel>,
        modifier: Modifier = Modifier,
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
    }

    selectedCastMember?.let { castMember ->
        ActorDetailsDialog(
                castMember = castMember,
                onDismiss = { selectedCastMember = null }
                           )
    }
}

@Composable
private fun CastMemberItem(castMember: CastMemberModel, onClick: () -> Unit) {
    Column(
            modifier = Modifier
                    .width(castItemWidth)
                    .clickable(onClickLabel = castMember.name, onClick = onClick),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
        AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                        .data(castMember.profileUrl)
                        .crossfade(true)
                        .build(),
                placeholder = painterResource(R.drawable.placeholder),
                error = painterResource(R.drawable.placeholder),
                contentDescription = castMember.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                        .size(castImageSize)
                        .clip(CircleShape)
                  )
        Text(
                text = castMember.name,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = castImageNameSpacing)
            )
        Text(
                text = castMember.character,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = TERTIARY_TEXT_ALPHA),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                maxLines = CAST_ITEM_TEXT_MAX_LINES,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = castNameCharacterSpacing)
            )
    }
}

@Composable
private fun SeasonsSection(
        seasons: List<SeasonModel>,
        modifier: Modifier = Modifier,
        onSeasonClicked: (seasonNumber: Int) -> Unit = {}
                          ) {
    Column(
            modifier = modifier.padding(
                    start = screenPadding,
                    end = screenPadding,
                    bottom = screenPadding
                                        )
          ) {
        Text(
                text = stringResource(R.string.details_seasons_label),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = SECONDARY_TEXT_ALPHA),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = castSectionTitleLetterSpacing
            )
        Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = spacingUnit),
                verticalArrangement = Arrangement.spacedBy(seasonRowSpacing)
              ) {
            seasons.forEach { season ->
                SeasonRow(season, onClick = { onSeasonClicked(season.seasonNumber) })
            }
        }
    }
}

@Composable
private fun SeasonRow(season: SeasonModel, onClick: () -> Unit) {
    Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                            onClickLabel = stringResource(R.string.movie_open_details_action)
                              ) { onClick() },
            verticalAlignment = Alignment.CenterVertically
       ) {
        AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                        .data(season.posterUrl)
                        .crossfade(true)
                        .build(),
                placeholder = painterResource(R.drawable.placeholder),
                error = painterResource(R.drawable.placeholder),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                        .size(width = seasonPosterWidth, height = seasonPosterHeight)
                        .clip(RoundedCornerShape(seasonPosterCornerShape))
                  )
        Column(
                modifier = Modifier
                        .weight(1f)
                        .padding(start = seasonRowTextStartPadding)
              ) {
            Text(
                    text = season.name,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            // The mapper yields "" for a missing count/date; rendering an
            // empty line anyway left an unexplained gap under the name.
            val metaText = listOf(season.episodeCountLabel, season.airDate)
                    .filter { it.isNotBlank() }
                    .joinToString(" • ")
            if (metaText.isNotBlank()) {
                Text(
                        text = metaText,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = SECONDARY_TEXT_ALPHA),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = seasonRowMetaTopSpacing)
                    )
            }
        }
    }
}

@Composable
private fun AirDateMetaRow(
        icon: ImageVector,
        label: SeriesAirDateLabel,
        modifier: Modifier = Modifier
                          ) {
    val isBlank = when (label) {
        is SeriesAirDateLabel.Ended    -> label.yearRange.isBlank()
        is SeriesAirDateLabel.Ongoing  -> label.text.isBlank()
        is SeriesAirDateLabel.Upcoming -> false
    }
    if (isBlank) return

    Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
       ) {
        Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = SECONDARY_TEXT_ALPHA),
                modifier = Modifier.size(metaIconSize)
            )
        Spacer(modifier = Modifier.width(metaIconTextSpacing))
        when (label) {
            is SeriesAirDateLabel.Ended    -> {
                Text(
                        text = label.yearRange,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = SECONDARY_TEXT_ALPHA),
                        style = MaterialTheme.typography.bodyMedium
                    )
            }
            is SeriesAirDateLabel.Ongoing  -> {
                Text(
                        text = label.text,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = SECONDARY_TEXT_ALPHA),
                        style = MaterialTheme.typography.bodyMedium
                    )
            }
            is SeriesAirDateLabel.Upcoming -> {
                Column {
                    Text(
                            text = label.premiereLabel,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = SECONDARY_TEXT_ALPHA),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    Text(
                            text = label.dateText,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = SECONDARY_TEXT_ALPHA),
                            style = MaterialTheme.typography.bodyMedium
                        )
                }
            }
        }
    }
}

@Composable
private fun MetaRow(
        icon: ImageVector,
        text: String,
        modifier: Modifier = Modifier
                   ) {
    if (text.isBlank()) return

    Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
       ) {
        Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = SECONDARY_TEXT_ALPHA),
                modifier = Modifier.size(metaIconSize)
            )
        Spacer(modifier = Modifier.width(metaIconTextSpacing))
        Text(
                text = text,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = SECONDARY_TEXT_ALPHA),
                style = MaterialTheme.typography.bodyMedium
            )
    }
}

@Composable
private fun GenreChip(genre: String) {
    Text(
            text = genre,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = TERTIARY_TEXT_ALPHA),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier
                    .clip(RoundedCornerShape(chipCornerShape))
                    .background(
                            MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = CHIP_CONTAINER_ALPHA
                                                                    )
                               )
                    .border(
                            width = chipBorderWidth,
                            color = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = CHIP_BORDER_ALPHA
                                                                            ),
                            shape = RoundedCornerShape(chipCornerShape)
                           )
                    .padding(
                            horizontal = chipHorizontalPadding,
                            vertical = chipVerticalPadding
                            )
        )
}

@Composable
private fun OverviewSection(
        overview: String,
        backdropUrl: String,
        modifier: Modifier = Modifier
                           ) {
    Box(
            modifier = modifier
                    .fillMaxWidth()
                    .padding(
                            start = screenPadding,
                            end = screenPadding,
                            top = overviewTopSpacing,
                            bottom = screenPadding
                            )
                    .clip(RoundedCornerShape(backdropCornerShape))
       ) {
        AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                        .data(backdropUrl)
                        .crossfade(true)
                        .build(),
                placeholder = painterResource(R.drawable.placeholder),
                error = painterResource(R.drawable.placeholder),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
                  )
        Box(
                modifier = Modifier
                        .matchParentSize()
                        .background(
                                Brush.verticalGradient(
                                        colors = listOf(
                                                Color.Black.copy(alpha = SCRIM_TOP_ALPHA),
                                                Color.Black.copy(alpha = SCRIM_BOTTOM_ALPHA)
                                                       )
                                                      )
                                   )
           )
        Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(backdropContentPadding)
              ) {
            Text(
                    text = stringResource(R.string.details_overview_label),
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = overviewTitleLetterSpacing
                )
            Text(
                    text = overview,
                    color = Color.White.copy(alpha = SECONDARY_TEXT_ALPHA),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = spacingUnit)
                )
        }
    }
}
