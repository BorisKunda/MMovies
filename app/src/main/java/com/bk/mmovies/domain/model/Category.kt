package com.bk.mmovies.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.bk.mmovies.R

sealed interface Category {
    @get:StringRes val labelRes: Int
    @get:DrawableRes val drawableRes: Int
    val categoryId: Int

    companion object {
        fun fromCategoryId(categoryId: Int): Category = when (categoryId) {
            POPULAR_MOVIE_CATEGORY_ID     -> MovieCategory.PopularMovieCategory
            NOW_PLAYING_MOVIE_CATEGORY_ID -> MovieCategory.NowPlayingMovieCategory
            UPCOMING_MOVIE_CATEGORY_ID    -> MovieCategory.UpcomingMovieCategory
            TOP_RATED_MOVIE_CATEGORY_ID   -> MovieCategory.TopRatedMovieCategory
            FAVORITES_MOVIE_CATEGORY_ID   -> MovieCategory.FavoritesMovieCategory
            POPULAR_TV_CATEGORY_ID        -> TvSeriesCategory.PopularTvSeriesCategory
            AIRING_TODAY_TV_CATEGORY_ID   -> TvSeriesCategory.AiringTodayTvSeriesCategory
            ON_TV_TV_CATEGORY_ID          -> TvSeriesCategory.OnTVTvSeriesCategory
            TOP_RATED_TV_CATEGORY_ID      -> TvSeriesCategory.TopRatedTvSeriesCategory
            FAVORITES_TV_CATEGORY_ID      -> TvSeriesCategory.FavoritesTvSeriesCategory
            UPCOMING_TV_CATEGORY_ID       -> TvSeriesCategory.UpcomingTvSeriesCategory
            else                          -> UnknownCategory
        }
    }
}

sealed class MovieCategory : Category {

    data object PopularMovieCategory : MovieCategory() {
        override val labelRes: Int = R.string.category_popular
        override val drawableRes: Int = R.drawable.ic_categories_popular
        override val categoryId: Int = POPULAR_MOVIE_CATEGORY_ID
    }

    data object NowPlayingMovieCategory : MovieCategory() {
        override val labelRes: Int = R.string.category_now_playing
        override val drawableRes: Int = R.drawable.ic_categories_now_playing
        override val categoryId: Int = NOW_PLAYING_MOVIE_CATEGORY_ID
    }

    data object UpcomingMovieCategory : MovieCategory() {
        override val labelRes: Int = R.string.category_upcoming
        override val drawableRes: Int = R.drawable.ic_categories_upcoming
        override val categoryId: Int = UPCOMING_MOVIE_CATEGORY_ID
    }

    data object TopRatedMovieCategory : MovieCategory() {
        override val labelRes: Int = R.string.category_top_rated
        override val drawableRes: Int = R.drawable.ic_categories_top_rated
        override val categoryId: Int = TOP_RATED_MOVIE_CATEGORY_ID
    }

    data object FavoritesMovieCategory : MovieCategory() {
        override val labelRes: Int = R.string.category_favorites
        override val drawableRes: Int = R.drawable.ic_star_filled
        override val categoryId: Int = FAVORITES_MOVIE_CATEGORY_ID
    }
}

sealed class TvSeriesCategory : Category {

    data object PopularTvSeriesCategory : TvSeriesCategory() {
        override val labelRes: Int = R.string.category_popular
        override val drawableRes: Int = R.drawable.ic_categories_popular
        override val categoryId: Int = POPULAR_TV_CATEGORY_ID
    }

    data object AiringTodayTvSeriesCategory : TvSeriesCategory() {
        override val labelRes: Int = R.string.category_airing_today
        override val drawableRes: Int = R.drawable.ic_today
        override val categoryId: Int = AIRING_TODAY_TV_CATEGORY_ID
    }

    data object OnTVTvSeriesCategory : TvSeriesCategory() {
        override val labelRes: Int = R.string.category_on_tv
        override val drawableRes: Int = R.drawable.ic_on_tv
        override val categoryId: Int = ON_TV_TV_CATEGORY_ID
    }

    data object TopRatedTvSeriesCategory : TvSeriesCategory() {
        override val labelRes: Int = R.string.category_top_rated
        override val drawableRes: Int = R.drawable.ic_categories_top_rated
        override val categoryId: Int = TOP_RATED_TV_CATEGORY_ID
    }

    data object FavoritesTvSeriesCategory : TvSeriesCategory() {
        override val labelRes: Int = R.string.category_favorites
        override val drawableRes: Int = R.drawable.ic_star_filled
        override val categoryId: Int = FAVORITES_TV_CATEGORY_ID
    }

    data object UpcomingTvSeriesCategory: TvSeriesCategory(){
        override val labelRes: Int = R.string.category_upcoming
        override val drawableRes: Int = R.drawable.ic_categories_upcoming
        override val categoryId: Int = UPCOMING_TV_CATEGORY_ID


    }
}

data object UnknownCategory : Category {
    override val labelRes: Int = R.string.category_unknown
    override val drawableRes: Int = android.R.drawable.ic_menu_help
    override val categoryId: Int = UNKNOWN_CATEGORY_ID
}

private const val UNKNOWN_CATEGORY_ID = -1
private const val POPULAR_MOVIE_CATEGORY_ID = 0
private const val NOW_PLAYING_MOVIE_CATEGORY_ID = 1
private const val UPCOMING_MOVIE_CATEGORY_ID = 2
private const val TOP_RATED_MOVIE_CATEGORY_ID = 3
private const val FAVORITES_MOVIE_CATEGORY_ID = 4
private const val POPULAR_TV_CATEGORY_ID = 5
private const val AIRING_TODAY_TV_CATEGORY_ID = 6
private const val ON_TV_TV_CATEGORY_ID = 7
private const val TOP_RATED_TV_CATEGORY_ID = 8
private const val FAVORITES_TV_CATEGORY_ID = 9
private const val UPCOMING_TV_CATEGORY_ID = 10
