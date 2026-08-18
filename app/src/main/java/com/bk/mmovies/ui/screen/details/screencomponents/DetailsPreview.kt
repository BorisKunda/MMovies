package com.bk.mmovies.ui.screen.details.screencomponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bk.mmovies.domain.model.CastMemberModel
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.domain.model.MovieDetailsModel
import com.bk.mmovies.ui.theme.MMoviesTheme

private val previewCast = listOf(
        CastMemberModel(
                id = 1,
                name = "Wyatt Russell",
                character = "Justin",
                profileUrl = "https://image.tmdb.org/t/p/w185/8qXWJx6mxvXynMhmXPWXTLDdrUB.jpg"
                        ),
        CastMemberModel(
                id = 2,
                name = "Melissa Barrera",
                character = "Mia",
                profileUrl = "https://image.tmdb.org/t/p/w185/oJ022qBH7d1hbrTiHwSyZbSC08l.jpg"
                        ),
        CastMemberModel(
                id = 3,
                name = "Charlie Hunnam",
                character = "Marcus",
                profileUrl = ""
                        ),
        CastMemberModel(
                id = 4,
                name = "A Very Long Cast Member Name",
                character = "A very long character description that should wrap",
                profileUrl = ""
                        )
                                 )

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
        overview = "A strange doorway appears in the basement of a furniture showroom.",
        cast = previewCast
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
 * backdrop, no overview and no cast yet.
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
                    overview = "",
                    cast = emptyList()
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
