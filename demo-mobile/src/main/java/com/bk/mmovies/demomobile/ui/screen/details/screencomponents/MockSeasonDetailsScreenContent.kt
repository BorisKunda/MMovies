package com.bk.mmovies.demomobile.ui.screen.details.screencomponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.bk.mmovies.core.R as CoreR
import com.bk.mmovies.demomobile.R
import com.bk.mmovies.demomobile.ui.component.rememberMockImageModel
import com.bk.mmovies.domain.model.EpisodeModel
import com.bk.mmovies.domain.model.SeasonModel

private val screenPadding = 16.dp
private val spacingUnit = 12.dp
private val spacingUnitLarge = 24.dp

private val posterWidth = 130.dp
private val posterHeight = 195.dp
private val posterCornerShape = 8.dp
private val infoColumnStartPadding = 20.dp

private const val TITLE_MAX_LINES = 3

private val overviewTitleLetterSpacing = 1.sp
private const val SECONDARY_TEXT_ALPHA = 0.85f
private const val TERTIARY_TEXT_ALPHA = 0.70f

private val episodesSectionTitleLetterSpacing = 1.sp
private val episodeRowSpacing = 16.dp
private val episodeStillWidth = 64.dp
private val episodeStillHeight = 96.dp
private val episodeStillCornerShape = 6.dp
private val episodeRowTextStartPadding = 12.dp
private val episodeRowMetaTopSpacing = 4.dp
private val episodeOverviewTopSpacing = 4.dp
private const val EPISODE_OVERVIEW_MAX_LINES = 2

@Composable
fun MockSeasonDetailsScreenContent(
        season: SeasonModel,
        personBiographies: Map<Int, String>,
        modifier: Modifier = Modifier
                                  ) {
    var selectedEpisode by remember { mutableStateOf<EpisodeModel?>(null) }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        item {
            SeasonHeader(season)
        }

        if (season.overview.isNotBlank()) {
            item {
                SeasonOverview(season.overview)
            }
        }

        if (season.episodes.isNotEmpty()) {
            item {
                Text(
                        text = stringResource(R.string.details_episodes_label),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = SECONDARY_TEXT_ALPHA),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = episodesSectionTitleLetterSpacing,
                        modifier = Modifier.padding(
                                start = screenPadding,
                                end = screenPadding,
                                top = spacingUnitLarge - screenPadding,
                                bottom = spacingUnit
                                                    )
                    )
            }
            items(season.episodes, key = { it.id }) { episode ->
                EpisodeRow(
                        episode = episode,
                        onClick = { selectedEpisode = episode },
                        modifier = Modifier.padding(
                                start = screenPadding,
                                end = screenPadding,
                                bottom = episodeRowSpacing
                                                    )
                          )
            }
        }
    }

    selectedEpisode?.let { episode ->
        MockEpisodeDetailsDialog(
                episode = episode,
                personBiographies = personBiographies,
                onDismiss = { selectedEpisode = null }
                                )
    }
}

@Composable
private fun SeasonHeader(season: SeasonModel) {
    Column(modifier = Modifier.padding(screenPadding)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                    modifier = Modifier.size(
                            width = posterWidth,
                            height = posterHeight
                                            )
               ) {
                AsyncImage(
                        model = rememberMockImageModel(season.posterUrl),
                        placeholder = painterResource(CoreR.drawable.placeholder),
                        error = painterResource(CoreR.drawable.placeholder),
                        contentDescription = season.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(posterCornerShape))
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
                        text = season.name,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = TITLE_MAX_LINES,
                        overflow = TextOverflow.Ellipsis
                    )
                val metaText = listOf(season.episodeCountLabel, season.airDate)
                        .filter { it.isNotBlank() }
                        .joinToString(" • ")
                if (metaText.isNotBlank()) {
                    Text(
                            text = metaText,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = SECONDARY_TEXT_ALPHA),
                            style = MaterialTheme.typography.bodyMedium
                        )
                }
            }
        }
    }
}

@Composable
private fun SeasonOverview(overview: String) {
    Column(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                            start = screenPadding,
                            end = screenPadding,
                            bottom = screenPadding
                            )
          ) {
        Text(
                text = stringResource(R.string.details_overview_label),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = SECONDARY_TEXT_ALPHA),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = overviewTitleLetterSpacing
            )
        Text(
                text = overview,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = SECONDARY_TEXT_ALPHA),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = spacingUnit)
            )
    }
}

@Composable
private fun EpisodeRow(
        episode: EpisodeModel,
        onClick: () -> Unit,
        modifier: Modifier = Modifier
                       ) {
    Row(
            modifier = modifier
                    .fillMaxWidth()
                    .clickable(
                            onClickLabel = stringResource(
                                    R.string.details_episode_number_title,
                                    episode.episodeNumber,
                                    episode.name
                                                         )
                              ) { onClick() },
            verticalAlignment = Alignment.CenterVertically
       ) {
        AsyncImage(
                model = rememberMockImageModel(episode.stillUrl),
                placeholder = painterResource(CoreR.drawable.placeholder),
                error = painterResource(CoreR.drawable.placeholder),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                        .size(width = episodeStillWidth, height = episodeStillHeight)
                        .clip(RoundedCornerShape(episodeStillCornerShape))
                  )
        Column(
                modifier = Modifier
                        .weight(1f)
                        .padding(start = episodeRowTextStartPadding)
              ) {
            Text(
                    text = stringResource(
                            R.string.details_episode_number_title,
                            episode.episodeNumber,
                            episode.name
                                         ),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            val metaText = listOf(episode.runtime, episode.airDate)
                    .filter { it.isNotBlank() }
                    .joinToString(" • ")
            if (metaText.isNotBlank()) {
                Text(
                        text = metaText,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = SECONDARY_TEXT_ALPHA),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = episodeRowMetaTopSpacing)
                    )
            }
            if (episode.overview.isNotBlank()) {
                Text(
                        text = episode.overview,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = TERTIARY_TEXT_ALPHA),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = EPISODE_OVERVIEW_MAX_LINES,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = episodeOverviewTopSpacing)
                    )
            }
        }
    }
}
