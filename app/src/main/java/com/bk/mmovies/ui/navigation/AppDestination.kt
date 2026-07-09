package com.bk.mmovies.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface AppDestination {
    @Serializable
    object SplashDestination : AppDestination

    @Serializable
    object MoviesDestination : AppDestination

    @Serializable
    data class MovieDetailsDestination(
        val movieId: Int
    ) : AppDestination
}

