package com.bk.mmovies.ui.screen.catalog.preview

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.domain.model.TvSeriesCategory
import com.bk.mmovies.ui.screen.catalog.screencomponents.CategoryListPopupView
import com.bk.mmovies.ui.screen.catalog.screencomponents.CategoryType
import com.bk.mmovies.ui.theme.MMoviesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun CategoryListPopupViewTvPreview() {
    MMoviesTheme {
        CategoryListPopupView(
                categoryType = CategoryType.TV,
                lastSelectedCategory = TvSeriesCategory.PopularTvSeriesCategory,
                onNewCategorySelected = {},
                onDismiss = {},
                state = rememberModalBottomSheetState()
                             )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun CategoryListPopupViewPreview() {
    MMoviesTheme {
        CategoryListPopupView(
                categoryType = CategoryType.MOVIE,
                lastSelectedCategory = MovieCategory.PopularMovieCategory,
                onNewCategorySelected = {},
                onDismiss = {},
                state = rememberModalBottomSheetState()
                             )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
        locale = "iw",
        name = "Hebrew (RTL)"
        )
@Composable
private fun CategoryListPopupViewPreviewHebrew() {
    MMoviesTheme {
        CategoryListPopupView(
                categoryType = CategoryType.MOVIE,
                lastSelectedCategory = MovieCategory.TopRatedMovieCategory,
                onNewCategorySelected = {},
                onDismiss = {},
                state = rememberModalBottomSheetState()
                             )
    }
}

// Russian category names are the longest of the three languages
// ("Лучшие по рейтингу" against "Top Rated"), so this is where a row would
// wrap or clip first.
@OptIn(ExperimentalMaterial3Api::class)
@Preview(
        locale = "ru",
        name = "Russian (long text)"
        )
@Composable
private fun CategoryListPopupViewPreviewRussian() {
    MMoviesTheme {
        CategoryListPopupView(
                categoryType = CategoryType.MOVIE,
                lastSelectedCategory = MovieCategory.TopRatedMovieCategory,
                onNewCategorySelected = {},
                onDismiss = {},
                state = rememberModalBottomSheetState()
                             )
    }
}