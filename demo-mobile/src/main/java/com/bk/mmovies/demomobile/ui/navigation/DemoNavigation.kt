package com.bk.mmovies.demomobile.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.bk.mmovies.demomobile.ui.screen.catalog.MockCatalogScreen
import com.bk.mmovies.demomobile.ui.screen.details.MOCK_MOVIE_ID_ARG
import com.bk.mmovies.demomobile.ui.screen.details.MOCK_SEASON_NUMBER_ARG
import com.bk.mmovies.demomobile.ui.screen.details.MOCK_SERIES_ID_ARG
import com.bk.mmovies.demomobile.ui.screen.details.MockItemDetailsScreen
import com.bk.mmovies.demomobile.ui.screen.details.MockSeasonDetailsScreen
import com.bk.mmovies.demomobile.ui.screen.details.MockTvSeriesDetailsScreen
import com.bk.mmovies.demomobile.ui.screen.search.MockSearchScreen

private const val CATALOG_ROUTE = "catalog"
private const val SEARCH_ROUTE = "search"
private const val MOVIE_DETAILS_ROUTE = "movieDetails"
private const val TV_SERIES_DETAILS_ROUTE = "tvSeriesDetails"
private const val SEASON_DETAILS_ROUTE = "seasonDetails"

@Composable
fun DemoNavigation(
        padding: PaddingValues,
        navController: NavHostController
                  ) {
    val navigateToMovieDetails: (Int) -> Unit = { movieId ->
        navController.navigate("$MOVIE_DETAILS_ROUTE/$movieId")
    }
    val navigateToTvSeriesDetails: (Int) -> Unit = { seriesId ->
        navController.navigate("$TV_SERIES_DETAILS_ROUTE/$seriesId")
    }

    NavHost(
            navController = navController,
            startDestination = CATALOG_ROUTE,
            modifier = Modifier.padding(padding)
           ) {
        composable(CATALOG_ROUTE) {
            MockCatalogScreen(
                    onNavigateToSearch = { navController.navigate(SEARCH_ROUTE) },
                    onNavigateToMovieDetails = navigateToMovieDetails,
                    onNavigateToTvSeriesDetails = navigateToTvSeriesDetails
                             )
        }
        composable(SEARCH_ROUTE) {
            MockSearchScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToMovieDetails = navigateToMovieDetails,
                    onNavigateToTvSeriesDetails = navigateToTvSeriesDetails
                            )
        }
        composable(
                route = "$MOVIE_DETAILS_ROUTE/{$MOCK_MOVIE_ID_ARG}",
                arguments = listOf(navArgument(MOCK_MOVIE_ID_ARG) { type = NavType.IntType })
                  ) {
            MockItemDetailsScreen(
                    onBack = { navController.popBackStack() }
                                 )
        }
        composable(
                route = "$TV_SERIES_DETAILS_ROUTE/{$MOCK_SERIES_ID_ARG}",
                arguments = listOf(navArgument(MOCK_SERIES_ID_ARG) { type = NavType.IntType })
                  ) {
            MockTvSeriesDetailsScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToSeasonDetails = { seriesId, seasonNumber ->
                        navController.navigate("$SEASON_DETAILS_ROUTE/$seriesId/$seasonNumber")
                    }
                                     )
        }
        composable(
                route = "$SEASON_DETAILS_ROUTE/{$MOCK_SERIES_ID_ARG}/{$MOCK_SEASON_NUMBER_ARG}",
                arguments = listOf(
                        navArgument(MOCK_SERIES_ID_ARG) { type = NavType.IntType },
                        navArgument(MOCK_SEASON_NUMBER_ARG) { type = NavType.IntType }
                                  )
                  ) {
            MockSeasonDetailsScreen(
                    onBack = { navController.popBackStack() }
                                   )
        }
    }
}
