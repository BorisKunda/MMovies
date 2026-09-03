package com.bk.mmovies.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.bk.mmovies.R
import com.bk.mmovies.domain.model.CastMemberModel

// Shared by MovieDetailsScreenContent, TvSeriesDetailsScreenContent, and
// EpisodeDetailsDialog, which used to each carry their own copy of these.

private val metaIconSize = 18.dp
private val metaIconTextSpacing = 8.dp

private val chipSpacing = 8.dp
private val chipCornerShape = 8.dp
private val chipHorizontalPadding = 12.dp
private val chipVerticalPadding = 6.dp
private const val CHIP_CONTAINER_ALPHA = 0.08f
private const val CHIP_BORDER_ALPHA = 0.14f
private val chipBorderWidth = 1.dp

private val screenPadding = 16.dp
private val spacingUnit = 12.dp
private val overviewTopSpacing = 8.dp
private val backdropCornerShape = 12.dp
private val backdropContentPadding = 20.dp
private val overviewTitleLetterSpacing = 1.sp
private const val SCRIM_TOP_ALPHA = 0.55f
private const val SCRIM_BOTTOM_ALPHA = 0.85f

private val castItemWidth = 84.dp
private val castImageSize = 72.dp
private val castImageNameSpacing = 8.dp
private val castNameCharacterSpacing = 2.dp
private const val CAST_ITEM_TEXT_MAX_LINES = 2

private val crewRoleNameSpacing = 6.dp
private val crewMultiNameSpacing = 4.dp

private const val SECONDARY_TEXT_ALPHA = 0.85f
private const val TERTIARY_TEXT_ALPHA = 0.70f

// TMDB omits runtime and release date often enough that rendering the row
// regardless would leave an orphaned icon with no value beside it.
@Composable
fun MetaRow(
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
fun GenreChip(genre: String) {
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

// No weight(1f): the card wraps its text height instead of stretching to
// fill the screen, which is what created a large empty backdrop below it.
@Composable
fun OverviewSection(
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
                model = backdropUrl,
                placeholder = painterResource(R.drawable.placeholder),
                error = painterResource(R.drawable.placeholder),
                // Decorative: it sits behind the overview text and carries no
                // information the poster and title haven't already announced.
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
            // White rather than onSurface: this text sits on an arbitrary
            // photograph behind a black scrim, not on the app surface.
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

@Composable
fun CastMemberItem(castMember: CastMemberModel, onClick: () -> Unit) {
    Column(
            modifier = Modifier
                    .width(castItemWidth)
                    .clickable(onClickLabel = castMember.name, onClick = onClick),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
        AsyncImage(
                model = castMember.profileUrl,
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
fun CrewMemberRow(
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
fun MultiNameCrewRow(
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
