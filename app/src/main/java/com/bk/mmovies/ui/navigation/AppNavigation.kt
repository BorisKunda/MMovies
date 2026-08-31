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
import com.bk.mmovies.domain.model.Category
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.domain.model.TvSeriesCategory
import com.bk.mmovies.ui.screen.auth.AuthScreen
import com.bk.mmovies.ui.screen.catalog.CatalogScreen
import com.bk.mmovies.ui.screen.details.moviedetails.MovieDetailsScreen
import com.bk.mmovies.ui.screen.details.tvseriesdetails.seasondetails.SeasonDetailsScreen
import com.bk.mmovies.ui.screen.splash.SplashScreen
import com.bk.mmovies.ui.screen.details.tvseriesdetails.TvSeriesDetailsScreen
import com.bk.mmovies.ui.screen.privacypolicy.PrivacyPolicyScreen
import com.bk.mmovies.ui.screen.search.SearchScreen
import com.bk.mmovies.ui.screen.terms.TermsScreen

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
    val navigateToTvSeriesDetails: (Int) -> Unit = { seriesId ->
        navController.navigate(AppDestination.TvSeriesDetailsDestination(seriesId))
    }
    val navigateToSeasonDetails: (Int, Int) -> Unit = { seriesId, seasonNumber ->
        navController.navigate(
                AppDestination.SeasonDetailsDestination(
                        seriesId,
                        seasonNumber
                                                        )
                              )
    }
    val navigateToSearch: () -> Unit = {
        navController.navigate(AppDestination.SearchDestination)
    }
    val navigateToTerms: () -> Unit = {
        navController.navigate(AppDestination.TermsDestination)
    }
    val navigateToPrivacyPolicy: () -> Unit = {
        navController.navigate(AppDestination.PrivacyPolicyDestination)
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
                        // Splash can reach Movies directly (session already
                        // valid) without ever pushing Auth, so popUpTo(Auth)
                        // would find nothing to pop and leave Splash on the
                        // stack underneath Movies. Pop Splash itself instead —
                        // it's always present on this path.
                        navController.navigate(AppDestination.MoviesDestination) {
                            popUpTo<AppDestination.SplashDestination> {
                                inclusive = true
                            }
                        }
                    }, onNavigateToTerms = navigateToTerms)
                })

                composable<AppDestination.AuthDestination>(content = {
                    AuthScreen(onNavigateToMoviesScreen = {
                        navController.navigate(AppDestination.MoviesDestination) {
                            popUpTo<AppDestination.AuthDestination> {
                                inclusive = true
                            }
                        }
                    }, onNavigateToTerms = navigateToTerms)
                })

                composable<AppDestination.MoviesDestination>(content = {
                    CatalogScreen(onNavigateToDetailsScreen = { id: Int, category: Category ->
                        when (category) {
                            is MovieCategory    -> navigateToMovieDetails(id, category)
                            is TvSeriesCategory -> navigateToTvSeriesDetails(id)
                            else                -> {}
                        }
                    }, onNavigateToAuthScreen = {
                        navController.navigate(AppDestination.AuthDestination) {
                            popUpTo<AppDestination.MoviesDestination> {
                                inclusive = true
                            }
                        }
                    }, onNavigateToSearch = {
                        navigateToSearch()
                    }, onNavigateToTerms = navigateToTerms
                                 )
                })
                composable<AppDestination.MovieDetailsDestination>(content = { backStackEntry ->
                    val destination: AppDestination.MovieDetailsDestination =
                            backStackEntry.toRoute()
                    MovieDetailsScreen(
                            movieId = destination.movieId,
                            category = Category.fromCategoryId(destination.categoryId) as? MovieCategory
                                    ?: MovieCategory.PopularMovieCategory,
                            onBack = { navController.popBackStack() },
                            onNavigateToTerms = navigateToTerms
                                      )
                })
                composable<AppDestination.TvSeriesDetailsDestination>(content = { backStackEntry ->
                    val destination: AppDestination.TvSeriesDetailsDestination =
                            backStackEntry.toRoute()
                    TvSeriesDetailsScreen(
                            seriesId = destination.seriesId,
                            onBack = { navController.popBackStack() },
                            onNavigateToSeasonDetails = { seasonNumber ->
                                navigateToSeasonDetails(destination.seriesId, seasonNumber)
                            },
                            onNavigateToTerms = navigateToTerms
                                         )
                })
                composable<AppDestination.SeasonDetailsDestination>(content = { backStackEntry ->
                    val destination: AppDestination.SeasonDetailsDestination =
                            backStackEntry.toRoute()
                    SeasonDetailsScreen(
                            seriesId = destination.seriesId,
                            seasonNumber = destination.seasonNumber,
                            onBack = { navController.popBackStack() },
                            onNavigateToTerms = navigateToTerms
                                       )
                })
                composable<AppDestination.TermsDestination>(content = {
                    TermsScreen(
                            onBack = { navController.popBackStack() },
                            onNavigateToPrivacyPolicy = navigateToPrivacyPolicy
                               )
                })
                composable<AppDestination.PrivacyPolicyDestination>(content = {
                    PrivacyPolicyScreen(onBack = { navController.popBackStack() })
                })
                composable<AppDestination.SearchDestination>(content = {
                    SearchScreen(
                            onNavigateToMovieDetails = { movieId, category ->
                                navigateToMovieDetails(movieId, category)
                            },
                            onNavigateToTvSeriesDetails = { seriesId ->
                                navigateToTvSeriesDetails(seriesId)
                            },
                            onBack = { navController.popBackStack() },
                            onNavigateToTerms = navigateToTerms
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


