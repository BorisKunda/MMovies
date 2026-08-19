package com.bk.mmovies.ui.screen.movies

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Tv
import androidx.compose.ui.graphics.vector.ImageVector
import com.bk.mmovies.R

enum class MoviesBottomTab(
        val labelResId: Int,
        val icon: ImageVector
                           ) {
    Movies(R.string.tab_movies, Icons.Filled.Movie),
    TvSeries(R.string.tab_tv_series, Icons.Filled.Tv)
}
