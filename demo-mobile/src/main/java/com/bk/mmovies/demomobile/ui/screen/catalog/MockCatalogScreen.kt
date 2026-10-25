package com.bk.mmovies.demomobile.ui.screen.catalog

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bk.mmovies.demomobile.ui.screen.catalog.screencomponents.CatalogBottomTab
import com.bk.mmovies.demomobile.ui.screen.catalog.screencomponents.CatalogBottomTabBar
import com.bk.mmovies.demomobile.ui.screen.catalog.screencomponents.CatalogListView
import com.bk.mmovies.demomobile.ui.screen.catalog.screencomponents.CatalogSearchBar
import com.bk.mmovies.demomobile.ui.screen.catalog.screencomponents.CategoryListPopupView
import com.bk.mmovies.demomobile.ui.screen.catalog.screencomponents.CategorySelector
import com.bk.mmovies.demomobile.ui.screen.catalog.screencomponents.UserProfileBar
import com.bk.mmovies.demomobile.ui.screen.news.MockNewsScreen
import com.bk.mmovies.domain.model.CatalogItem
import com.bk.mmovies.ui.component.PoweredByNewsApiFooter
import com.bk.mmovies.ui.component.PoweredByTmdbFooter
import com.bk.mmovies.ui.theme.MMoviesTheme
import kotlinx.coroutines.launch

private val tmdbFooterTopPadding = 8.dp
private const val MOCK_USER_NAME = "Mock User"

@Composable
fun MockCatalogScreen(
        modifier: Modifier = Modifier,
        onNavigateToSearch: () -> Unit,
        onNavigateToMovieDetails: (movieId: Int) -> Unit,
        onNavigateToTvSeriesDetails: (seriesId: Int) -> Unit,
        viewModel: MockCatalogViewModel = viewModel()
                     ) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.messageEvent.collect { messageRes ->
            Toast.makeText(context, messageRes, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.goToMovieDetailsNavEvent.collect { movieId ->
            onNavigateToMovieDetails(movieId)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.goToTvSeriesDetailsNavEvent.collect { seriesId ->
            onNavigateToTvSeriesDetails(seriesId)
        }
    }

    MockCatalogScreenContent(
            state = state,
            onTabSelected = viewModel::onTabSelected,
            onCategoryPopupInteracted = viewModel::onCategoryPopupInteracted,
            onUserProfileClicked = viewModel::onUserProfileClicked,
            onNavigateToSearch = onNavigateToSearch,
            onCatalogItemClicked = viewModel::onCatalogItemClicked,
            onFavoriteClicked = viewModel::onFavoriteClicked,
            modifier = modifier
                            )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MockCatalogScreenContent(
        state: MockCatalogScreenState,
        onTabSelected: (CatalogBottomTab) -> Unit,
        onCategoryPopupInteracted: () -> Unit,
        onUserProfileClicked: () -> Unit,
        onNavigateToSearch: () -> Unit,
        onCatalogItemClicked: (itemId: Int) -> Unit,
        onFavoriteClicked: (CatalogItem) -> Unit,
        modifier: Modifier = Modifier
                            ) {
    var isCategoryPopupVisible by rememberSaveable { mutableStateOf(false) }
    val categorySheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    val tabStateHolder = rememberSaveableStateHolder()

    Scaffold(
            modifier = modifier,
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal
                                                                ),
            bottomBar = {
                Column {
                    CatalogBottomTabBar(
                            selectedTab = state.selectedTab,
                            onTabSelected = onTabSelected
                                       )
                    if (state.selectedTab == CatalogBottomTab.News) {
                        PoweredByNewsApiFooter(
                                modifier = Modifier
                                        .padding(top = tmdbFooterTopPadding)
                                        .alpha(0f)
                                              )
                    } else {
                        PoweredByTmdbFooter(
                                modifier = Modifier
                                        .padding(top = tmdbFooterTopPadding)
                                        .alpha(0f),
                                onClick = {}
                                           )
                    }
                }
            }
            ) { innerPadding ->
        Column(
                modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
              ) {
            UserProfileBar(
                    name = MOCK_USER_NAME,
                    imageUrl = "",
                    isGuest = false,
                    onClick = onUserProfileClicked
                          )
            CatalogSearchBar(onClick = onNavigateToSearch)
            if (state.isCategorySelectorVisible) {
                CategorySelector(
                        selectedCategory = state.selectedCategory,
                        categoryLabel = stringResource(state.categoryLabelRes),
                        onClick = { isCategoryPopupVisible = true }
                                 )
            }
            Box(
                    modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
               ) {
                tabStateHolder.SaveableStateProvider(state.selectedTab) {
                    if (state.selectedTab == CatalogBottomTab.News) {
                        MockNewsScreen()
                    } else {
                        CatalogListView(
                                catalogItems = state.catalogItems,
                                selectedCategory = state.selectedCategory,
                                onCatalogItemClicked = onCatalogItemClicked,
                                isGuest = false,
                                onFavoriteClicked = onFavoriteClicked
                                       )
                    }
                }
            }
        }
    }

    if (isCategoryPopupVisible) {
        CategoryListPopupView(
                categoryType = state.categoryType,
                onAnyInteraction = {
                    onCategoryPopupInteracted()
                    coroutineScope.launch { categorySheetState.hide() }
                            .invokeOnCompletion {
                                if (!categorySheetState.isVisible) {
                                    isCategoryPopupVisible = false
                                }
                            }
                },
                state = categorySheetState
                             )
    }
}

@Preview(showBackground = true)
@Composable
private fun MockCatalogScreenPreview() {
    MMoviesTheme {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                MockCatalogScreenContent(
                        state = MockCatalogViewModel().screenState.value,
                        onTabSelected = {},
                        onCategoryPopupInteracted = {},
                        onUserProfileClicked = {},
                        onNavigateToSearch = {},
                        onCatalogItemClicked = {},
                        onFavoriteClicked = {}
                                        )
            }
        }
    }
}
