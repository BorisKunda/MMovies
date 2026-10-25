package com.bk.mmovies.demomobile.ui.screen.details.screencomponents

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import com.bk.mmovies.core.R as CoreR
import com.bk.mmovies.demomobile.R
import com.bk.mmovies.demomobile.ui.component.rememberMockImageModel
import com.bk.mmovies.domain.model.CastMemberModel
import com.bk.mmovies.domain.model.EpisodeModel
import com.bk.mmovies.ui.component.UserScoreView

private val dialogWidthFraction = 0.92f
private val dialogHeightFraction = 0.88f
private val dialogCornerShape = 20.dp
private val contentPadding = 20.dp
private val titleToMetaSpacing = 8.dp
private val metaToOverviewSpacing = 16.dp
private val scoreRingSize = 32.dp
private val scoreRowSpacing = 12.dp
private val crewSectionTitleTopSpacing = 16.dp
private val crewRowSpacing = 8.dp

private const val SCRIM_ALPHA = 0.7f
private const val SECONDARY_TEXT_ALPHA = 0.85f

@Composable
fun MockEpisodeDetailsDialog(
        episode: EpisodeModel,
        personBiographies: Map<Int, String>,
        onDismiss: () -> Unit
                            ) {
    Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false)
          ) {
        BoxWithConstraints {
            Surface(
                    modifier = Modifier
                            .fillMaxWidth(dialogWidthFraction)
                            .heightIn(max = maxHeight * dialogHeightFraction),
                    shape = RoundedCornerShape(dialogCornerShape),
                    color = MaterialTheme.colorScheme.surface
                   ) {
                Column(
                        modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                      ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        AsyncImage(
                                model = rememberMockImageModel(episode.stillUrl),
                                placeholder = painterResource(CoreR.drawable.placeholder),
                                error = painterResource(CoreR.drawable.placeholder),
                                contentDescription = null,
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(
                                                RoundedCornerShape(
                                                        topStart = dialogCornerShape,
                                                        topEnd = dialogCornerShape
                                                                   )
                                             )
                                  )
                        IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.align(Alignment.TopEnd)
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
                                UserScoreView(
                                        score = episode.rating,
                                        size = scoreRingSize
                                             )
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
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(top = metaToOverviewSpacing)
                                )
                        }

                        if (episode.director != null || episode.writers.isNotEmpty()) {
                            CrewSection(
                                    director = episode.director,
                                    writers = episode.writers,
                                    personBiographies = personBiographies
                                       )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CrewSection(
        director: CastMemberModel?,
        writers: List<CastMemberModel>,
        personBiographies: Map<Int, String>
                       ) {
    var selectedCrewMember by remember { mutableStateOf<CastMemberModel?>(null) }

    Column(
            modifier = Modifier.padding(top = crewSectionTitleTopSpacing),
            verticalArrangement = Arrangement.spacedBy(crewRowSpacing)
          ) {
        director?.let {
            CrewMemberRow(
                    role = stringResource(R.string.details_director_role),
                    crewMember = it,
                    onClick = { selectedCrewMember = it }
                         )
        }
        if (writers.size > 1) {
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
        MockActorDetailsDialog(
                castMember = crewMember,
                biography = personBiographies[crewMember.id].orEmpty(),
                onDismiss = { selectedCrewMember = null }
                              )
    }
}
