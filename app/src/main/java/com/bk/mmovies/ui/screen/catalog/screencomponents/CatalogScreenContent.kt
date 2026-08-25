package com.bk.mmovies.ui.screen.catalog.screencomponents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.bk.mmovies.R
import com.bk.mmovies.domain.model.CatalogItem
import com.bk.mmovies.domain.model.Category
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.domain.model.TvSeriesCategory
import com.bk.mmovies.ui.component.EmptyStateView
import com.bk.mmovies.ui.component.GenericErrorScreen
import com.bk.mmovies.ui.screen.catalog.CatalogScreenState

@Composable
fun CatalogScreenContent(
        state: CatalogScreenState,
        selectedCategory: Category,
        isGuest: Boolean,
        onCatalogItemClicked: (id: Int) -> Unit,
        onRetry: () -> Unit,
        onFavoriteClicked: (catalogItem: CatalogItem) -> Unit = {},
        onLoadNextPage: () -> Unit = {}
                         ) {
    Column(modifier = Modifier.fillMaxSize()) {
        when (state) {
            is CatalogScreenState.Loading -> {
                MovieRowLoadingPlaceholderList()
            }

            is CatalogScreenState.Content -> {
                CatalogListView(
                        catalogItems = state.catalogItems,
                        selectedCategory = selectedCategory,
                        isGuest = isGuest,
                        onCatalogItemClicked = onCatalogItemClicked,
                        onFavoriteClicked = onFavoriteClicked,
                        isLoadingNextPage = state.isLoadingNextPage,
                        onLoadNextPage = onLoadNextPage
                               )
            }

            is CatalogScreenState.Empty -> {
                val isFavorites = selectedCategory == MovieCategory.FavoritesMovieCategory ||
                        selectedCategory == TvSeriesCategory.FavoritesTvSeriesCategory
                val isTv = selectedCategory is TvSeriesCategory
                EmptyStateView(
                        imageResId = if (isFavorites) R.drawable.ic_star_filled_large
                        else R.drawable.ic_popcorn_bucket,
                        title = stringResource(
                                when {
                                    isFavorites -> R.string.empty_favorites_title
                                    isTv        -> R.string.empty_tv_series_title
                                    else        -> R.string.empty_movies_title
                                }
                                              ),
                        message = stringResource(
                                when {
                                    isFavorites -> R.string.empty_favorites_message
                                    isTv        -> R.string.empty_tv_series_message
                                    else        -> R.string.empty_movies_message
                                }
                                                )
                              )
            }

            is CatalogScreenState.FavoritesLoginRequired -> {
                EmptyStateView(
                        imageResId = R.drawable.ic_star_filled_large,
                        title = stringResource(R.string.empty_favorites_guest_title),
                        message = stringResource(R.string.empty_favorites_guest_message)
                              )
            }

            is CatalogScreenState.Error -> {
                GenericErrorScreen(
                        state.errorMessage,
                        onTryAgainClicked = onRetry
                                  )
            }
        }
    }
}
