package com.bk.mmovies.ui.screen.details.moviedetails

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
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.domain.model.MovieDetailsModel
import com.bk.mmovies.ui.screen.details.moviedetails.screencomponents.MovieDetailsScreenContent
import com.bk.mmovies.ui.theme.MMoviesTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Edge cases for the movie details screen, isolated from the ViewModel and
 * network: each test drives [MovieDetailsScreenContent] directly with a fake
 * model. Cast/crew rows are asserted on but never tapped, since tapping one
 * opens [com.bk.mmovies.ui.component.ActorDetailsDialog], which pulls in a
 * Hilt ViewModel this isolated host can't provide.
 */
class DetailsScreenContentEdgeCasesTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    private fun baseMovie(
            releaseDate: String = "July 15, 2026",
            runtime: String = "2h 15m",
            overview: String = "A crew ventures into the unknown.",
            genres: List<String> = listOf("Action", "Thriller"),
            cast: List<CastMemberModel> = emptyList(),
            director: CastMemberModel? = null,
            writers: List<CastMemberModel> = emptyList(),
            isFavorite: Boolean = false
                          ) = MovieDetailsModel(
            id = 1,
            title = "The Odyssey",
            posterUrl = "",
            backdropUrl = "",
            releaseDate = releaseDate,
            runtime = runtime,
            userScore = 79,
            genres = genres,
            overview = overview,
            cast = cast,
            director = director,
            writers = writers,
            isFavorite = isFavorite
                                                )

    private fun setDetailsScreenContent(
            movieDetails: MovieDetailsModel,
            category: MovieCategory = MovieCategory.PopularMovieCategory,
            showFavoriteStar: Boolean = false,
            onFavoriteClicked: () -> Unit = {}
                                        ) {
        composeRule.setContent {
            MMoviesTheme {
                MovieDetailsScreenContent(
                        movieDetails = movieDetails,
                        category = category,
                        showFavoriteStar = showFavoriteStar,
                        onFavoriteClicked = onFavoriteClicked
                                         )
            }
        }
    }

    @Test
    fun upcomingCategory_blankFields_showsTbaLabels() {
        setDetailsScreenContent(
                movieDetails = baseMovie(releaseDate = "", runtime = "", overview = ""),
                category = MovieCategory.UpcomingMovieCategory
                                )

        composeRule.onNodeWithText(context.getString(R.string.details_date_tba)).assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.details_runtime_tba)).assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.details_overview_tba)).assertIsDisplayed()
    }

    @Test
    fun nonUpcomingCategory_blankFields_rendersNoEmptyRowsOrTbaLabels() {
        setDetailsScreenContent(
                movieDetails = baseMovie(releaseDate = "", runtime = "", overview = ""),
                category = MovieCategory.PopularMovieCategory
                                )

        // Only Upcoming gets a "TBA" placeholder; every other category just
        // drops the blank row entirely rather than showing an empty one.
        composeRule.onNodeWithText(context.getString(R.string.details_date_tba)).assertDoesNotExist()
        assertTrue(
                "A blank meta field must not render an empty Text node",
                composeRule.onAllNodesWithText("").fetchSemanticsNodes().isEmpty()
                  )
    }

    @Test
    fun favoriteStar_hiddenForUpcomingCategory_evenWhenRequested() {
        setDetailsScreenContent(
                movieDetails = baseMovie(),
                category = MovieCategory.UpcomingMovieCategory,
                showFavoriteStar = true
                                )

        composeRule.onNodeWithContentDescription(context.getString(R.string.favorite_add_action))
                .assertDoesNotExist()
    }

    @Test
    fun favoriteStar_shownForRegularCategory_tapInvokesCallback() {
        var favoriteClicked = false
        setDetailsScreenContent(
                movieDetails = baseMovie(),
                category = MovieCategory.PopularMovieCategory,
                showFavoriteStar = true,
                onFavoriteClicked = { favoriteClicked = true }
                                )

        composeRule.onNodeWithContentDescription(context.getString(R.string.favorite_add_action)).performClick()

        assertTrue("Favorite callback should fire on tap", favoriteClicked)
    }

    @Test
    fun posterLongPress_opensZoomDialogWithSecondPosterImage() {
        setDetailsScreenContent(movieDetails = baseMovie())

        // The header poster and the zoomed poster both use the movie title
        // as their contentDescription, so a successful zoom is exactly one
        // more match for that description.
        composeRule.onAllNodesWithContentDescription("The Odyssey").assertCountEquals(1)

        composeRule.onNodeWithContentDescription("The Odyssey").performTouchInput { longClick() }

        composeRule.onAllNodesWithContentDescription("The Odyssey").assertCountEquals(2)
    }

    @Test
    fun singleWriter_showsWriterRoleRow() {
        val writer = CastMemberModel(id = 1, name = "Nolan Kade", character = "", profileUrl = "")
        setDetailsScreenContent(movieDetails = baseMovie(writers = listOf(writer)))

        composeRule.onNodeWithText(context.getString(R.string.details_writer_role)).assertIsDisplayed()
        composeRule.onNodeWithText("Nolan Kade").assertIsDisplayed()
    }

    @Test
    fun multipleWriters_showsCombinedWritersRow() {
        val writers = listOf(
                CastMemberModel(id = 1, name = "Nolan Kade", character = "", profileUrl = ""),
                CastMemberModel(id = 2, name = "Ava Sorel", character = "", profileUrl = "")
                             )
        setDetailsScreenContent(movieDetails = baseMovie(writers = writers))

        composeRule.onNodeWithText(context.getString(R.string.details_writers_label)).assertIsDisplayed()
        // Every writer but the last gets a trailing comma so the row reads
        // as one continuous list.
        composeRule.onNodeWithText("Nolan Kade,").assertIsDisplayed()
        composeRule.onNodeWithText("Ava Sorel").assertIsDisplayed()
    }

    @Test
    fun director_showsDirectorRoleRow() {
        val director = CastMemberModel(id = 3, name = "Priya Nasser", character = "", profileUrl = "")
        setDetailsScreenContent(movieDetails = baseMovie(director = director))

        composeRule.onNodeWithText(context.getString(R.string.details_director_role)).assertIsDisplayed()
        composeRule.onNodeWithText("Priya Nasser").assertIsDisplayed()
    }

    @Test
    fun upcomingCategory_emptyCast_showsCastTbaLabel() {
        setDetailsScreenContent(
                movieDetails = baseMovie(cast = emptyList()),
                category = MovieCategory.UpcomingMovieCategory
                                )

        composeRule.onNodeWithText(context.getString(R.string.details_cast_tba)).assertIsDisplayed()
    }

    @Test
    fun nonUpcomingCategory_emptyCast_showsNoCastSectionAtAll() {
        setDetailsScreenContent(
                movieDetails = baseMovie(cast = emptyList()),
                category = MovieCategory.PopularMovieCategory
                                )

        composeRule.onNodeWithText(context.getString(R.string.details_cast_tba)).assertDoesNotExist()
        composeRule.onNodeWithText(context.getString(R.string.details_cast_label)).assertDoesNotExist()
    }

    @Test
    fun nonEmptyCast_showsCastLabelAndMemberName() {
        val cast = listOf(CastMemberModel(id = 5, name = "Jason Statham", character = "Cole Reed", profileUrl = ""))
        setDetailsScreenContent(movieDetails = baseMovie(cast = cast))

        composeRule.onNodeWithText(context.getString(R.string.details_cast_label)).assertIsDisplayed()
        composeRule.onNodeWithText("Jason Statham").assertIsDisplayed()
    }
}
