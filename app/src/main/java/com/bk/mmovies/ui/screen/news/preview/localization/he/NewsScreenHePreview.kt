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
import com.bk.mmovies.ui.theme.HebrewPhoneSizePreviews
import com.bk.mmovies.ui.theme.MMoviesTheme

// Hebrew sample data --------------------------------------------------------

private val sampleNewsItemsHebrew = listOf(
        NewsItem(
                title = "גל גדות בסרט אקשן חדש בן 84 דקות שובר שיא ברוטן טומייטוס",
                description = "הסרט ממשיך מגמה בת שלוש שנים שאינה חיובית באתר הביקורות.",
                articleUrl = "https://example.com/he-1",
                imageUrl = "https://image.tmdb.org/t/p/w780/xlaY2zyzMfkhk0HSC5VUwzoZPU1.jpg",
                sourceName = "כאן חדשות",
                author = "ישראל ישראלי",
                publishedAt = "5 בספטמבר 2026"
                ),
        NewsItem(
                title = "מה לצפות בסוף השבוע: תוכניות וסרטים חדשים בסטרימינג",
                description = "",
                articleUrl = "https://example.com/he-2",
                imageUrl = "",
                sourceName = "Forbes",
                author = "",
                publishedAt = "4 בספטמבר 2026"
                )
                                           )

// --- Localization: Hebrew (RTL) -------------------------------------------

@HebrewPhoneSizePreviews
@Composable
private fun NewsScreenContentPreviewHebrew() {
    NewsScreenPreviewFrame(NewsScreenState.Content(sampleNewsItemsHebrew))
}

@Preview(showBackground = true, locale = "iw", name = "Card - Hebrew (RTL)")
@Composable
private fun NewsCardPreviewHebrew() {
    MMoviesTheme {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
            NewsCard(
                    newsItem = sampleNewsItemsHebrew[0],
                    onReadFullArticle = {},
                    modifier = Modifier.padding(padding).padding(12.dp)
                    )
        }
    }
}

@Preview(showBackground = true, locale = "iw", name = "Empty - Hebrew (RTL)")
@Composable
private fun NewsScreenEmptyPreviewHebrew() {
    NewsScreenPreviewFrame(NewsScreenState.Empty)
}
