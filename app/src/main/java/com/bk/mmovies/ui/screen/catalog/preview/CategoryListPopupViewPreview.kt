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
import com.bk.mmovies.ui.theme.PhoneSizePreviews

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
@PhoneSizePreviews
@Composable
private fun CategoryListPopupViewMoviePreview() {
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