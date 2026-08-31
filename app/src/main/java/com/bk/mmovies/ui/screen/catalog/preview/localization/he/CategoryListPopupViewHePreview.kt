package com.bk.mmovies.ui.screen.catalog.preview.localization

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.ui.screen.catalog.screencomponents.CategoryListPopupView
import com.bk.mmovies.ui.screen.catalog.screencomponents.CategoryType
import com.bk.mmovies.ui.theme.HebrewPhoneSizePreviews
import com.bk.mmovies.ui.theme.MMoviesTheme

@OptIn(ExperimentalMaterial3Api::class)
@HebrewPhoneSizePreviews
@Composable
private fun CategoryListPopupViewHePreview() {
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