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
import com.bk.mmovies.ui.theme.HebrewPhoneSizePreviews
import com.bk.mmovies.ui.theme.MMoviesTheme

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
                   ),
        // TV row exercising the Ongoing air-date label — mixes a Latin year
        // with Hebrew free text, a real bidi-mixing case in RTL layout.
        CatalogItem(
                id = 3,
                title = "פורצי חוק",
                imageUrl = "https://image.tmdb.org/t/p/w342/ztkUQFLlC19CCMYHW9o1zWhJRNq.jpg",
                releaseDate = "20 בינואר 2008",
                mediaType = CatalogMediaType.TV_SERIES,
                rating = 88
                   )
                                             )

@HebrewPhoneSizePreviews
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
                        onFavoriteClicked = {},
                        getTvSeriesAirDateLabel = { SeriesAirDateLabel.Ongoing(text = "2023 - בשידור") }
                               )
            }
        }
    }
}