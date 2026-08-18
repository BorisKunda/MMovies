package com.bk.mmovies.ui.screen.movies.screencomponents

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.domain.model.MovieModel
import com.bk.mmovies.ui.screen.movies.MoviesScreenState
import com.bk.mmovies.ui.screen.movies.UserProfileUiState
import com.bk.mmovies.ui.theme.MMoviesTheme

private val previewMovies = listOf(
        MovieModel(
                id = 1,
                title = "The Matrix",
                desc = "A computer hacker learns about the true nature of reality.",
                imageUrl = "https://image.tmdb.org/t/p/w342/dXNAPwY7VrqMAo51EKhhCJfaGb5.jpg",
                releaseDate = "March 31, 1999",
                rating = 83
                  ),
        MovieModel(
                id = 2,
                title = "Inception",
                desc = "A thief who steals corporate secrets through dream-sharing technology.",
                imageUrl = "https://image.tmdb.org/t/p/w342/xlaY2zyzMfkhk0HSC5VUwzoZPU1.jpg",
                releaseDate = "July 16, 2010",
                rating = 91
                  )
                                   )

// Hebrew content too (not just the UI chrome) so the preview exercises real
// RTL text shaping and bidi mixing with the Latin release dates/ratings.
private val previewMoviesHebrew = listOf(
        MovieModel(
                id = 1,
                title = "המטריקס",
                desc = "פורץ מחשבים מגלה את טבעה האמיתי של המציאות.",
                imageUrl = "https://image.tmdb.org/t/p/w342/dXNAPwY7VrqMAo51EKhhCJfaGb5.jpg",
                releaseDate = "31 במרץ 1999",
                rating = 83
                  ),
        MovieModel(
                id = 2,
                title = "התחלה",
                desc = "גנב הגונב סודות תאגידיים באמצעות טכנולוגיית שיתוף חלומות.",
                imageUrl = "https://image.tmdb.org/t/p/w342/xlaY2zyzMfkhk0HSC5VUwzoZPU1.jpg",
                releaseDate = "16 ביולי 2010",
                rating = 91
                  )
                                         )

@Preview(showBackground = true, locale = "iw", name = "Hebrew (RTL)")
@Composable
private fun MoviesScreenContentPreviewHebrew() {
    MoviesScreenPreviewFrame(MoviesScreenState.Content(previewMoviesHebrew))
}

@Preview(showBackground = true, locale = "iw", name = "Hebrew (RTL) - Empty favorites")
@Composable
private fun MoviesScreenEmptyFavoritesPreviewHebrew() {
    MoviesScreenPreviewFrame(
            state = MoviesScreenState.Empty,
            selectedCategory = MovieCategory.FavoritesMovieCategory
                            )
}

// Russian content too, since the point of this one is length rather than
// direction: titles, descriptions and the Cyrillic date format all run
// longer than their English equivalents.
private val previewMoviesRussian = listOf(
        MovieModel(
                id = 1,
                title = "Матрица",
                desc = "Хакер узнаёт истинную природу окружающей его реальности.",
                imageUrl = "https://image.tmdb.org/t/p/w342/dXNAPwY7VrqMAo51EKhhCJfaGb5.jpg",
                releaseDate = "31 марта 1999",
                rating = 83
                  ),
        MovieModel(
                id = 2,
                title = "Начало",
                desc = "Вор, крадущий корпоративные секреты с помощью технологии совместных сновидений.",
                imageUrl = "https://image.tmdb.org/t/p/w342/xlaY2zyzMfkhk0HSC5VUwzoZPU1.jpg",
                releaseDate = "16 июля 2010",
                rating = 91
                  )
                                         )

@Preview(showBackground = true, locale = "ru", name = "Russian (long text)")
@Composable
private fun MoviesScreenContentPreviewRussian() {
    MoviesScreenPreviewFrame(MoviesScreenState.Content(previewMoviesRussian))
}

@Preview(showBackground = true, locale = "ru", name = "Russian (long text) - Empty favorites")
@Composable
private fun MoviesScreenEmptyFavoritesPreviewRussian() {
    MoviesScreenPreviewFrame(
            state = MoviesScreenState.Empty,
            selectedCategory = MovieCategory.FavoritesMovieCategory
                            )
}

