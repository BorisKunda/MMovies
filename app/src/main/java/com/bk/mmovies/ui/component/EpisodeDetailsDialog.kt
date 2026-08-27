package com.bk.mmovies.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.bk.mmovies.R
import com.bk.mmovies.domain.model.CastMemberModel
import com.bk.mmovies.domain.model.EpisodeModel

private val dialogWidthFraction = 0.92f
private val dialogHeightFraction = 0.88f
private val dialogCornerShape = 20.dp
private val photoHeight = 220.dp
private val contentPadding = 20.dp
private val titleToMetaSpacing = 8.dp
private val metaToOverviewSpacing = 16.dp
private val scoreRingSize = 32.dp
private val scoreRowSpacing = 12.dp
private val crewSectionTitleTopSpacing = 16.dp
private val crewRowSpacing = 8.dp
private val crewRoleNameSpacing = 6.dp
private val crewMultiNameSpacing = 4.dp

private const val SCRIM_ALPHA = 0.7f
private const val SECONDARY_TEXT_ALPHA = 0.85f
private const val TERTIARY_TEXT_ALPHA = 0.70f

@Composable
fun EpisodeDetailsDialog(
        episode: EpisodeModel,
        onDismiss: () -> Unit
                        ) {
    Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false)
          ) {
        Surface(
                modifier = Modifier
                        .fillMaxWidth(dialogWidthFraction)
                        .fillMaxHeight(dialogHeightFraction),
                shape = RoundedCornerShape(dialogCornerShape),
                color = MaterialTheme.colorScheme.surface
               ) {
            Column(
                    modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                  ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                    .data(episode.stillUrl)
                                    .crossfade(true)
                                    .build(),
                            placeholder = painterResource(R.drawable.placeholder),
                            error = painterResource(R.drawable.placeholder),
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
                        CrewSection(director = episode.director, writers = episode.writers)
                    }
                }
            }
        }
    }
}

@Composable
private fun CrewSection(director: CastMemberModel?, writers: List<CastMemberModel>) {
    // Owned here rather than passed down, since only this section's rows
    // can ever open it. Reuses ActorDetailsDialog: director/writer credits
    // carry the same TMDB person id, so the same bio lookup applies.
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
            // Repeating "Writer" per row read as noise once an episode
            // credits several — one "Writers:" row with each name still
            // separately clickable reads the same as the single-writer case.
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
private fun MultiNameCrewRow(
        role: String,
        crewMembers: List<CastMemberModel>,
        onCrewMemberClicked: (CastMemberModel) -> Unit,
        modifier: Modifier = Modifier
                            ) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
                text = role,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = TERTIARY_TEXT_ALPHA),
                style = MaterialTheme.typography.labelMedium
            )
        Spacer(modifier = Modifier.width(crewRoleNameSpacing))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(crewMultiNameSpacing)) {
            crewMembers.forEachIndexed { index, crewMember ->
                val nameText = if (index < crewMembers.lastIndex) "${crewMember.name}," else crewMember.name
                Text(
                        text = nameText,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable(
                                onClickLabel = crewMember.name
                                                      ) { onCrewMemberClicked(crewMember) }
                    )
            }
        }
    }
}
