package com.bk.mmovies.ui.screen.movies.screencomponents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.bk.mmovies.R
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.domain.model.MovieModel
import com.bk.mmovies.ui.component.EmptyStateView
import com.bk.mmovies.ui.component.GenericErrorScreen
import com.bk.mmovies.ui.screen.movies.MoviesScreenState

@Composable
fun MoviesScreenContent(
        state: MoviesScreenState,
        selectedCategory: MovieCategory,
        isGuest: Boolean,
        onMovieClicked: (id: Int) -> Unit,
        onRetry: () -> Unit,
        onFavoriteClicked: (movie: MovieModel) -> Unit = {}
                        ) {
    Column(modifier = Modifier.fillMaxSize()) {
        when (state) {
            is MoviesScreenState.Loading -> {
                MovieRowLoadingPlaceholderList()
            }

            is MoviesScreenState.Content -> {
                MoviesListView(
                        movies = state.movies,
                        selectedCategory = selectedCategory,
                        isGuest = isGuest,
                        onMovieClicked = onMovieClicked,
                        onFavoriteClicked = onFavoriteClicked
                              )
            }

            is MoviesScreenState.Empty -> {
                val isFavorites = selectedCategory == MovieCategory.FavoritesMovieCategory
                EmptyStateView(
                        imageResId = if (isFavorites) R.drawable.ic_star_filled_large
                        else R.drawable.ic_popcorn_bucket,
                        title = stringResource(
                                if (isFavorites) R.string.empty_favorites_title
                                else R.string.empty_movies_title
                                              ),
                        message = stringResource(
                                if (isFavorites) R.string.empty_favorites_message
                                else R.string.empty_movies_message
                                                )
                              )
            }

            is MoviesScreenState.FavoritesLoginRequired -> {
                EmptyStateView(
                        imageResId = R.drawable.ic_star_filled_large,
                        title = stringResource(R.string.empty_favorites_guest_title),
                        message = stringResource(R.string.empty_favorites_guest_message)
                              )
            }

            is MoviesScreenState.Error -> {
                GenericErrorScreen(
                        state.errorMessage,
                        onTryAgainClicked = onRetry
                                  )
            }
        }
    }
}