@Preview(showBackground = true)
@Composable
private fun MoviesScreenContentPreview() {
    MoviesScreenPreviewFrame(MoviesScreenState.Content(previewMovies))
}

@Preview(showBackground = true)
@Composable
private fun MoviesScreenLoadingPreview() {
    MoviesScreenPreviewFrame(MoviesScreenState.Loading)
}

@Preview(showBackground = true)
@Composable
private fun MoviesScreenErrorPreview() {
    MoviesScreenPreviewFrame(
            MoviesScreenState.Error("Couldn't load movies right now. Please try again later.")
                            )
}

@Preview(showBackground = true, name = "Empty - favorites")
@Composable
private fun MoviesScreenEmptyFavoritesPreview() {
    MoviesScreenPreviewFrame(
            state = MoviesScreenState.Empty,
            selectedCategory = MovieCategory.FavoritesMovieCategory
                            )
}

@Preview(showBackground = true, name = "Empty - category")
@Composable
private fun MoviesScreenEmptyPreview() {
    MoviesScreenPreviewFrame(MoviesScreenState.Empty)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoviesScreenPreviewFrame(
        state: MoviesScreenState,
        selectedCategory: MovieCategory = MovieCategory.PopularMovieCategory
                                    ) {
    MMoviesTheme {
        MoviesScreenContent(
                state = state,
                selectedCategory = selectedCategory,
                userProfileState = UserProfileUiState(
                        name = "Boris Kunda",
                        imageUrl = "",
                        isGuest = false
                                                      ),
                onMovieClicked = {},
                onCategorySelected = {},
                onRetry = {},
                onLogout = {}
                            )
    }
}

@Preview
@Composable
private fun MovieRowLoadingPlaceholderListPreview() {
    MMoviesTheme {
        MovieRowLoadingPlaceholderList()
    }
}

@Preview
@Composable
private fun MovieRowLoadingPlaceholderPreview() {
    MMoviesTheme {
        MovieRowLoadingPlaceholder()
    }
}

@Preview
@Composable
private fun MoviesListViewPreview() {
    val sampleMovies = listOf(
            MovieModel(
                    id = 1,
                    title = "The Matrix",
                    desc = "A computer hacker learns about the true nature of reality.",
                    imageUrl = "https://image.tmdb.org/t/p/w342/dXNAPwY7VrqMAo51EKhhCJfaGb5.jpg",
                    releaseDate = "March 31, 1999",
                    rating = 83
                      ),
            MovieModel(
                    id = 2,
                    title = "Inception",
                    desc = "A thief who steals corporate secrets through dream-sharing technology.",
                    imageUrl = "https://image.tmdb.org/t/p/w342/xlaY2zyzMfkhk0HSC5VUwzoZPU1.jpg",
                    releaseDate = "July 16, 2010",
                    rating = 91
                      ),
            // No date and no description: both blocks must simply not render.
            MovieModel(
                    id = 3,
                    title = "An Untitled Film With A Very Long Name That Has To Truncate",
                    desc = "",
                    imageUrl = "",
                    releaseDate = "",
                    rating = 0
                      )
                             )
    // Wrapping in the theme: without it this previewed in light Material
    // colours for an app that only ships a dark scheme.
    MMoviesTheme {
        MoviesListView(
                movies = sampleMovies,
                selectedCategory = MovieCategory.PopularMovieCategory,
                onMovieClicked = {}
                      )
    }
}

@Preview(showBackground = true)
@Composable
private fun UserSelectorPreview() {
    MMoviesTheme {
        Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
            Box(
                    modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    contentAlignment = Alignment.Center
               ) {
                UserSelector(
                        name = "Boris Kunda",
                        imageUrl = "",
                        isGuest = false,
                        onLogout = {}
                            )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UserSelectorGuestPreview() {
    MMoviesTheme {
        Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
            Box(
                    modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    contentAlignment = Alignment.Center
               ) {
                UserSelector(
                        name = "Guest",
                        imageUrl = "",
                        isGuest = true,
                        onLogout = {}
                            )
            }
        }
    }
}
