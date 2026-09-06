package com.bk.mmovies.ui.screen.news.preview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bk.mmovies.R
import com.bk.mmovies.domain.model.NewsItem
import com.bk.mmovies.ui.screen.news.ArticleWebViewScreen
import com.bk.mmovies.ui.screen.news.NewsScreenBody
import com.bk.mmovies.ui.screen.news.NewsScreenState
import com.bk.mmovies.ui.screen.news.screencomponents.NewsCard
import com.bk.mmovies.ui.screen.news.screencomponents.NewsListView
import com.bk.mmovies.ui.theme.MMoviesTheme
import com.bk.mmovies.ui.theme.PhoneSizePreviews

// English sample data ---------------------------------------------------

internal val sampleNewsItems = listOf(
        NewsItem(
                title = "Gal Gadot's New 84-Minute Action Movie Sets a New Rotten Tomatoes Record",
                description = "The action film continues an unfortunate three-year-old Rotten Tomatoes trend.",
                articleUrl = "https://example.com/gal-gadot-action-movie",
                imageUrl = "https://image.tmdb.org/t/p/w780/xlaY2zyzMfkhk0HSC5VUwzoZPU1.jpg",
                sourceName = "CBR - Comic Book Resources",
                author = "Jane Doe",
                publishedAt = "September 5, 2026"
                ),
        // No image: the card must skip the image slot entirely, not show a
        // broken placeholder.
        NewsItem(
                title = "What To Watch This Weekend: New Shows And Movies To Stream",
                description = "",
                articleUrl = "https://example.com/what-to-watch",
                imageUrl = "",
                sourceName = "Forbes",
                author = "",
                publishedAt = "September 4, 2026"
                ),
        // Long title + no source: exercises the 3-line ellipsis and the
        // "Unknown source" fallback.
        NewsItem(
                title = "An Extremely Long Headline About A New Streaming Series That Should Wrap And Then Truncate After Three Lines",
                description = "A short excerpt of the article body, since NewsAPI never returns the full text.",
                articleUrl = "https://example.com/long-headline",
                imageUrl = "https://image.tmdb.org/t/p/w780/dXNAPwY7VrqMAo51EKhhCJfaGb5.jpg",
                sourceName = "",
                author = "John Smith",
                publishedAt = ""
                )
                                     )

/**
 * Reproduces [com.bk.mmovies.ui.screen.news.NewsScreen]'s Scaffold/top bar/
 * body without a Hilt-backed [com.bk.mmovies.ui.screen.news.NewsViewModel],
 * so every [NewsScreenState] can be previewed directly with canned data.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NewsScreenPreviewFrame(screenState: NewsScreenState) {
    MMoviesTheme {
        Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                topBar = {
                    TopAppBar(
                            title = {
                                Text(
                                        text = stringResource(R.string.news_screen_title),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.background
                                                                      )
                             )
                }
                ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                NewsScreenBody(screenState = screenState, onRetry = {}, onReadFullArticle = {})
            }
        }
    }
}

// --- NewsScreen states (English) -------------------------------------

@Preview
@PhoneSizePreviews
@Composable
private fun NewsScreenContentPreview() {
    NewsScreenPreviewFrame(NewsScreenState.Content(sampleNewsItems))
}

@Preview(showBackground = true, name = "Loading")
@Composable
private fun NewsScreenLoadingPreview() {
    NewsScreenPreviewFrame(NewsScreenState.Loading)
}

@Preview(showBackground = true, name = "Empty")
@Composable
private fun NewsScreenEmptyPreview() {
    NewsScreenPreviewFrame(NewsScreenState.Empty)
}

@Preview(showBackground = true, name = "Error")
@Composable
private fun NewsScreenErrorPreview() {
    NewsScreenPreviewFrame(
            NewsScreenState.Error("Couldn't load news right now. Please try again later.")
                          )
}

// --- NewsListView / NewsCard (English) --------------------------------

@Preview(showBackground = true, name = "News list")
@Composable
private fun NewsListViewPreview() {
    MMoviesTheme {
        Scaffold(modifier = Modifier.fillMaxSize(), containerColor = MaterialTheme.colorScheme.background) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                NewsListView(newsItems = sampleNewsItems, onReadFullArticle = {})
            }
        }
    }
}

@Preview(showBackground = true, name = "Card - full data")
@Composable
private fun NewsCardPreview() {
    MMoviesTheme {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
            NewsCard(
                    newsItem = sampleNewsItems[0],
                    onReadFullArticle = {},
                    modifier = Modifier.padding(padding).padding(12.dp)
                    )
        }
    }
}

@Preview(showBackground = true, name = "Card - missing image and description")
@Composable
private fun NewsCardMissingImagePreview() {
    MMoviesTheme {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
            NewsCard(
                    newsItem = sampleNewsItems[1],
                    onReadFullArticle = {},
                    modifier = Modifier.padding(padding).padding(12.dp)
                    )
        }
    }
}

@Preview(showBackground = true, name = "Card - long title, no source/date")
@Composable
private fun NewsCardLongTitlePreview() {
    MMoviesTheme {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
            NewsCard(
                    newsItem = sampleNewsItems[2],
                    onReadFullArticle = {},
                    modifier = Modifier.padding(padding).padding(12.dp)
                    )
        }
    }
}

// --- ArticleWebViewScreen (English) ------------------------------------

// Only the invalid-url path is previewable statically: WebView isn't
// rendered by Layoutlib's preview renderer, but the blank/invalid guard
// short-circuits before a WebView is ever created.
@Preview(showBackground = true, name = "Article WebView - invalid url")
@Composable
private fun ArticleWebViewInvalidUrlPreview() {
    MMoviesTheme {
        ArticleWebViewScreen(articleUrl = "", onClose = {})
    }
}
