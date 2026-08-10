package com.bk.mmovies.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.bk.mmovies.R

sealed class MovieCategory(
        @get:StringRes val labelRes: Int,
        @get:DrawableRes val drawableRes: Int,
        val categoryId: Int
                          ) {
    object PopularMovieCategory : MovieCategory(
            R.string.category_popular,
            R.drawable.ic_categories_popular,
            POPULAR_CATEGORY_ID
                                               )

    object UpcomingMovieCategory : MovieCategory(
            R.string.category_upcoming,
            R.drawable.ic_categories_upcoming,
            UPCOMING_CATEGORY_ID
                                                )

    object NowPlayingMovieCategory : MovieCategory(
            R.string.category_now_playing,
            R.drawable.ic_categories_now_playing,
            NOW_PLAYING_CATEGORY_ID
                                                  )

    object TopRatedMovieCategory : MovieCategory(
            R.string.category_top_rated,
            R.drawable.ic_categories_top_rated,
            TOP_RATED_CATEGORY_ID
                                                )

    object FavoritesMovieCategory : MovieCategory(
            R.string.category_favorites,
            R.drawable.ic_star_filled,
            FAVORITES_CATEGORY_ID
                                                 )

    companion object {
        // Navigation routes can only carry primitives, so the category tags
        // along as its categoryId and gets resolved back here on the other side.
        fun fromCategoryId(categoryId: Int): MovieCategory = when (categoryId) {
            POPULAR_CATEGORY_ID -> PopularMovieCategory
            UPCOMING_CATEGORY_ID -> UpcomingMovieCategory
            NOW_PLAYING_CATEGORY_ID -> NowPlayingMovieCategory
            TOP_RATED_CATEGORY_ID -> TopRatedMovieCategory
            FAVORITES_CATEGORY_ID -> FavoritesMovieCategory
            else -> PopularMovieCategory
        }
    }
}

private const val FAVORITES_CATEGORY_ID = 0
private const val POPULAR_CATEGORY_ID = 1
private const val UPCOMING_CATEGORY_ID = 2
private const val NOW_PLAYING_CATEGORY_ID = 3
private const val TOP_RATED_CATEGORY_ID = 4
