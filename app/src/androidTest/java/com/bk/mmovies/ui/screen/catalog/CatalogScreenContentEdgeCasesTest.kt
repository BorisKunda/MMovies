package com.bk.mmovies.ui.screen.catalog

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.bk.mmovies.R
import com.bk.mmovies.domain.model.CatalogItem
import com.bk.mmovies.domain.model.CatalogMediaType
import com.bk.mmovies.domain.model.Category
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.domain.model.SeriesAirDateLabel
import com.bk.mmovies.domain.model.TvSeriesCategory
import com.bk.mmovies.ui.screen.catalog.screencomponents.CatalogScreenContent
import com.bk.mmovies.ui.theme.MMoviesTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Edge cases for the catalog list, isolated from the ViewModel and network:
 * each test drives [CatalogScreenContent] directly with fake state, mirroring
 * the equivalent search result tests so the two screens' shared logic
 * (release date formatting, TV air-date labels) stays provably in sync.
 */
class CatalogScreenContentEdgeCasesTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    private fun movieItem(id: Int, title: String, releaseDate: String, isFavorite: Boolean = false) = CatalogItem(
            id = id,
            title = title,
            imageUrl = "",
            releaseDate = releaseDate,
            mediaType = CatalogMediaType.MOVIE,
            rating = 70,
            isFavorite = isFavorite
                                                                                                                  )

    private fun tvItem(id: Int, title: String, isFavorite: Boolean = false) = CatalogItem(
            id = id,
            title = title,
            imageUrl = "",
            releaseDate = "2026-07-15",
            mediaType = CatalogMediaType.TV_SERIES,
            rating = 70,
            isFavorite = isFavorite
                                                                                           )

    private fun setCatalogScreenContent(
            state: CatalogScreenState,
            selectedCategory: Category,
            isGuest: Boolean = false,
            onRetry: () -> Unit = {},
            onFavoriteClicked: (catalogItem: CatalogItem) -> Unit = {},
            getTvSeriesAirDateLabel: suspend (seriesId: Int) -> SeriesAirDateLabel = { SeriesAirDateLabel.Upcoming("", "") }
                                        ) {
        composeRule.setContent {
            MMoviesTheme {
                CatalogScreenContent(
                        state = state,
                        selectedCategory = selectedCategory,
                        isGuest = isGuest,
                        onCatalogItemClicked = {},
                        onRetry = onRetry,
                        onFavoriteClicked = onFavoriteClicked,
                        getTvSeriesAirDateLabel = getTvSeriesAirDateLabel
                                     )
            }
        }
    }

    @Test
    fun emptyState_moviesCategory_showsMoviesMessage() {
        setCatalogScreenContent(
                state = CatalogScreenState.Empty,
                selectedCategory = MovieCategory.PopularMovieCategory
                                )

        composeRule.onNodeWithText(context.getString(R.string.empty_movies_message)).assertIsDisplayed()
    }

    @Test
    fun emptyState_tvCategory_showsTvMessage() {
        setCatalogScreenContent(
                state = CatalogScreenState.Empty,
                selectedCategory = TvSeriesCategory.PopularTvSeriesCategory
                                )

        composeRule.onNodeWithText(context.getString(R.string.empty_tv_series_message)).assertIsDisplayed()
    }

    @Test
    fun emptyState_favoritesCategory_showsFavoritesMessage() {
        setCatalogScreenContent(
                state = CatalogScreenState.Empty,
                selectedCategory = MovieCategory.FavoritesMovieCategory
                                )

        composeRule.onNodeWithText(context.getString(R.string.empty_favorites_message)).assertIsDisplayed()
    }

    @Test
    fun favoritesLoginRequired_showsGuestMessageRegardlessOfCategory() {
        setCatalogScreenContent(
                state = CatalogScreenState.FavoritesLoginRequired,
                selectedCategory = MovieCategory.FavoritesMovieCategory,
                isGuest = true
                                )

        composeRule.onNodeWithText(context.getString(R.string.empty_favorites_guest_message)).assertIsDisplayed()
    }

    @Test
    fun errorState_showsMessageAndRetryTriggersCallback() {
        var retried = false
        setCatalogScreenContent(
                state = CatalogScreenState.Error("Network unreachable"),
                selectedCategory = MovieCategory.PopularMovieCategory,
                onRetry = { retried = true }
                                )

        composeRule.onNodeWithText("Network unreachable").assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.try_again)).performClick()
        assertTrue("Retry callback should fire on tap", retried)
    }

    @Test
    fun movieRow_blankReleaseDate_rendersNoDateText() {
        setCatalogScreenContent(
                state = CatalogScreenState.Content(
                        catalogItems = listOf(movieItem(1, "Untitled", releaseDate = ""))
                                                   ),
                selectedCategory = MovieCategory.PopularMovieCategory
                                )

        composeRule.onNodeWithText("Untitled").assertIsDisplayed()
        // A regression that drops the isNotBlank() guard would render an
        // empty Text node instead of skipping it entirely.
        assertTrue(
                "Blank release date must not render an empty Text node",
                composeRule.onAllNodesWithText("").fetchSemanticsNodes().isEmpty()
                  )
    }

    @Test
    fun movieRow_withReleaseDate_showsFormattedDate() {
        setCatalogScreenContent(
                state = CatalogScreenState.Content(
                        catalogItems = listOf(movieItem(1, "The Odyssey", releaseDate = "July 15, 2026"))
                                                   ),
                selectedCategory = MovieCategory.PopularMovieCategory
                                )

        composeRule.onNodeWithText("July 15, 2026").assertIsDisplayed()
    }

    @Test
    fun tvRow_endedStatus_showsYearRange() {
        setCatalogScreenContent(
                state = CatalogScreenState.Content(catalogItems = listOf(tvItem(1, "Ended Show"))),
                selectedCategory = TvSeriesCategory.PopularTvSeriesCategory,
                getTvSeriesAirDateLabel = { SeriesAirDateLabel.Ended(yearRange = "2020 - 2022") }
                                )

        composeRule.onNodeWithText("2020 - 2022").assertIsDisplayed()
    }

    @Test
    fun tvRow_ongoingStatus_showsOngoingLabel() {
        setCatalogScreenContent(
                state = CatalogScreenState.Content(catalogItems = listOf(tvItem(1, "Ongoing Show"))),
                selectedCategory = TvSeriesCategory.PopularTvSeriesCategory,
                getTvSeriesAirDateLabel = { SeriesAirDateLabel.Ongoing(text = "2023 - Ongoing") }
                                )

        composeRule.onNodeWithText("2023 - Ongoing").assertIsDisplayed()
    }

    @Test
    fun tvRow_upcomingStatus_showsPremiereLabelAndDate() {
        setCatalogScreenContent(
                state = CatalogScreenState.Content(catalogItems = listOf(tvItem(1, "New Show"))),
                selectedCategory = TvSeriesCategory.PopularTvSeriesCategory,
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
    fun favoriteStar_hiddenForGuest() {
        setCatalogScreenContent(
                state = CatalogScreenState.Content(
                        catalogItems = listOf(movieItem(1, "Movie A", releaseDate = "July 15, 2026"))
                                                   ),
                selectedCategory = MovieCategory.PopularMovieCategory,
                // Guest sessions can't favorite anything, regardless of
                // category.
                isGuest = true
                                )

        composeRule.onNodeWithContentDescription(context.getString(R.string.favorite_add_action))
                .assertDoesNotExist()
    }

    @Test
    fun favoriteStar_hiddenForUpcomingCategory() {
        setCatalogScreenContent(
                state = CatalogScreenState.Content(
                        catalogItems = listOf(movieItem(1, "Movie A", releaseDate = "July 15, 2026"))
                                                   ),
                // Unreleased titles can't be favorited yet.
                selectedCategory = MovieCategory.UpcomingMovieCategory
                                )

        composeRule.onNodeWithContentDescription(context.getString(R.string.favorite_add_action))
                .assertDoesNotExist()
    }

    @Test
    fun favoriteStar_shownForRegularCategory_tapInvokesCallbackWithThatItem() {
        var clickedItemId: Int? = null
        setCatalogScreenContent(
                state = CatalogScreenState.Content(
                        catalogItems = listOf(
                                movieItem(1, "Movie A", releaseDate = "July 15, 2026"),
                                movieItem(2, "Movie B", releaseDate = "August 1, 2026")
                                              )
                                                   ),
                selectedCategory = MovieCategory.PopularMovieCategory,
                onFavoriteClicked = { clickedItemId = it.id }
                                )

        composeRule.onAllNodesWithContentDescription(context.getString(R.string.favorite_add_action))[0]
                .performClick()

        assertEquals("Tapping the first row's star should favorite that row's item", 1, clickedItemId)
    }
}
