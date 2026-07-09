package com.bk.mmovies.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bk.mmovies.ui.screen.details.MovieDetailsScreen
import com.bk.mmovies.ui.screen.movies.MoviesScreen
import com.bk.mmovies.ui.screen.splash.SplashScreen


private const val TAG = "AppNavigation"

@Composable
fun AppNavigation(
    padding: PaddingValues,
    navController: NavHostController,
    onAppExit: () -> Unit
) {
    val navigateToMovieDetails: (Int) -> Unit = { movieId ->
        navController.navigate(AppDestination.MovieDetailsDestination(movieId))
    }

    NavHost(
        navController,
        AppDestination.SplashDestination,
        Modifier.padding(paddingValues = padding),
        builder = {
            composable<AppDestination.SplashDestination>(content = {
                SplashScreen() {
                    navController.navigate(AppDestination.MoviesDestination) {
                        popUpTo<AppDestination.SplashDestination> {
                            inclusive = true
                        }
                    }
                }
            })
            composable<AppDestination.MoviesDestination>(content = {
                MoviesScreen(
                    onNavigateToMovieDetails = { id:Int ->
                        navigateToMovieDetails(id)
                    }
                )
            })
            composable<AppDestination.MovieDetailsDestination>(content = {
                MovieDetailsScreen()
            })
        },
    )
    BackHandler(
        enabled = true,
        onBack = {
            if (!navController.popBackStack()) {
                onAppExit()
            }
        })
}



