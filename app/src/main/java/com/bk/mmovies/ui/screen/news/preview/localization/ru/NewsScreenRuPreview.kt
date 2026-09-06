package com.bk.mmovies.ui.screen.news.preview

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bk.mmovies.domain.model.NewsItem
import com.bk.mmovies.ui.screen.news.NewsScreenState
import com.bk.mmovies.ui.screen.news.screencomponents.NewsCard
import com.bk.mmovies.ui.theme.MMoviesTheme
import com.bk.mmovies.ui.theme.RussianPhoneSizePreviews

// Russian sample data -----------------------------------------------------

private val sampleNewsItemsRussian = listOf(
        NewsItem(
                title = "Галь Гадот снялась в новом 84-минутном боевике",
                description = "Фильм продолжает неудачную трёхлетнюю тенденцию на Rotten Tomatoes.",
                articleUrl = "https://example.com/ru-1",
                imageUrl = "https://image.tmdb.org/t/p/w780/xlaY2zyzMfkhk0HSC5VUwzoZPU1.jpg",
                sourceName = "Комсомольская правда",
                author = "Иван Иванов",
                publishedAt = "5 сентября 2026"
                ),
        NewsItem(
                title = "Что посмотреть на выходных: новые сериалы и фильмы",
                description = "",
                articleUrl = "https://example.com/ru-2",
                imageUrl = "",
                sourceName = "Forbes",
                author = "",
                publishedAt = "4 сентября 2026"
                )
                                            )

// --- Localization: Russian ----------------------------------------------

@RussianPhoneSizePreviews
@Composable
private fun NewsScreenContentPreviewRussian() {
    NewsScreenPreviewFrame(NewsScreenState.Content(sampleNewsItemsRussian))
}

@Preview(showBackground = true, locale = "ru", name = "Card - Russian")
@Composable
private fun NewsCardPreviewRussian() {
    MMoviesTheme {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
            NewsCard(
                    newsItem = sampleNewsItemsRussian[0],
                    onReadFullArticle = {},
                    modifier = Modifier.padding(padding).padding(12.dp)
                    )
        }
    }
}

@Preview(showBackground = true, locale = "ru", name = "Empty - Russian")
@Composable
private fun NewsScreenEmptyPreviewRussian() {
    NewsScreenPreviewFrame(NewsScreenState.Empty)
}
