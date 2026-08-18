package com.bk.mmovies.ui.screen.details

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.bk.mmovies.R
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.domain.model.MovieDetailsModel
import com.bk.mmovies.ui.component.UserScoreView
import com.bk.mmovies.ui.theme.MMoviesTheme

// --- Spacing scale -----------------------------------------------------------
// A single consistent step keeps the vertical rhythm tight and deliberate
// instead of the uneven 10/28/24 gaps the old layout used.
private val spacingUnit = 12.dp           // base step
private val spacingUnitLarge = 24.dp      // 2x base, for section breaks

private val screenPadding = 16.dp
private val posterWidth = 130.dp
private val posterHeight = 195.dp         // kept at a true 2:3 poster ratio
private val posterCornerShape = 8.dp
private val infoColumnStartPadding = 20.dp
private val metaIconSize = 18.dp
private val metaIconTextSpacing = 8.dp
private val userScoreRingSize =
        32.dp     // UserScoreView scales its numeral and ring with this
private val userScoreRowSpacing = 12.dp

// A long title must not out-grow the poster it sits beside, or the Row's
// CenterVertically ends up centring the poster against a wall of text.
private const val TITLE_MAX_LINES = 3

private val chipSpacing = 8.dp
private val chipCornerShape = 8.dp        // softer radius so chips read as tags, not tappable pills
private val chipHorizontalPadding = 12.dp
private val chipVerticalPadding = 6.dp

// Derived from the scale rather than hand-picked: the header block already
// contributes screenPadding below the chips, so this tops the gap up to
// exactly spacingUnitLarge and stays correct if screenPadding changes.
private val overviewTopSpacing = spacingUnitLarge - screenPadding
private val backdropCornerShape = 12.dp
private val backdropContentPadding = 20.dp

private val overviewTitleLetterSpacing = 1.sp

// Tags are metadata, not actions: a low-contrast fill communicates that far
// better than a bright bordered pill (which invites a tap that goes nowhere).
private const val CHIP_CONTAINER_ALPHA = 0.08f
private const val CHIP_BORDER_ALPHA = 0.14f
private val chipBorderWidth = 1.dp

// The overview text runs the full height of the card, so the scrim has to be
// heaviest at the bottom where the body copy is densest — leaving the top
// lighter lets the backdrop actually show through under the short label.
private const val SCRIM_TOP_ALPHA = 0.55f
private const val SCRIM_BOTTOM_ALPHA = 0.85f

private const val SECONDARY_TEXT_ALPHA = 0.85f
private const val TERTIARY_TEXT_ALPHA = 0.70f

@Composable
fun DetailsScreenContent(
        movieDetails: MovieDetailsModel,
        category: MovieCategory,
        modifier: Modifier = Modifier
                        ) {
    // Upcoming releases routinely lack a score, runtime, backdrop or overview
    // on TMDB, so this category gets a "coming soon" presentation instead of
    // hiding fields the way the sparse-data fallback does for other categories.
    val isUpcoming = category == MovieCategory.UpcomingMovieCategory
    val dateTbaLabel = stringResource(R.string.details_date_tba)
    val runtimeTbaLabel = stringResource(R.string.details_runtime_tba)
    val overviewTbaLabel = stringResource(R.string.details_overview_tba)

    Column(
            modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())   // never let content overflow the screen
          ) {
        Column(modifier = Modifier.padding(screenPadding)) {
            // Center the info block against the poster so the shorter text
            // column no longer leaves a void beside the poster's lower half.
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                                .size(
                                        width = posterWidth,
                                        height = posterHeight
                                     )
                                .clip(RoundedCornerShape(posterCornerShape))
                          )
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
    }
}

