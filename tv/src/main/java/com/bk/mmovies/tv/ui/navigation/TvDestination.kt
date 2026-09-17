package com.bk.mmovies.tv.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface TvDestination {
    @Serializable
    object SplashDestination : TvDestination

    @Serializable
    object AuthDestination : TvDestination

    @Serializable
    object MoviesDestination : TvDestination

    @Serializable
    data class MovieDetailsDestination(
            val movieId: Int,
            val categoryId: Int
                                      ) : TvDestination

    @Serializable
    data class TvSeriesDetailsDestination(
            val seriesId: Int
                                         ) : TvDestination

    @Serializable
    data class WebViewDestination(
            val url: String
                                  ) : TvDestination

    @Serializable
    data class NewsDetailsDestination(
            val title: String,
            val description: String,
            val articleUrl: String,
            val imageUrl: String,
            val sourceName: String,
            val author: String,
            val publishedAt: String
                                      ) : TvDestination
}
