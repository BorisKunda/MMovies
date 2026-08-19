package com.bk.mmovies.ui.screen.movies.screencomponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bk.mmovies.R
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.domain.model.MovieModel
import com.bk.mmovies.ui.component.EmptyStateView
import com.bk.mmovies.ui.component.GenericErrorScreen
import com.bk.mmovies.ui.screen.movies.MoviesScreenState
import com.bk.mmovies.ui.screen.movies.UserProfileUiState
import kotlinx.coroutines.launch

// Mirrors CategorySelector's own start padding so UserSelector sits as far
// from the end edge as CategorySelector sits from the start edge.
private val userSelectorEndPadding = 28.dp

// Ensures the gap below the selector never shrinks below the normal card-to-card gap
// once the list is scrolled past its own top content padding.
private val categorySelectorListSpacing = 12.dp

private val categorySelectorTopMargin = 12.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesScreenContent(
        state: MoviesScreenState,
        selectedCategory: MovieCategory,
        userProfileState: UserProfileUiState,
        onMovieClicked: (id: Int) -> Unit,
        onCategorySelected: (category: MovieCategory) -> Unit,
        onRetry: () -> Unit,
        onLogout: () -> Unit,
        onFavoriteClicked: (movie: MovieModel) -> Unit = {}
                        ) {
    var isCategoryPopupVisible by remember { mutableStateOf(false) }
    val categorySheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    Column(
            modifier = Modifier
                    .fillMaxSize()
                    .padding(top = categorySelectorTopMargin),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
          ) {
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                // Both chips apply the same top/bottom margin around their own
                // content, so centering their bounding boxes centers their content.
                verticalAlignment = Alignment.CenterVertically
           ) {
            CategorySelector(
                    selectedCategory = selectedCategory,
                    onClick = { isCategoryPopupVisible = true }
                             )

            UserSelector(
                    name = userProfileState.name,
                    imageUrl = userProfileState.imageUrl,
                    isGuest = userProfileState.isGuest,
                    onLogout = onLogout,
                    modifier = Modifier.padding(
                            end = userSelectorEndPadding,
                            top = categorySelectorVerticalMargin,
                            bottom = categorySelectorVerticalMargin
                                                )
                        )
        }

        Spacer(modifier = Modifier.height(categorySelectorListSpacing))

        when (state) {
            is MoviesScreenState.Loading -> {
                MovieRowLoadingPlaceholderList()
            }

            is MoviesScreenState.Content -> {
                MoviesListView(
                        movies = state.movies,
                        selectedCategory = selectedCategory,
                        isGuest = userProfileState.isGuest,
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

    if (isCategoryPopupVisible) {
        CategoryListPopupView(
                lastSelectedCategory = selectedCategory,
                onNewCategorySelected = { newCategory ->
                    onCategorySelected(newCategory)
                    coroutineScope.launch { categorySheetState.hide() }
                            .invokeOnCompletion {
                                if (!categorySheetState.isVisible) {
                                    isCategoryPopupVisible = false
                                }
                            }
                },
                onDismiss = { isCategoryPopupVisible = false },
                state = categorySheetState
                              )
    }
}
