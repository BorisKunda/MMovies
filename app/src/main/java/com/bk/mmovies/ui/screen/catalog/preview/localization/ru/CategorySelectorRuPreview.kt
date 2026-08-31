package com.bk.mmovies.ui.screen.catalog.preview.localization

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.ui.screen.catalog.screencomponents.CategorySelector
import com.bk.mmovies.ui.theme.MMoviesTheme
import com.bk.mmovies.ui.theme.RussianPhoneSizePreviews

// Top Rated is the longest of the three languages' category labels
// ("Лучшие по рейтингу" against "Top Rated"), so this is where the chip
// would wrap or clip first.
@RussianPhoneSizePreviews
@Composable
private fun CategorySelectorPreviewRussian() {
    MMoviesTheme {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                CategorySelector(
                        selectedCategory = MovieCategory.TopRatedMovieCategory,
                        onClick = {}
                                 )
            }
        }
    }
}
