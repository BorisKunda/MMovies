package com.bk.mmovies.tv.ui.news.preview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import coil3.annotation.ExperimentalCoilApi
import coil3.asImage
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import com.bk.mmovies.tv.R
import com.bk.mmovies.tv.ui.news.NewsArticleBodyUiState
import com.bk.mmovies.tv.ui.news.NewsDetailsTvContent
import com.bk.mmovies.ui.theme.MMoviesTheme

internal const val PREVIEW_BODY =
        "The festival opened on Thursday with a retrospective of the director's early work, " +
        "drawing crowds that stretched around the block. Critics praised the restored prints, " +
        "while organisers announced an expanded programme for the coming weekend."


@Preview(
        device = "id:tv_1080p",
        name = "News details - loading"
)
@Composable
private fun NewsDetailsTvScreenLoadingPreview() {
    NewsDetailsPreviewScaffold(bodyState = NewsArticleBodyUiState.Loading)
}

@Preview(
        device = "id:tv_1080p",
        name = "News details - unavailable"
)
@Composable
private fun NewsDetailsTvScreenUnavailablePreview() {
    NewsDetailsPreviewScaffold(bodyState = NewsArticleBodyUiState.Unavailable)
}

@OptIn(ExperimentalCoilApi::class)
@Composable
internal fun NewsDetailsPreviewScaffold(bodyState: NewsArticleBodyUiState) {
    val posterImage = ContextCompat.getDrawable(
            LocalContext.current,
            R.drawable.preview_news_poster
    )!!
            .asImage()
    CompositionLocalProvider(LocalAsyncImagePreviewHandler provides AsyncImagePreviewHandler { posterImage }) {
        MMoviesTheme {
            Scaffold(
                    containerColor = MaterialTheme.colorScheme.background
            ) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    NewsDetailsTvContent(
                            title = "Film festival opens with restored classics",
                            description = "A short summary of the article shown while the full text loads.",
                            imageUrl = "https://example.com/poster.jpg",
                            sourceName = "The Guardian",
                            author = "Jane Doe",
                            publishedAt = "2026-10-14",
                            bodyState = bodyState
                    )
                }
            }
        }
    }
}
