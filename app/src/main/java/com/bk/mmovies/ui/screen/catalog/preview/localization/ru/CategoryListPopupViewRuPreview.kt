package com.bk.mmovies.ui.screen.catalog.preview.localization

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.ui.screen.catalog.screencomponents.CategoryListPopupView
import com.bk.mmovies.ui.screen.catalog.screencomponents.CategoryType
import com.bk.mmovies.ui.theme.MMoviesTheme
import com.bk.mmovies.ui.theme.RussianPhoneSizePreviews

// Russian category names are the longest of the three languages
// ("Лучшие по рейтингу" against "Top Rated"), so this is where a row would
// wrap or clip first.
@OptIn(ExperimentalMaterial3Api::class)
@RussianPhoneSizePreviews
@Composable
private fun CategoryListPopupViewRuPreview() {
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