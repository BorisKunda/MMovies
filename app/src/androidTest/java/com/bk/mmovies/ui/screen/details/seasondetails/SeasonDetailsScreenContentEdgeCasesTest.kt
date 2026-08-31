package com.bk.mmovies.ui.screen.details.seasondetails

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.bk.mmovies.R
import com.bk.mmovies.domain.model.EpisodeModel
import com.bk.mmovies.domain.model.SeasonModel
import com.bk.mmovies.ui.screen.details.tvseriesdetails.seasondetails.screencomponents.SeasonDetailsScreenContent
import com.bk.mmovies.ui.theme.MMoviesTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Edge cases for the season details screen (episode list + episode dialog),
 * isolated from the ViewModel and network: each test drives
 * [SeasonDetailsScreenContent] directly with a fake model. Unlike the cast
 * dialog, [com.bk.mmovies.ui.screen.details.tvseriesdetails.seasondetails.episodedetails.EpisodeDetailsDialog] has no Hilt
 * dependency of its own, so it's safe to actually open in these tests — as
 * long as its own director/writer rows (which reopen the Hilt-backed actor
 * dialog) are never tapped.
 */
class SeasonDetailsScreenContentEdgeCasesTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    private fun episode(
            number: Int,
            name: String,
            overview: String = "An eventful episode.",
            runtime: String = "42m",
            airDate: String = "2026-07-15",
            rating: Int = 0
                        ) = EpisodeModel(
            id = number,
            name = name,
            overview = overview,
            airDate = airDate,
            episodeNumber = number,
            seasonNumber = 1,
            stillUrl = "",
            rating = rating,
            runtime = runtime
                                        )

    private fun baseSeason(
            overview: String = "The crew's first outing.",
            episodeCountLabel: String = "10 Episodes",
            airDate: String = "2026-07-15",
            episodes: List<EpisodeModel> = emptyList()
                           ) = SeasonModel(
            id = 1,
            name = "Season 1",
            overview = overview,
            posterUrl = "",
            airDate = airDate,
            seasonNumber = 1,
            episodeCountLabel = episodeCountLabel,
            episodes = episodes
                                          )

    private fun setSeasonDetailsScreenContent(season: SeasonModel) {
        composeRule.setContent {
            MMoviesTheme {
                SeasonDetailsScreenContent(season = season)
            }
        }
    }

    private fun episodeTitle(number: Int, name: String) =
            context.getString(R.string.details_episode_number_title, number, name)

    @Test
    fun seasonHeader_showsNameAndMeta() {
        setSeasonDetailsScreenContent(baseSeason(episodeCountLabel = "10 Episodes", airDate = "2026-07-15"))

        composeRule.onNodeWithText("Season 1").assertIsDisplayed()
        composeRule.onNodeWithText("10 Episodes • 2026-07-15").assertIsDisplayed()
    }

    @Test
    fun seasonHeader_blankMeta_rendersNoEmptyMetaLine() {
        setSeasonDetailsScreenContent(baseSeason(episodeCountLabel = "", airDate = ""))

        assertTrue(
                "Blank episode count and air date must not render an empty meta Text node",
                composeRule.onAllNodesWithText("").fetchSemanticsNodes().isEmpty()
                  )
    }

    @Test
    fun overview_shownWhenNonBlank() {
        setSeasonDetailsScreenContent(baseSeason(overview = "The crew's first outing."))

        composeRule.onNodeWithText(context.getString(R.string.details_overview_label)).assertIsDisplayed()
        composeRule.onNodeWithText("The crew's first outing.").assertIsDisplayed()
    }

    @Test
    fun overview_hiddenWhenBlank() {
        setSeasonDetailsScreenContent(baseSeason(overview = ""))

        composeRule.onNodeWithText(context.getString(R.string.details_overview_label)).assertDoesNotExist()
    }

    @Test
    fun noEpisodes_showsNoEpisodesLabel() {
        setSeasonDetailsScreenContent(baseSeason(episodes = emptyList()))

        composeRule.onNodeWithText(context.getString(R.string.details_episodes_label)).assertDoesNotExist()
    }

    @Test
    fun episodes_rendersEachEpisodeTitle() {
        val episodes = listOf(episode(1, "Pilot"), episode(2, "The Reckoning"))
        setSeasonDetailsScreenContent(baseSeason(episodes = episodes))

        composeRule.onNodeWithText(context.getString(R.string.details_episodes_label)).assertIsDisplayed()
        composeRule.onNodeWithText(episodeTitle(1, "Pilot")).assertIsDisplayed()
        composeRule.onNodeWithText(episodeTitle(2, "The Reckoning")).assertIsDisplayed()
    }

    @Test
    fun episodeRow_blankMetaAndOverview_rendersOnlyTitle() {
        val blankEpisode = episode(1, "Untitled", overview = "", runtime = "", airDate = "")
        setSeasonDetailsScreenContent(baseSeason(episodes = listOf(blankEpisode)))

        composeRule.onNodeWithText(episodeTitle(1, "Untitled")).assertIsDisplayed()
        assertTrue(
                "Blank episode meta/overview must not render empty Text nodes",
                composeRule.onAllNodesWithText("").fetchSemanticsNodes().isEmpty()
                  )
    }

    @Test
    fun tappingSecondEpisodeRow_opensDialogForThatEpisodeNotTheFirst() {
        val episodes = listOf(episode(1, "Pilot"), episode(2, "The Reckoning"))
        setSeasonDetailsScreenContent(baseSeason(episodes = episodes))

        // Before tapping, each episode's title exists exactly once (the list
        // row); the dialog reuses the same title string, so a successful
        // open is a second match for episode 2's title specifically, while
        // episode 1's title must stay at exactly one match.
        composeRule.onNodeWithText(episodeTitle(1, "Pilot")).performClick()
        composeRule.onAllNodesWithText(episodeTitle(1, "Pilot")).assertCountEquals(2)
        composeRule.onAllNodesWithText(episodeTitle(2, "The Reckoning")).assertCountEquals(1)
    }

    @Test
    fun episodeDialog_ratingZero_hidesScoreRow() {
        val ep = episode(1, "Pilot", rating = 0)
        setSeasonDetailsScreenContent(baseSeason(episodes = listOf(ep)))

        composeRule.onNodeWithText(episodeTitle(1, "Pilot")).performClick()

        composeRule.onNodeWithText(context.getString(R.string.user_score_label)).assertDoesNotExist()
    }

    @Test
    fun episodeDialog_ratingPositive_showsScoreRow() {
        val ep = episode(1, "Pilot", rating = 82)
        setSeasonDetailsScreenContent(baseSeason(episodes = listOf(ep)))

        composeRule.onNodeWithText(episodeTitle(1, "Pilot")).performClick()

        composeRule.onNodeWithText(context.getString(R.string.user_score_label)).assertIsDisplayed()
    }

    @Test
    fun episodeDialog_closeButtonDismissesIt() {
        val ep = episode(1, "Pilot")
        setSeasonDetailsScreenContent(baseSeason(episodes = listOf(ep)))

        composeRule.onNodeWithText(episodeTitle(1, "Pilot")).performClick()
        composeRule.onAllNodesWithText(episodeTitle(1, "Pilot")).assertCountEquals(2)

        composeRule.onNodeWithContentDescription(context.getString(R.string.action_close)).performClick()

        composeRule.onAllNodesWithText(episodeTitle(1, "Pilot")).assertCountEquals(1)
    }
}
