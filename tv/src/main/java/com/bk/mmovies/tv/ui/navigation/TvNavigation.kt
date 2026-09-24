package com.bk.mmovies.tv.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.bk.mmovies.domain.model.CatalogItem
import com.bk.mmovies.domain.model.CatalogMediaType
import com.bk.mmovies.domain.model.Category
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.tv.ui.auth.AuthTvScreenContainer
import com.bk.mmovies.tv.ui.catalog.CatalogTvScreen
import com.bk.mmovies.tv.ui.inputtest.InputMonitorTestScreen
import com.bk.mmovies.tv.ui.details.moviedetails.MovieDetailsTvScreen
import com.bk.mmovies.tv.ui.details.tvseriesdetails.TvSeriesDetailsTvScreen
import com.bk.mmovies.tv.ui.news.NewsDetailsTvScreen
import com.bk.mmovies.tv.ui.splash.SplashScreenTv
import com.bk.mmovies.tv.ui.webview.WebViewTvScreen

// Debug switch: true launches straight into the TvInputMonitor playground
// (BACK exits the app) instead of Splash. Keep false for normal use.
private const val START_ON_INPUT_TEST_SCREEN = false

@Composable
fun TvNavigation(
        navController: NavHostController,
        modifier: Modifier = Modifier,
        firstItemFocusRequester: FocusRequester? = null,
        onAppExit: () -> Unit = {}
                 ) {
    val navigateToMovieDetails: (CatalogItem, Category?) -> Unit = { item, category ->
        when (item.mediaType) {
            CatalogMediaType.MOVIE      -> {
                val categoryId = (category as? MovieCategory ?: MovieCategory.PopularMovieCategory).categoryId
                navController.navigate(TvDestination.MovieDetailsDestination(item.id, categoryId))
            }
            CatalogMediaType.TV_SERIES  -> {
                navController.navigate(TvDestination.TvSeriesDetailsDestination(item.id))
            }
        }
    }

    NavHost(
            navController,
            if (START_ON_INPUT_TEST_SCREEN) TvDestination.InputTestDestination else TvDestination.SplashDestination,
            modifier,
            builder = {
                composable<TvDestination.InputTestDestination>(content = {
                    InputMonitorTestScreen(onExit = onAppExit)
                })

                composable<TvDestination.SplashDestination>(content = {
                    SplashScreenTv(
                            onNavigateToAuthScreen = {
                                navController.navigate(TvDestination.AuthDestination) {
                                    popUpTo<TvDestination.SplashDestination> { inclusive = true }
                                }
                            },
                            onNavigateToMoviesScreen = {
                                navController.navigate(TvDestination.MoviesDestination) {
                                    popUpTo<TvDestination.SplashDestination> { inclusive = true }
                                }
                            }
                                  )
                })

                composable<TvDestination.AuthDestination>(content = {
                    AuthTvScreenContainer(
                            onNavigateToMoviesScreen = {
                                navController.navigate(TvDestination.MoviesDestination) {
                                    popUpTo<TvDestination.AuthDestination> { inclusive = true }
                                }
                            }
                                          )
                })

                composable<TvDestination.MoviesDestination>(content = {
                    CatalogTvScreen(
                            onViewDetailsClicked = navigateToMovieDetails,
                            onMovieClicked = { item -> navigateToMovieDetails(item, null) },
                            onNewsItemClicked = { newsItem ->
                                navController.navigate(
                                        TvDestination.NewsDetailsDestination(
                                                title = newsItem.title,
                                                description = newsItem.description,
                                                articleUrl = newsItem.articleUrl,
                                                imageUrl = newsItem.imageUrl,
                                                sourceName = newsItem.sourceName,
                                                author = newsItem.author,
                                                publishedAt = newsItem.publishedAt
                                                                             )
                                                       )
                            },
                            onNavigateToAuth = { navController.navigate(TvDestination.AuthDestination) },
                            firstItemFocusRequester = firstItemFocusRequester
                                   )
                })

                composable<TvDestination.MovieDetailsDestination>(content = { backStackEntry ->
                    val destination: TvDestination.MovieDetailsDestination = backStackEntry.toRoute()
                    MovieDetailsTvScreen(
                            movieId = destination.movieId,
                            categoryId = destination.categoryId
                                        )
                })

                composable<TvDestination.TvSeriesDetailsDestination>(content = { backStackEntry ->
                    val destination: TvDestination.TvSeriesDetailsDestination = backStackEntry.toRoute()
                    TvSeriesDetailsTvScreen(seriesId = destination.seriesId)
                })

                composable<TvDestination.WebViewDestination>(content = { backStackEntry ->
                    val destination: TvDestination.WebViewDestination = backStackEntry.toRoute()
                    WebViewTvScreen(
                            url = destination.url,
                            onBack = { navController.popBackStack() }
                                    )
                })

                composable<TvDestination.NewsDetailsDestination>(content = { backStackEntry ->
                    val destination: TvDestination.NewsDetailsDestination = backStackEntry.toRoute()
                    NewsDetailsTvScreen(
                            title = destination.title,
                            description = destination.description,
                            articleUrl = destination.articleUrl,
                            imageUrl = destination.imageUrl,
                            sourceName = destination.sourceName,
                            author = destination.author,
                            publishedAt = destination.publishedAt,
                            onBack = { navController.popBackStack() }
                                        )
                })
            },
           )

    BackHandler(enabled = true, onBack = {
        if (!navController.popBackStack()) {
            onAppExit()
        }
    })
}
