package com.bk.mmovies.ui.screen.catalog

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
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
import com.bk.mmovies.connectivity.openDeviceInternetSettings
import com.bk.mmovies.domain.model.Category
import com.bk.mmovies.ui.component.PoweredByTmdbFooter
import com.bk.mmovies.ui.screen.catalog.screencomponents.CatalogBottomTab
import com.bk.mmovies.ui.screen.catalog.screencomponents.CatalogBottomTabBar
import com.bk.mmovies.ui.screen.catalog.screencomponents.CatalogScreenContent
import com.bk.mmovies.ui.screen.catalog.screencomponents.CatalogSearchBar
import com.bk.mmovies.ui.screen.catalog.screencomponents.CategoryListPopupView
import com.bk.mmovies.ui.screen.catalog.screencomponents.CategorySelector
import com.bk.mmovies.ui.screen.catalog.screencomponents.CategoryType
import com.bk.mmovies.ui.screen.catalog.screencomponents.UserProfileBar
import kotlinx.coroutines.launch

// Zero: CategorySelector already carries its own 12dp bottom margin
// (categorySelectorVerticalMargin), which is exactly the gap between the
// search capsule and CategorySelector above it — adding more here would
// make the two gaps uneven.
private val tabContentTopSpacing = 0.dp

// Gap between the nav bar and the footer above it; the footer's own bottom
// margin is fixed inside PoweredByTmdbFooter.
private val tmdbFooterTopPadding = 8.dp


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
            // MainActivity's own outer Scaffold already pads its content by
            // the full safe-drawing insets (see AppNavigation's
            // Modifier.padding(padding)) — this Scaffold applying the
            // default insets again on top of that doubled the gap above the
            // status bar. Only the bottom (nav bar/gesture inset) still
            // needs to be reserved here, for the bottom tab bar/footer.
            contentWindowInsets = WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal
                                                                ),
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
            // The user profile chip sits at the very top-right of the screen,
            // above the search bar.
            UserProfileBar(
                    userProfileState = userProfileState,
                    onLogout = { catalogViewModel.onLogoutClicked() }
                          )

            CatalogSearchBar(onClick = onNavigateToSearch)

            // Fixed above the tab content so switching tabs never resets the
            // selected category.
            CategorySelector(
                    selectedCategory = selectedCategory,
                    onClick = { isCategoryPopupVisible = true }
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
                            onOpenNetworkSettings = { openDeviceInternetSettings(context) },
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
        // Toggling favorite from the Details screen doesn't update this
        // screen's in-memory list, so re-sync stars against the local cache
        // whenever we're navigated back to (e.g. from Details).
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                catalogViewModel.refreshFavoriteMarkers()
                catalogViewModel.retryIfOffline()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
