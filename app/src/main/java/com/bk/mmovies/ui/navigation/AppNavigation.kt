package com.bk.mmovies.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.ui.screen.auth.AuthScreen
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
    val navigateToMovieDetails: (Int, MovieCategory) -> Unit = { movieId, category ->
        navController.navigate(
                AppDestination.MovieDetailsDestination(
                        movieId,
                        category.categoryId
                                                      )
                              )
    }

    NavHost(
            navController,
            AppDestination.SplashDestination,
            Modifier.padding(paddingValues = padding),
            builder = {
                composable<AppDestination.SplashDestination>(content = {
                    SplashScreen(onNavigateToAuthScreen = {
                        navController.navigate(AppDestination.AuthDestination) {
                            popUpTo<AppDestination.SplashDestination> {
                                inclusive = true
                            }
                        }
                    }, onNavigateToMoviesScreen = {
                        navController.navigate(AppDestination.MoviesDestination) {
                            popUpTo<AppDestination.AuthDestination> {
                                inclusive = true
                            }
                        }
                    })
                })

                composable<AppDestination.AuthDestination>(content = {
                    AuthScreen(onNavigateToMoviesScreen = {
                        navController.navigate(AppDestination.MoviesDestination) {
                            popUpTo<AppDestination.AuthDestination> {
                                inclusive = true
                            }
                        }
                    })
                })

                composable<AppDestination.MoviesDestination>(content = {
                    MoviesScreen(onNavigateToMovieDetailsScreen = { id: Int, category: MovieCategory ->
                        navigateToMovieDetails(
                                id,
                                category
                                              )
                    }
                                )
                })
                composable<AppDestination.MovieDetailsDestination>(content = { backStackEntry ->
                    val destination: AppDestination.MovieDetailsDestination =
                            backStackEntry.toRoute()
                    MovieDetailsScreen(
                            movieId = destination.movieId,
                            category = MovieCategory.fromCategoryId(destination.categoryId),
                            onBack = { navController.popBackStack() }
                                      )
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



