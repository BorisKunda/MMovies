package com.bk.mmovies.ui.screen.catalog.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.ui.screen.catalog.screencomponents.CategorySelector
import com.bk.mmovies.ui.theme.MMoviesTheme
import com.bk.mmovies.ui.theme.PhoneSizePreviews

@PhoneSizePreviews
@Composable
private fun CategorySelectorPreview() {
    CategorySelectorPreviewFrame(MovieCategory.PopularMovieCategory)
}

@Preview(showBackground = true, name = "Top Rated")
@Composable
private fun CategorySelectorTopRatedPreview() {
    CategorySelectorPreviewFrame(MovieCategory.TopRatedMovieCategory)
}

@Composable
private fun CategorySelectorPreviewFrame(selectedCategory: MovieCategory) {
    MMoviesTheme {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                CategorySelector(selectedCategory = selectedCategory, onClick = {})
            }
        }
    }
}
