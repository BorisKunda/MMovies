package com.bk.mmovies.ui.screen.details.tvseriesdetails

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.test.platform.app.InstrumentationRegistry
import com.bk.mmovies.R
import com.bk.mmovies.domain.model.CastMemberModel
import com.bk.mmovies.domain.model.SeasonModel
import com.bk.mmovies.domain.model.SeriesAirDateLabel
import com.bk.mmovies.domain.model.TvSeriesDetailsModel
import com.bk.mmovies.ui.screen.details.tvseriesdetails.screencomponents.TvSeriesDetailsScreenContent
import com.bk.mmovies.ui.theme.MMoviesTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Edge cases for the TV series details screen, isolated from the ViewModel
 * and network: each test drives [TvSeriesDetailsScreenContent] directly with
 * a fake model. Cast/crew rows are asserted on but never tapped, since
 * tapping one opens [com.bk.mmovies.ui.component.ActorDetailsDialog], which
 * pulls in a Hilt ViewModel this isolated host can't provide.
 */
class TvSeriesDetailsScreenContentEdgeCasesTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    private fun baseSeries(
            airDateLabel: SeriesAirDateLabel = SeriesAirDateLabel.Ended(yearRange = "2020 - 2022"),
            userScore: Int = 79,
            creators: List<CastMemberModel> = emptyList(),
            seasons: List<SeasonModel> = emptyList(),
            isFavorite: Boolean = false
                          ) = TvSeriesDetailsModel(
            id = 1,
            title = "Ended Show",
            posterUrl = "",
            backdropUrl = "",
            airDateLabel = airDateLabel,
            seasonsLabel = "3 Seasons",
            userScore = userScore,
            genres = listOf("Drama"),
            overview = "A long-running drama.",
            creators = creators,
            seasons = seasons,
            isFavorite = isFavorite
                                                   )

    private fun season(number: Int, name: String) = SeasonModel(
            id = number,
            name = name,
            overview = "",
            posterUrl = "",
            airDate = "2026-07-15",
            seasonNumber = number,
            episodeCountLabel = "10 Episodes"
                                                                 )

    private fun setTvSeriesDetailsScreenContent(
            tvSeriesDetails: TvSeriesDetailsModel,
            showFavoriteStar: Boolean = false,
            onFavoriteClicked: () -> Unit = {},
            onSeasonClicked: (seasonNumber: Int) -> Unit = {}
                                                ) {
        composeRule.setContent {
            MMoviesTheme {
                TvSeriesDetailsScreenContent(
                        tvSeriesDetails = tvSeriesDetails,
                        showFavoriteStar = showFavoriteStar,
                        onFavoriteClicked = onFavoriteClicked,
                        onSeasonClicked = onSeasonClicked
                                             )
            }
        }
    }

    @Test
    fun airDateLabel_ended_showsYearRange() {
        setTvSeriesDetailsScreenContent(
                baseSeries(airDateLabel = SeriesAirDateLabel.Ended(yearRange = "2020 - 2022"))
                                        )

        composeRule.onNodeWithText("2020 - 2022").assertIsDisplayed()
    }

    @Test
    fun airDateLabel_ongoing_showsOngoingText() {
        setTvSeriesDetailsScreenContent(
                baseSeries(airDateLabel = SeriesAirDateLabel.Ongoing(text = "2023 - Ongoing"))
                                        )

        composeRule.onNodeWithText("2023 - Ongoing").assertIsDisplayed()
    }

    @Test
    fun airDateLabel_upcoming_showsPremiereLabelAndDate() {
        setTvSeriesDetailsScreenContent(
                baseSeries(
                        airDateLabel = SeriesAirDateLabel.Upcoming(
                                premiereLabel = "Series Premiere",
                                dateText = "August 19, 2026"
                                                                   )
                          )
                                        )

        composeRule.onNodeWithText("Series Premiere").assertIsDisplayed()
        composeRule.onNodeWithText("August 19, 2026").assertIsDisplayed()
    }

    @Test
    fun zeroUserScore_hidesScoreRow() {
        setTvSeriesDetailsScreenContent(baseSeries(userScore = 0))

        composeRule.onNodeWithText(context.getString(R.string.user_score_label)).assertDoesNotExist()
    }

    @Test
    fun positiveUserScore_showsScoreRow() {
        setTvSeriesDetailsScreenContent(baseSeries(userScore = 79))

        composeRule.onNodeWithText(context.getString(R.string.user_score_label)).assertIsDisplayed()
    }

    @Test
    fun favoriteStar_hiddenWhenNotRequested() {
        setTvSeriesDetailsScreenContent(baseSeries(), showFavoriteStar = false)

        composeRule.onNodeWithContentDescription(context.getString(R.string.favorite_add_action))
                .assertDoesNotExist()
    }

    @Test
    fun favoriteStar_shownWhenRequested_tapInvokesCallback() {
        var favoriteClicked = false
        setTvSeriesDetailsScreenContent(
                baseSeries(),
                showFavoriteStar = true,
                onFavoriteClicked = { favoriteClicked = true }
                                        )

        composeRule.onNodeWithContentDescription(context.getString(R.string.favorite_add_action)).performClick()

        assertTrue("Favorite callback should fire on tap", favoriteClicked)
    }

    @Test
    fun posterLongPress_opensZoomDialogWithSecondPosterImage() {
        setTvSeriesDetailsScreenContent(baseSeries())

        composeRule.onAllNodesWithContentDescription("Ended Show").assertCountEquals(1)

        composeRule.onNodeWithContentDescription("Ended Show").performTouchInput { longClick() }

        composeRule.onAllNodesWithContentDescription("Ended Show").assertCountEquals(2)
    }

    @Test
    fun singleCreator_showsCreatorRoleRow() {
        val creator = CastMemberModel(id = 1, name = "Shonda Reign", character = "", profileUrl = "")
        setTvSeriesDetailsScreenContent(baseSeries(creators = listOf(creator)))

        composeRule.onNodeWithText(context.getString(R.string.details_creator_role)).assertIsDisplayed()
        composeRule.onNodeWithText("Shonda Reign").assertIsDisplayed()
    }

    @Test
    fun multipleCreators_showsCombinedCreatorsRow() {
        val creators = listOf(
                CastMemberModel(id = 1, name = "Shonda Reign", character = "", profileUrl = ""),
                CastMemberModel(id = 2, name = "Marc Idowu", character = "", profileUrl = "")
                              )
        setTvSeriesDetailsScreenContent(baseSeries(creators = creators))

        composeRule.onNodeWithText(context.getString(R.string.details_creators_label)).assertIsDisplayed()
        composeRule.onNodeWithText("Shonda Reign,").assertIsDisplayed()
        composeRule.onNodeWithText("Marc Idowu").assertIsDisplayed()
    }

    @Test
    fun seasons_rendersEachSeasonNameAndMeta() {
        val seasons = listOf(season(1, "Season 1"), season(2, "Season 2"))
        setTvSeriesDetailsScreenContent(baseSeries(seasons = seasons))

        composeRule.onNodeWithText("Season 1").assertIsDisplayed()
        composeRule.onNodeWithText("Season 2").assertIsDisplayed()
        composeRule.onAllNodesWithText("10 Episodes • 2026-07-15").assertCountEquals(2)
    }

    @Test
    fun tappingSecondSeasonRow_invokesCallbackWithThatSeasonNumberNotTheFirst() {
        var clickedSeasonNumber: Int? = null
        val seasons = listOf(season(1, "Season 1"), season(2, "Season 2"))
        setTvSeriesDetailsScreenContent(
                baseSeries(seasons = seasons),
                onSeasonClicked = { clickedSeasonNumber = it }
                                        )

        composeRule.onNodeWithText("Season 2").performClick()

        assertEquals("Tapping Season 2's row should report seasonNumber 2, not 1", 2, clickedSeasonNumber)
    }
}
