package com.bk.mmovies.ui.screen.catalog.preview


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bk.mmovies.domain.model.CatalogItem
import com.bk.mmovies.domain.model.CatalogMediaType
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.ui.screen.catalog.screencomponents.CatalogListView
import com.bk.mmovies.ui.screen.catalog.screencomponents.MovieRowLoadingPlaceholder
import com.bk.mmovies.ui.screen.catalog.screencomponents.MovieRowLoadingPlaceholderList
import com.bk.mmovies.ui.theme.MMoviesTheme

private val sampleCatalogItems = listOf(
        // isFavorite = true so the preview shows both the filled and
        // outline star states side by side.
        CatalogItem(
                id = 1,
                title = "The Matrix",
                imageUrl = "https://image.tmdb.org/t/p/w342/dXNAPwY7VrqMAo51EKhhCJfaGb5.jpg",
                releaseDate = "March 31, 1999",
                mediaType = CatalogMediaType.MOVIE,
                rating = 83,
                isFavorite = true
                   ),
        CatalogItem(
                id = 2,
                title = "Inception",
                imageUrl = "https://image.tmdb.org/t/p/w342/xlaY2zyzMfkhk0HSC5VUwzoZPU1.jpg",
                releaseDate = "July 16, 2010",
                mediaType = CatalogMediaType.MOVIE,
                rating = 91
                   ),
        // No date and no image: both blocks must simply not render/fall back.
        CatalogItem(
                id = 3,
                title = "An Untitled Show With A Very Long Name That Has To Truncate",
                imageUrl = "",
                releaseDate = "",
                mediaType = CatalogMediaType.MOVIE,
                rating = 0
                   )
                                       )

private val sampleCatalogItemsHebrew = listOf(
        CatalogItem(
                id = 1,
                title = "המטריקס",
                imageUrl = "https://image.tmdb.org/t/p/w342/dXNAPwY7VrqMAo51EKhhCJfaGb5.jpg",
                releaseDate = "31 במרץ 1999",
                mediaType = CatalogMediaType.MOVIE,
                rating = 83,
                isFavorite = true
                   ),
        CatalogItem(
                id = 2,
                title = "התחלה",
                imageUrl = "https://image.tmdb.org/t/p/w342/xlaY2zyzMfkhk0HSC5VUwzoZPU1.jpg",
                releaseDate = "16 ביולי 2010",
                mediaType = CatalogMediaType.MOVIE,
                rating = 91
                   )
                                             )

private val sampleCatalogItemsRussian = listOf(
        CatalogItem(
                id = 1,
                title = "Матрица",
                imageUrl = "https://image.tmdb.org/t/p/w342/dXNAPwY7VrqMAo51EKhhCJfaGb5.jpg",
                releaseDate = "31 марта 1999",
                mediaType = CatalogMediaType.MOVIE,
                rating = 83,
                isFavorite = true
                   ),
        CatalogItem(
                id = 2,
                title = "Начало",
                imageUrl = "https://image.tmdb.org/t/p/w342/xlaY2zyzMfkhk0HSC5VUwzoZPU1.jpg",
                releaseDate = "16 июля 2010",
                mediaType = CatalogMediaType.MOVIE,
                rating = 91
                   )
                                              )

@Preview(
        showBackground = true,
        name = "Content"
        )
@Composable
private fun CatalogListViewPreview() {
    MMoviesTheme {
        Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                CatalogListView(
                        catalogItems = sampleCatalogItems,
                        selectedCategory = MovieCategory.PopularMovieCategory,
                        onCatalogItemClicked = {},
                        isGuest = false,
                        onFavoriteClicked = {}
                               )
            }
        }
    }
}

@Preview(
        showBackground = true,
        name = "Guest - no favorite star"
        )
@Composable
private fun CatalogListViewGuestPreview() {
    MMoviesTheme {
        Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                CatalogListView(
                        catalogItems = sampleCatalogItems,
                        selectedCategory = MovieCategory.PopularMovieCategory,
                        onCatalogItemClicked = {},
                        isGuest = true,
                        onFavoriteClicked = {}
                               )
            }
        }
    }
}

@Preview(
        showBackground = true,
        name = "Upcoming - neutral fallback, no score"
        )
@Composable
private fun CatalogListViewUpcomingPreview() {
    MMoviesTheme {
        Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                CatalogListView(
                        catalogItems = sampleCatalogItems,
                        selectedCategory = MovieCategory.UpcomingMovieCategory,
                        onCatalogItemClicked = {},
                        isGuest = false,
                        onFavoriteClicked = {}
                               )
            }
        }
    }
}

@Preview(
        showBackground = true,
        locale = "iw",
        name = "Hebrew (RTL)"
        )
@Composable
private fun CatalogListViewHebrewPreview() {
    MMoviesTheme {
        Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                CatalogListView(
                        catalogItems = sampleCatalogItemsHebrew,
                        selectedCategory = MovieCategory.PopularMovieCategory,
                        onCatalogItemClicked = {},
                        isGuest = false,
                        onFavoriteClicked = {}
                               )
            }
        }
    }
}

@Preview(
        showBackground = true,
        locale = "ru",
        name = "Russian (long text)"
        )
@Composable
private fun CatalogListViewRussianPreview() {
    MMoviesTheme {
        Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                CatalogListView(
                        catalogItems = sampleCatalogItemsRussian,
                        selectedCategory = MovieCategory.PopularMovieCategory,
                        onCatalogItemClicked = {},
                        isGuest = false,
                        onFavoriteClicked = {}
                               )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MovieRowLoadingPlaceholderListPreview() {
    MMoviesTheme {
        Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
            innerPadding
            MovieRowLoadingPlaceholderList()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MovieRowLoadingPlaceholderPreview() {
    MMoviesTheme {
        Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
            innerPadding
            MovieRowLoadingPlaceholder()
        }
    }
}
