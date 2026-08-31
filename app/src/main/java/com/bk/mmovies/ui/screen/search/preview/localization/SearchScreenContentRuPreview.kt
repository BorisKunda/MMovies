package com.bk.mmovies.ui.screen.search.preview.localization

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bk.mmovies.domain.model.SearchResultMediaType
import com.bk.mmovies.domain.model.SearchResultModel
import com.bk.mmovies.ui.screen.search.SearchResultsUiState
import com.bk.mmovies.ui.screen.search.screencomponents.SearchIdleContent
import com.bk.mmovies.ui.screen.search.screencomponents.SearchResultsContent
import com.bk.mmovies.ui.theme.MMoviesTheme
import com.bk.mmovies.ui.theme.RussianPhoneSizePreviews

// Russian content too, since the point of this one is length rather than
// direction: result titles and the media-type badge are the tightest fits
// on this screen.
private val sampleResultsRussian = listOf(
        SearchResultModel(
                id = 1,
                mediaType = SearchResultMediaType.MOVIE,
                title = "Матрица",
                imageUrl = "https://image.tmdb.org/t/p/w342/dXNAPwY7VrqMAo51EKhhCJfaGb5.jpg",
                subtitle = "31 марта 1999"
                          ),
        SearchResultModel(
                id = 2,
                mediaType = SearchResultMediaType.TV_SERIES,
                title = "Во все тяжкие",
                imageUrl = "https://image.tmdb.org/t/p/w342/xlaY2zyzMfkhk0HSC5VUwzoZPU1.jpg",
                subtitle = "20 января 2008"
                          ),
        SearchResultModel(
                id = 3,
                mediaType = SearchResultMediaType.PERSON,
                title = "Киану Ривз",
                imageUrl = "",
                subtitle = ""
                          )
                                          )

private val sampleRecentSearchesRussian = listOf("Матрица", "Во все тяжкие", "Киану Ривз")

@RussianPhoneSizePreviews
@Composable
private fun SearchResultsContentPreviewRussian() {
    MMoviesTheme {
        Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                SearchResultsContent(
                        state = SearchResultsUiState.Content(sampleResultsRussian),
                        selectedFilters = emptySet(),
                        onResultClicked = {},
                        onRetry = {}
                                     )
            }
        }
    }
}

@Preview(showBackground = true, locale = "ru", name = "Recent searches - Russian (long text)")
@Composable
private fun SearchIdleContentPreviewRussian() {
    MMoviesTheme {
        Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                SearchIdleContent(
                        recentSearches = sampleRecentSearchesRussian,
                        onRecentSearchClicked = {},
                        onRemoveRecentSearchClicked = {},
                        onClearRecentSearchesClicked = {}
                                  )
            }
        }
    }
}
