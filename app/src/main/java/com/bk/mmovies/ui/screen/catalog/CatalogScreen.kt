package com.bk.mmovies.ui.screen.catalog

import android.widget.Toast
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
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bk.mmovies.domain.model.Category
import com.bk.mmovies.ui.component.PoweredByTmdbFooter
import com.bk.mmovies.ui.screen.catalog.screencomponents.CatalogBottomTab
import com.bk.mmovies.ui.screen.catalog.screencomponents.CatalogBottomTabBar
import com.bk.mmovies.ui.screen.catalog.screencomponents.CatalogHeaderBar
import com.bk.mmovies.ui.screen.catalog.screencomponents.CatalogScreenContent
import com.bk.mmovies.ui.screen.catalog.screencomponents.CatalogSearchBar
import com.bk.mmovies.ui.screen.catalog.screencomponents.CategoryListPopupView
import com.bk.mmovies.ui.screen.catalog.screencomponents.CategoryType
import com.bk.mmovies.util.logDebug
import kotlinx.coroutines.launch

// Gap between the fixed header bar and the tab content below it.
private val tabContentTopSpacing = 6.dp

// Gap between the nav bar and the footer above it; the footer's own bottom
// margin is fixed inside PoweredByTmdbFooter.
private val tmdbFooterTopPadding = 8.dp

private const val TAG = "CatalogScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
        onNavigateToDetailsScreen: (id: Int, category: Category) -> Unit,
        onNavigateToAuthScreen: () -> Unit,
        onNavigateToSearch: () -> Unit,
        onNavigateToTerms: () -> Unit = {}) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val catalogViewModel = hiltViewModel<CatalogViewModel>()
    val state by catalogViewModel.catalogScreenState.collectAsStateWithLifecycle()
    val selectedTab by catalogViewModel.selectedTab.collectAsStateWithLifecycle()
    val selectedCategory by catalogViewModel.selectedCategory.collectAsStateWithLifecycle()
    val userProfileState by catalogViewModel.userProfileState.collectAsStateWithLifecycle()

    // Keeps each tab's own remembered/rememberSaveable state (e.g. list scroll
    // position) alive while the other tab is composed, instead of it being
    // torn down and recreated on every switch.
    val tabStateHolder: SaveableStateHolder = rememberSaveableStateHolder()

    var isCategoryPopupVisible by remember { mutableStateOf(false) }
    val categorySheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                Column {
                    CatalogBottomTabBar(
                            selectedTab = selectedTab,
                            onTabSelected = { catalogViewModel.onTabSelected(it) }
                                       )
                    PoweredByTmdbFooter(
                            modifier = Modifier.padding(top = tmdbFooterTopPadding),
                            onClick = onNavigateToTerms
                                       )
                }
            }
            ) { innerPadding ->
        Column(
                modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
              ) {
            CatalogSearchBar(onClick = onNavigateToSearch)

            // Fixed above the tab content so switching tabs never resets the
            // selected category or logs out the user's selection state.
            CatalogHeaderBar(
                    selectedCategory = selectedCategory,
                    userProfileState = userProfileState,
                    onCategoryClick = { isCategoryPopupVisible = true },
                    onLogout = { catalogViewModel.onLogoutClicked() }
                            )

            Box(
                    modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(top = tabContentTopSpacing)
               ) {
                tabStateHolder.SaveableStateProvider(selectedTab) {
                    CatalogScreenContent(
                            state = state,
                            selectedCategory = selectedCategory,
                            isGuest = userProfileState.isGuest,
                            onCatalogItemClicked = { id -> catalogViewModel.handleCatalogItemClicked(id) },
                            onRetry = { catalogViewModel.retry() },
                            onFavoriteClicked = { item -> catalogViewModel.onFavoriteClicked(item) },
                            onLoadNextPage = { catalogViewModel.loadNextPage() },
                            getTvSeriesAirDateLabel = { seriesId ->
                                catalogViewModel.getTvSeriesAirDateLabel(seriesId)
                            }
                                         )
                }
            }
        }
    }

    if (isCategoryPopupVisible) {
        CategoryListPopupView(
                categoryType = if (selectedTab == CatalogBottomTab.Movies) CategoryType.MOVIE else CategoryType.TV,
                lastSelectedCategory = selectedCategory,
                onNewCategorySelected = { newCategory ->
                    catalogViewModel.handleCategorySelected(newCategory)
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
        catalogViewModel.goToDetailsNavEvent.collect { (itemId, category) ->
            onNavigateToDetailsScreen(itemId, category)
        }
    }
    LaunchedEffect(Unit) {
        catalogViewModel.goToAuthNavEvent.collect {
            onNavigateToAuthScreen()
        }
    }
    LaunchedEffect(Unit) {
        catalogViewModel.messageEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
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
                catalogViewModel.refreshFavoriteMarkers()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
