package com.bk.mmovies.ui.screen.movies

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.ui.screen.movies.screencomponents.MoviesScreenContent
import com.bk.mmovies.util.logDebug

private const val TAG = "MoviesScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesScreen(
        onNavigateToMovieDetailsScreen: (id: Int, category: MovieCategory) -> Unit,
        onNavigateToAuthScreen: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val moviesViewModel = hiltViewModel<MoviesViewModel>()
    val state by moviesViewModel.moviesScreenState.collectAsStateWithLifecycle()
    val selectedCategory by moviesViewModel.selectedCategory.collectAsStateWithLifecycle()
    val userProfileState by moviesViewModel.userProfileState.collectAsStateWithLifecycle()

    MoviesScreenContent(
            state = state,
            selectedCategory = selectedCategory,
            userProfileState = userProfileState,
            onMovieClicked = { id -> moviesViewModel.handleMovieClick(id) },
            onCategorySelected = { category -> moviesViewModel.handleCategorySelected(category) },
            onRetry = { moviesViewModel.retry() },
            onLogout = { moviesViewModel.onLogoutClicked() },
            onFavoriteClicked = { movie -> moviesViewModel.onFavoriteClicked(movie) }
                        )

    LaunchedEffect(Unit) {
        moviesViewModel.goToMovieDetailsNavEvent.collect { (movieId, category) ->
            onNavigateToMovieDetailsScreen(movieId, category)
        }
    }
    LaunchedEffect(Unit) {
        moviesViewModel.goToAuthNavEvent.collect {
            onNavigateToAuthScreen()
        }
    }
    DisposableEffect(lifecycleOwner) {
        // Composed/Disposed rather than a log on every recomposition, which is
        // what a bare logDebug in the composable body produced.
        logDebug(
                TAG,
                "Composed"
                )
        onDispose {
            logDebug(
                    TAG,
                    "Disposed"
                    )
        }
    }

    DisposableEffect(lifecycleOwner) {
        // Toggling favorite from the Details screen doesn't update this
        // screen's in-memory list, so re-sync stars against the local cache
        // whenever we're navigated back to (e.g. from Details).
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                moviesViewModel.refreshFavoriteMarkers()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
