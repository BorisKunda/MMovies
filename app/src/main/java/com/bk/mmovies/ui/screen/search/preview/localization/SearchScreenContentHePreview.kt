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
import com.bk.mmovies.ui.theme.HebrewPhoneSizePreviews
import com.bk.mmovies.ui.theme.MMoviesTheme

// Hebrew content too (not just the UI chrome) so the preview exercises real
// RTL text shaping and bidi mixing with the Latin date subtitle.
private val sampleResultsHebrew = listOf(
        SearchResultModel(
                id = 1,
                mediaType = SearchResultMediaType.MOVIE,
                title = "המטריקס",
                imageUrl = "https://image.tmdb.org/t/p/w342/dXNAPwY7VrqMAo51EKhhCJfaGb5.jpg",
                subtitle = "31 במרץ 1999"
                          ),
        SearchResultModel(
                id = 2,
                mediaType = SearchResultMediaType.TV_SERIES,
                title = "פורצי חוק",
                imageUrl = "https://image.tmdb.org/t/p/w342/xlaY2zyzMfkhk0HSC5VUwzoZPU1.jpg",
                subtitle = "20 בינואר 2008"
                          ),
        SearchResultModel(
                id = 3,
                mediaType = SearchResultMediaType.PERSON,
                title = "קיאנו ריבס",
                imageUrl = "",
                subtitle = ""
                          )
                                         )

private val sampleRecentSearchesHebrew = listOf("המטריקס", "פורצי חוק", "קיאנו ריבס")

@HebrewPhoneSizePreviews
@Composable
private fun SearchResultsContentPreviewHebrew() {
    MMoviesTheme {
        Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                SearchResultsContent(
                        state = SearchResultsUiState.Content(sampleResultsHebrew),
                        selectedFilters = emptySet(),
                        onResultClicked = {},
                        onRetry = {}
                                     )
            }
        }
    }
}

@Preview(
        showBackground = true,
        locale = "iw",
        name = "Recent searches - Hebrew (RTL)"
        )
@Composable
private fun SearchIdleContentPreviewHebrew() {
    MMoviesTheme {
        Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                SearchIdleContent(
                        recentSearches = sampleRecentSearchesHebrew,
                        onRecentSearchClicked = {},
                        onRemoveRecentSearchClicked = {},
                        onClearRecentSearchesClicked = {}
                                  )
            }
        }
    }
}
