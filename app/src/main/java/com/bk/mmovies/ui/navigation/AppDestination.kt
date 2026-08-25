package com.bk.mmovies.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface AppDestination {
    @Serializable
    object SplashDestination : AppDestination

    @Serializable
    object AuthDestination : AppDestination

    @Serializable
    object MoviesDestination : AppDestination

    @Serializable
    data class MovieDetailsDestination(
            val movieId: Int,
            val categoryId: Int
                                      ) : AppDestination

    // No categoryId: unlike MovieDetailsScreen (which varies its TBA labels by
    // category), the TV details screen never reads it — carrying it would only
    // split the same series into distinct back-stack entries per category.
    @Serializable
    data class TvSeriesDetailsDestination(
            val seriesId: Int
                                         ) : AppDestination

    @Serializable
    data class SeasonDetailsDestination(
            val seriesId: Int,
            val seasonNumber: Int
                                       ) : AppDestination
}

