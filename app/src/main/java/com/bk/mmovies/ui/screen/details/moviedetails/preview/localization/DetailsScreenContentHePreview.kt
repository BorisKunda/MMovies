package com.bk.mmovies.ui.screen.details.moviedetails.preview.localization

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.domain.model.MovieDetailsModel
import com.bk.mmovies.ui.screen.details.moviedetails.screencomponents.MovieDetailsScreenContent
import com.bk.mmovies.ui.theme.HebrewPhoneSizePreviews
import com.bk.mmovies.ui.theme.MMoviesTheme

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

@HebrewPhoneSizePreviews
@Composable
private fun DetailsScreenContentPreviewHebrew() {
    MMoviesTheme {
        Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
            MovieDetailsScreenContent(
                    movieDetails = previewMovieDetailsHebrew,
                    category = MovieCategory.PopularMovieCategory,
                    modifier = Modifier.padding(innerPadding),
                    showFavoriteStar = true,
                    onFavoriteClicked = {}
                                     )
        }
    }
}