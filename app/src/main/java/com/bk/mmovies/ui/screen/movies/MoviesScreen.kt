package com.bk.mmovies.ui.screen.movies

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.ui.component.PoweredByTmdbFooter
import com.bk.mmovies.ui.screen.movies.screencomponents.CategoryListPopupView
import com.bk.mmovies.ui.screen.movies.screencomponents.MoviesBottomBar
import com.bk.mmovies.ui.screen.movies.screencomponents.MoviesHeaderBar
import com.bk.mmovies.ui.screen.movies.screencomponents.MoviesScreenContent
import com.bk.mmovies.ui.screen.tvseries.TvSeriesScreen
import com.bk.mmovies.util.logDebug
import kotlinx.coroutines.launch

// Gap between the fixed header bar and the tab content below it.
private val tabContentTopSpacing = 12.dp

// Gap between the nav bar and the footer above it; the footer's own bottom
// margin is fixed inside PoweredByTmdbFooter.
private val tmdbFooterTopPadding = 8.dp

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

    var selectedTab by rememberSaveable { mutableStateOf(MoviesBottomTab.Movies) }
    // Keeps each tab's own remembered/rememberSaveable state (e.g. list scroll
    // position) alive while the other tab is composed, instead of it being
    // torn down and recreated on every switch.
    val tabStateHolder = rememberSaveableStateHolder()

    var isCategoryPopupVisible by remember { mutableStateOf(false) }
    val categorySheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                Column {
                    MoviesBottomBar(
                            selectedTab = selectedTab,
                            onTabSelected = { selectedTab = it }
                                   )
                    PoweredByTmdbFooter(
                            modifier = Modifier.padding(top = tmdbFooterTopPadding)
                                       )
                }
            }
            ) { innerPadding ->
        Column(
                modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
              ) {
            // Fixed above the tab content so switching tabs never resets the
            // selected category or logs out the user's selection state.
            MoviesHeaderBar(
                    selectedCategory = selectedCategory,
                    userProfileState = userProfileState,
                    onCategoryClick = { isCategoryPopupVisible = true },
                    onLogout = { moviesViewModel.onLogoutClicked() }
                           )

            Box(
                    modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(top = tabContentTopSpacing)
               ) {
                tabStateHolder.SaveableStateProvider(selectedTab) {
                    when (selectedTab) {
                        MoviesBottomTab.Movies -> {
                            MoviesScreenContent(
                                    state = state,
                                    selectedCategory = selectedCategory,
                                    isGuest = userProfileState.isGuest,
                                    onMovieClicked = { id -> moviesViewModel.handleMovieClick(id) },
                                    onRetry = { moviesViewModel.retry() },
                                    onFavoriteClicked = { movie -> moviesViewModel.onFavoriteClicked(movie) }
                                                )
                        }

                        MoviesBottomTab.TvSeries -> TvSeriesScreen()
                    }
                }
            }
        }
    }

    if (isCategoryPopupVisible) {
        CategoryListPopupView(
                lastSelectedCategory = selectedCategory,
                onNewCategorySelected = { newCategory ->
                    moviesViewModel.handleCategorySelected(newCategory)
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