@Composable
private fun MetaRow(
        icon: ImageVector,
        text: String,
        modifier: Modifier = Modifier
                   ) {
    // TMDB omits runtime and release date often enough that rendering the row
    // regardless would leave an orphaned icon with no value beside it.
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
    // No weight(1f): the card now wraps its text height instead of stretching
    // to fill the screen, which is what created the large empty backdrop below.
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

private val previewMovieDetails = MovieDetailsModel(
        id = 1,
        title = "Backrooms",
        posterUrl = "https://image.tmdb.org/t/p/w342/dXNAPwY7VrqMAo51EKhhCJfaGb5.jpg",
        backdropUrl = "https://image.tmdb.org/t/p/w780/xlaY2zyzMfkhk0HSC5VUwzoZPU1.jpg",
        releaseDate = "May 28, 2026",
        runtime = "1h 51m",
        userScore = 85,
        genres = listOf(
                "Horror",
                "Mystery",
                "Science Fiction",
                "Thriller"
                       ),
        overview = "A strange doorway appears in the basement of a furniture showroom."
                                                   )

@Preview(showBackground = true)
@Composable
private fun DetailsScreenContentPreview() {
    DetailsScreenContentPreviewFrame(previewMovieDetails)
}

// Hebrew content too (not just the UI chrome) so the preview exercises real
// RTL text shaping and bidi mixing with the Latin runtime/score numerals.
private val previewMovieDetailsHebrew = MovieDetailsModel(
        id = 1,
        title = "החדרים האחוריים",
        posterUrl = "https://image.tmdb.org/t/p/w342/dXNAPwY7VrqMAo51EKhhCJfaGb5.jpg",
        backdropUrl = "https://image.tmdb.org/t/p/w780/xlaY2zyzMfkhk0HSC5VUwzoZPU1.jpg",
        releaseDate = "28 במאי 2026",
        runtime = "1ש 51ד",
        userScore = 85,
        genres = listOf(
                "אימה",
                "מסתורין",
                "מדע בדיוני",
                "מותחן"
                       ),
        overview = "דלת מוזרה מופיעה במרתף של חנות רהיטים."
                                                          )

@Preview(showBackground = true, locale = "iw", name = "Hebrew (RTL)")
@Composable
private fun DetailsScreenContentPreviewHebrew() {
    DetailsScreenContentPreviewFrame(previewMovieDetailsHebrew)
}

// Russian content too, since the point of this one is length rather than
// direction: the genre chips and the two-line score caption
// ("ОЦЕНКА\nЗРИТЕЛЕЙ") are the tightest fits on this screen.
private val previewMovieDetailsRussian = MovieDetailsModel(
        id = 1,
        title = "Закулисные комнаты",
        posterUrl = "https://image.tmdb.org/t/p/w342/dXNAPwY7VrqMAo51EKhhCJfaGb5.jpg",
        backdropUrl = "https://image.tmdb.org/t/p/w780/xlaY2zyzMfkhk0HSC5VUwzoZPU1.jpg",
        releaseDate = "28 мая 2026",
        runtime = "1ч 51м",
        userScore = 85,
        genres = listOf(
                "Ужасы",
                "Мистика",
                "Научная фантастика",
                "Триллер"
                       ),
        overview = "Странная дверь появляется в подвале мебельного салона."
                                                          )

@Preview(showBackground = true, locale = "ru", name = "Russian (long text)")
@Composable
private fun DetailsScreenContentPreviewRussian() {
    DetailsScreenContentPreviewFrame(previewMovieDetailsRussian)
}

/**
 * Everything the mapper can hand us as empty: no release date, no runtime, no
 * genres and no overview, plus a title long enough to need truncating.
 */
@Preview(showBackground = true, name = "Sparse data + long title")
@Composable
private fun DetailsScreenContentSparsePreview() {
    DetailsScreenContentPreviewFrame(
            previewMovieDetails.copy(
                    title = "The Lord of the Rings: The Fellowship of the Ring - Extended Edition",
                    releaseDate = "",
                    runtime = "",
                    genres = emptyList(),
                    overview = ""
                                    )
                                    )
}

@Preview(showBackground = true, name = "Large font scale", fontScale = 2f)
@Composable
private fun DetailsScreenContentLargeFontPreview() {
    DetailsScreenContentPreviewFrame(previewMovieDetails)
}

/**
 * TMDB's real shape for an unreleased title: no score, no runtime, no
 * backdrop and no overview yet.
 */
@Preview(showBackground = true, name = "Upcoming - no data yet")
@Composable
private fun DetailsScreenContentUpcomingPreview() {
    DetailsScreenContentPreviewFrame(
            movieDetails = previewMovieDetails.copy(
                    releaseDate = "",
                    runtime = "",
                    userScore = 0,
                    backdropUrl = "",
                    overview = ""
                                                    ),
            category = MovieCategory.UpcomingMovieCategory
                                    )
}

@Composable
private fun DetailsScreenContentPreviewFrame(
        movieDetails: MovieDetailsModel,
        category: MovieCategory = MovieCategory.PopularMovieCategory
                                             ) {
    MMoviesTheme {
        Box(
                Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
           ) {
            DetailsScreenContent(movieDetails = movieDetails, category = category)
        }
    }
}
