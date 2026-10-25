package com.bk.mmovies.demotv.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.bk.mmovies.demotv.ui.catalog.CatalogTvScreen
import com.bk.mmovies.demotv.ui.details.moviedetails.MovieDetailsTvScreen
import com.bk.mmovies.demotv.ui.details.tvseriesdetails.TvSeriesDetailsTvScreen
import com.bk.mmovies.demotv.ui.news.NewsDetailsTvScreen
import com.bk.mmovies.demotv.ui.splash.SplashScreenTv
import com.bk.mmovies.domain.model.CatalogItem
import com.bk.mmovies.domain.model.CatalogMediaType
import com.bk.mmovies.domain.model.Category
import com.bk.mmovies.domain.model.MovieCategory

@Composable
fun TvNavigation(
        navController: NavHostController,
        modifier: Modifier = Modifier,
        firstItemFocusRequester: FocusRequester? = null,
        onAppExit: () -> Unit = {}
                 ) {
    val navigateToDetails: (CatalogItem, Category?) -> Unit = { item, category ->
        when (item.mediaType) {
            CatalogMediaType.MOVIE     -> {
                val categoryId = (category as? MovieCategory ?: MovieCategory.PopularMovieCategory).categoryId
                navController.navigate(TvDestination.MovieDetailsDestination(item.id, categoryId))
            }
            CatalogMediaType.TV_SERIES -> {
                navController.navigate(TvDestination.TvSeriesDetailsDestination(item.id))
            }
        }
    }

    NavHost(
            navController,
            TvDestination.SplashDestination,
            modifier,
            builder = {
                composable<TvDestination.SplashDestination>(content = {
                    SplashScreenTv(
                            onNavigateToMoviesScreen = {
                                navController.navigate(TvDestination.MoviesDestination) {
                                    popUpTo<TvDestination.SplashDestination> { inclusive = true }
                                }
                            }
                                  )
                })

                composable<TvDestination.MoviesDestination>(content = {
                    CatalogTvScreen(
                            onViewDetailsClicked = navigateToDetails,
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
