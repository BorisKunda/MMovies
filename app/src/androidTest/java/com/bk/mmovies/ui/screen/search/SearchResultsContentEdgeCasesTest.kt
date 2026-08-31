package com.bk.mmovies.ui.screen.search

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.bk.mmovies.R
import com.bk.mmovies.domain.model.SearchResultMediaType
import com.bk.mmovies.domain.model.SearchResultModel
import com.bk.mmovies.domain.model.SeriesAirDateLabel
import com.bk.mmovies.ui.screen.search.screencomponents.SearchFilterRow
import com.bk.mmovies.ui.screen.search.screencomponents.SearchResultsContent
import com.bk.mmovies.ui.theme.MMoviesTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Edge cases for the search results list, isolated from the ViewModel and
 * network: each test drives [SearchResultsContent]/[SearchFilterRow]
 * directly with fake state, the same fakes a Content state could contain
 * from a real /search/multi response.
 */
class SearchResultsContentEdgeCasesTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    private fun movie(id: Int, title: String, subtitle: String = "July 15, 2026") = SearchResultModel(
            id = id,
            mediaType = SearchResultMediaType.MOVIE,
            title = title,
            imageUrl = "",
            subtitle = subtitle
                                                                                                       )

    private fun tvSeries(id: Int, title: String) = SearchResultModel(
            id = id,
            mediaType = SearchResultMediaType.TV_SERIES,
            title = title,
            imageUrl = "",
            subtitle = "2026-07-15"
                                                                      )

    private fun person(id: Int, title: String) = SearchResultModel(
            id = id,
            mediaType = SearchResultMediaType.PERSON,
            title = title,
            imageUrl = "",
            subtitle = ""
                                                                    )

    private fun setSearchResultsContent(
            state: SearchResultsUiState,
            selectedFilters: Set<SearchResultMediaType> = emptySet(),
            onRetry: () -> Unit = {},
            onLoadNextPage: () -> Unit = {},
            getTvSeriesAirDateLabel: suspend (seriesId: Int) -> SeriesAirDateLabel = { SeriesAirDateLabel.Upcoming("", "") }
                                        ) {
        composeRule.setContent {
            MMoviesTheme {
                SearchResultsContent(
                        state = state,
                        selectedFilters = selectedFilters,
                        onResultClicked = {},
                        onRetry = onRetry,
                        onLoadNextPage = onLoadNextPage,
                        getTvSeriesAirDateLabel = getTvSeriesAirDateLabel
                                     )
            }
        }
    }

    @Test
    fun emptyState_showsEmptyMessage() {
        setSearchResultsContent(state = SearchResultsUiState.Empty)

        composeRule.onNodeWithText(context.getString(R.string.search_empty_title)).assertIsDisplayed()
    }

    @Test
    fun errorState_showsMessageAndRetryTriggersCallback() {
        var retried = false
        setSearchResultsContent(
                state = SearchResultsUiState.Error("Network unreachable"),
                onRetry = { retried = true }
                                )

        composeRule.onNodeWithText("Network unreachable").assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.try_again)).performClick()
        assertTrue("Retry callback should fire on tap", retried)
    }

    @Test
    fun movieResult_blankSubtitle_rendersNoSubtitleText() {
        setSearchResultsContent(
                state = SearchResultsUiState.Content(results = listOf(movie(1, "Untitled", subtitle = "")))
                                )

        composeRule.onNodeWithText("Untitled").assertIsDisplayed()
        // A regression that drops the isNotBlank() guard would render an
        // empty Text node instead of skipping it entirely.
        assertTrue(
                "Blank subtitle must not render an empty Text node",
                composeRule.onAllNodesWithText("").fetchSemanticsNodes().isEmpty()
                  )
    }

    @Test
    fun movieResult_withSubtitle_rendersFormattedDate() {
        setSearchResultsContent(
                state = SearchResultsUiState.Content(
                        results = listOf(movie(1, "The Odyssey", subtitle = "July 15, 2026"))
                                                     )
                                )

        composeRule.onNodeWithText("July 15, 2026").assertIsDisplayed()
    }

    @Test
    fun tvResult_endedStatus_showsYearRange() {
        setSearchResultsContent(
                state = SearchResultsUiState.Content(results = listOf(tvSeries(1, "Ended Show"))),
                getTvSeriesAirDateLabel = { SeriesAirDateLabel.Ended(yearRange = "2020 - 2022") }
                                )

        composeRule.onNodeWithText("2020 - 2022").assertIsDisplayed()
    }

    @Test
    fun tvResult_ongoingStatus_showsOngoingLabel() {
        setSearchResultsContent(
                state = SearchResultsUiState.Content(results = listOf(tvSeries(1, "Ongoing Show"))),
                getTvSeriesAirDateLabel = { SeriesAirDateLabel.Ongoing(text = "2023 - Ongoing") }
                                )

        composeRule.onNodeWithText("2023 - Ongoing").assertIsDisplayed()
    }

    @Test
    fun tvResult_upcomingStatus_showsPremiereLabelAndDate() {
        setSearchResultsContent(
                state = SearchResultsUiState.Content(results = listOf(tvSeries(1, "New Show"))),
                getTvSeriesAirDateLabel = {
                    SeriesAirDateLabel.Upcoming(
                            premiereLabel = "Series Premiere",
                            dateText = "August 19, 2026"
                                               )
                }
                                )

        composeRule.onNodeWithText("Series Premiere").assertIsDisplayed()
        composeRule.onNodeWithText("August 19, 2026").assertIsDisplayed()
    }

    @Test
    fun personResult_showsTitleAndBadgeWithoutSubtitleFetch() {
        setSearchResultsContent(
                state = SearchResultsUiState.Content(results = listOf(person(1, "Jason Statham"))),
                // A person row must never call this — there's no TMDB
                // id-to-series relationship for it.
                getTvSeriesAirDateLabel = { error("Person rows must not fetch a TV air-date label") }
                                )

        composeRule.onNodeWithText("Jason Statham").assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.search_result_type_person)).assertIsDisplayed()
    }

    @Test
    fun filterExcludesAllLoadedResults_triggersAutoPageWhilePagesRemain() {
        var loadNextPageCalls = 0
        setSearchResultsContent(
                state = SearchResultsUiState.Content(
                        results = listOf(tvSeries(1, "Only TV Result")),
                        currentPage = 1,
                        endReached = false
                                                     ),
                // Excludes the only loaded result, and a later page could
                // still hold a movie match.
                selectedFilters = setOf(SearchResultMediaType.MOVIE),
                onLoadNextPage = { loadNextPageCalls++ }
                                )

        composeRule.waitForIdle()
        assertTrue(
                "A non-final page under the auto-page cap should trigger onLoadNextPage",
                loadNextPageCalls > 0
                  )
    }

    @Test
    fun filterExcludesAllLoadedResults_stopsAtAutoPageCapAndShowsEmptyState() {
        var loadNextPageCalls = 0
        setSearchResultsContent(
                state = SearchResultsUiState.Content(
                        // MAX_FILTER_AUTO_PAGE is 5; currentPage == 5 fails
                        // the "< MAX" check, so this must stop paging rather
                        // than loop forever.
                        results = listOf(tvSeries(1, "Only TV Result")),
                        currentPage = 5,
                        endReached = false
                                                     ),
                selectedFilters = setOf(SearchResultMediaType.MOVIE),
                onLoadNextPage = { loadNextPageCalls++ }
                                )

        composeRule.waitForIdle()
        composeRule.onNodeWithText(context.getString(R.string.search_empty_title)).assertIsDisplayed()
        assertEquals(
                "The auto-page cap must stop further onLoadNextPage calls",
                0,
                loadNextPageCalls
                    )
    }

    @Test
    fun filterRow_togglingChipTwice_invokesCallbackForThatMediaTypeEachTime() {
        val toggledTypes = mutableListOf<SearchResultMediaType>()
        composeRule.setContent {
            MMoviesTheme {
                SearchFilterRow(
                        selectedFilters = emptySet(),
                        onFilterToggled = { toggledTypes.add(it) }
                               )
            }
        }

        val movieLabel = context.getString(SearchResultMediaType.MOVIE.labelResId)
        composeRule.onNodeWithText(movieLabel).performClick()
        composeRule.onNodeWithText(movieLabel).performClick()

        assertEquals(listOf(SearchResultMediaType.MOVIE, SearchResultMediaType.MOVIE), toggledTypes)
    }
}
