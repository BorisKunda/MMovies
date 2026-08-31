package com.bk.mmovies.ui.screen.catalog.preview.localization

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bk.mmovies.domain.model.CatalogItem
import com.bk.mmovies.domain.model.CatalogMediaType
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.domain.model.SeriesAirDateLabel
import com.bk.mmovies.ui.screen.catalog.screencomponents.CatalogListView
import com.bk.mmovies.ui.theme.MMoviesTheme
import com.bk.mmovies.ui.theme.RussianPhoneSizePreviews

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
                   ),
        // TV row exercising the Ongoing air-date label — mixes a Latin year
        // with Russian free text, the tightest fit for this caption.
        CatalogItem(
                id = 3,
                title = "Во все тяжкие",
                imageUrl = "https://image.tmdb.org/t/p/w342/ztkUQFLlC19CCMYHW9o1zWhJRNq.jpg",
                releaseDate = "20 января 2008",
                mediaType = CatalogMediaType.TV_SERIES,
                rating = 88
                   )
                                              )


@RussianPhoneSizePreviews
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
                        onFavoriteClicked = {},
                        getTvSeriesAirDateLabel = { SeriesAirDateLabel.Ongoing(text = "2023 - Идёт показ") }
                               )
            }
        }
    }
}