package com.bk.mmovies.ui.screen.search.preview

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
import com.bk.mmovies.ui.theme.PhoneSizePreviews

private val sampleResults = listOf(
        SearchResultModel(
                id = 1,
                mediaType = SearchResultMediaType.MOVIE,
                title = "The Matrix",
                imageUrl = "https://image.tmdb.org/t/p/w342/dXNAPwY7VrqMAo51EKhhCJfaGb5.jpg",
                subtitle = "March 31, 1999"
                          ),
        SearchResultModel(
                id = 2,
                mediaType = SearchResultMediaType.TV_SERIES,
                title = "Breaking Bad",
                imageUrl = "https://image.tmdb.org/t/p/w342/xlaY2zyzMfkhk0HSC5VUwzoZPU1.jpg",
                subtitle = "January 20, 2008"
                          ),
        SearchResultModel(
                id = 3,
                mediaType = SearchResultMediaType.PERSON,
                title = "Keanu Reeves",
                imageUrl = "",
                subtitle = ""
                          )
                                   )

private val sampleRecentSearches = listOf("Matrix", "Breaking Bad", "Keanu Reeves")

@PhoneSizePreviews
@Composable
private fun SearchResultsContentPreview() {
    MMoviesTheme {
        Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                SearchResultsContent(
                        state = SearchResultsUiState.Content(sampleResults),
                        selectedFilters = emptySet(),
                        onResultClicked = {},
                        onRetry = {}
                                     )
            }
        }
    }
}

@Preview(showBackground = true, name = "Recent searches")
@Composable
private fun SearchIdleContentPreview() {
    MMoviesTheme {
        Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                SearchIdleContent(
                        recentSearches = sampleRecentSearches,
                        onRecentSearchClicked = {},
                        onRemoveRecentSearchClicked = {},
                        onClearRecentSearchesClicked = {}
                                  )
            }
        }
    }
}

@Preview(showBackground = true, name = "Idle - no recent searches")
@Composable
private fun SearchIdleContentEmptyPreview() {
    MMoviesTheme {
        Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                SearchIdleContent(
                        recentSearches = emptyList(),
                        onRecentSearchClicked = {},
                        onRemoveRecentSearchClicked = {},
                        onClearRecentSearchesClicked = {}
                                  )
            }
        }
    }
}
