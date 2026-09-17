package com.bk.mmovies.tv.ui.catalog.preview.localization.he

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.tv.ui.catalog.CatalogTvScreenContent
import com.bk.mmovies.tv.ui.catalog.TvCatalogDestination
import com.bk.mmovies.tv.ui.catalog.TvCatalogScreenState
import com.bk.mmovies.tv.ui.catalog.TvCategoryRowContent
import com.bk.mmovies.ui.theme.MMoviesTheme

@Preview(device = "id:tv_1080p", locale = "iw", name = "Catalog - Hebrew (RTL)")
@Composable
private fun CatalogTvScreenHePreview() {
    MMoviesTheme {
        Scaffold(
                containerColor = MaterialTheme.colorScheme.background
                ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                CatalogTvScreenContent(
                        selectedDestination = TvCatalogDestination.Movies,
                        screenState = TvCatalogScreenState.Movies(
                                listOf(
                                        TvCategoryRowContent(
                                                MovieCategory.PopularMovieCategory,
                                                emptyList()
                                                             )
                                      )
                                                                  ),
                        onDestinationSelected = {},
                        onViewDetailsClicked = { _, _ -> },
                        onFeaturedFavoriteClicked = {},
                        onMovieClicked = {}
                                       )
            }
        }
    }
}
