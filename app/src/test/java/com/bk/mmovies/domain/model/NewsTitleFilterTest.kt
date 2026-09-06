package com.bk.mmovies.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class NewsTitleFilterTest {

    private fun newsItem(title: String) = NewsItem(
            title = title,
            description = "",
            articleUrl = "https://example.com/$title",
            imageUrl = "",
            sourceName = ""
                                                    )

    @Test
    fun titleContainingKeyword_isKept() {
        val items = listOf(newsItem("Rob Lowe reveals iconic movie roles"))

        val result = items.filterByRequiredTitleKeywords()

        assertEquals(1, result.size)
    }

    @Test
    fun titleMissingAnyKeyword_isDropped() {
        val items = listOf(newsItem("Brooke Hogan threatens to expose family receipts"))

        val result = items.filterByRequiredTitleKeywords()

        assertEquals(0, result.size)
    }

    // Regression: NewsAPI often appends the source name straight into the
    // title (e.g. "... - StyleCaster"), and "StyleCaster" contains "caster",
    // which contains the keyword "cast" as a raw substring. A naive
    // String.contains would wrongly keep this unrelated Harry & Meghan
    // gossip piece just because of the source name suffix.
    @Test
    fun keywordEmbeddedInsideAnotherWord_isNotAMatch() {
        val items = listOf(
                newsItem(
                        "The Actual Reason Harry & Meghan Moved Back to the UK After Report He " +
                                "Gave Her a Major 'Ultimatum' Amid Rumored Marriage Struggles - StyleCaster"
                        )
                           )

        val result = items.filterByRequiredTitleKeywords()

        assertEquals(0, result.size)
    }

    @Test
    fun keywordAsWholeWord_isStillMatched() {
        val items = listOf(newsItem("Netflix announces new season of a hit show"))

        val result = items.filterByRequiredTitleKeywords()

        assertEquals(1, result.size)
    }

    @Test
    fun symbolKeyword_matchesAtEndOfTitle() {
        val items = listOf(newsItem("Everything coming to Disney+ this month"))

        val result = items.filterByRequiredTitleKeywords()

        assertEquals(1, result.size)
    }

    @Test
    fun multiWordKeyword_matches() {
        val items = listOf(newsItem("New movie tops the box office this weekend"))

        val result = items.filterByRequiredTitleKeywords()

        assertEquals(1, result.size)
    }
}
