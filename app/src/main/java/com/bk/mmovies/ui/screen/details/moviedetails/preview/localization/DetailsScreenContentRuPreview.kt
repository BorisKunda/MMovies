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
import com.bk.mmovies.ui.theme.MMoviesTheme
import com.bk.mmovies.ui.theme.RussianPhoneSizePreviews

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

@RussianPhoneSizePreviews
@Composable
private fun DetailsScreenContentPreviewRussian() {
    MMoviesTheme {
        Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
            MovieDetailsScreenContent(
                    movieDetails = previewMovieDetailsRussian,
                    category = MovieCategory.PopularMovieCategory,
                    modifier = Modifier.padding(innerPadding),
                    showFavoriteStar = true,
                    onFavoriteClicked = {}
                                     )
        }
    }
}