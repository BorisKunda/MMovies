package com.bk.mmovies.tv.ui.catalog

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bk.mmovies.domain.model.CatalogItem
import com.bk.mmovies.domain.model.Category
import com.bk.mmovies.domain.model.NewsItem
import com.bk.mmovies.tv.R
import com.bk.mmovies.tv.ui.catalog.content.CatalogContentList
import com.bk.mmovies.tv.ui.catalog.content.TvCatalogNavRail
import com.bk.mmovies.tv.ui.catalog.content.component.TvCatalogHeroDialog
import com.bk.mmovies.tv.ui.catalog.content.component.profile.TvProfileDialog
import com.bk.mmovies.tv.ui.catalog.content.component.profile.TvProfileHeader
import com.bk.mmovies.tv.ui.catalog.content.RailReservedSpaceSpacer
import com.bk.mmovies.tv.ui.component.TvProviderFooter
import com.bk.mmovies.ui.component.PoweredByTmdbFooter

internal const val FOCUS_REQUEST_RETRY_FRAMES = 5

@Composable
fun CatalogTvScreen(
        viewModel: TvCatalogViewModel = hiltViewModel(),
        onViewDetailsClicked: (item: CatalogItem, category: Category?) -> Unit = { _, _ -> },
        onMovieClicked: (item: CatalogItem) -> Unit = {},
        onNewsItemClicked: (newsItem: NewsItem) -> Unit = {},
        onNavigateToAuth: () -> Unit = {},
        firstItemFocusRequester: FocusRequester? = null
                   ) {
    val selectedDestination by viewModel.selectedDestination.collectAsStateWithLifecycle()
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()
    val userProfileState by viewModel.userProfileState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showProfileDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.messageEvent.collect {
            Toast.makeText(
                    context,
                    it,
                    Toast.LENGTH_SHORT
                          )
                    .show()
        }
    }

    LaunchedEffect(Unit) { viewModel.refreshFavoriteStates(reloadFavoritesTab = true) }

    val navItemFocusRequesters = remember {
        TvCatalogDestination.entries.associateWith { destination ->
            if (destination == TvCatalogDestination.Movies && firstItemFocusRequester != null) {
                firstItemFocusRequester
            } else {
                FocusRequester()
            }
        }
    }

    LaunchedEffect(Unit) {
        val target = navItemFocusRequesters[selectedDestination] ?: return@LaunchedEffect
        repeat(FOCUS_REQUEST_RETRY_FRAMES) {
            runCatching { target.requestFocus() }
            withFrameNanos {}
        }
    }

    CatalogTvScreenContent(
            selectedDestination = selectedDestination,
            screenState = screenState,
            userProfileState = userProfileState,
            canToggleFavorite = viewModel.canToggleFavorite,
            onDestinationSelected = viewModel::onDestinationSelected,
            onViewDetailsClicked = onViewDetailsClicked,
            onFeaturedFavoriteClicked = viewModel::onFeaturedFavoriteClicked,
            onMovieClicked = onMovieClicked,
            onNewsItemClicked = onNewsItemClicked,
            onProfileClicked = { showProfileDialog = true },
            onItemFocused = viewModel::onItemFocused,
            onDialogDismissed = viewModel::onDialogDismissed,
            onPopupClosedPrune = viewModel::onPopupClosedPrune,
            onSearchQueryChanged = viewModel::onSearchQueryChanged,
            onSearchItemPressed = { item ->
                viewModel.onItemFocused(item)
            },
            onRowEndReached = viewModel::onRowEndReached,
            onFavoritesRowEndReached = viewModel::onFavoritesRowEndReached,
            onNewsRowEndReached = viewModel::onNewsRowEndReached,
            onRecommendationsRetryClicked = viewModel::onRecommendationsRetryClicked,
            onOfflineRetryClicked = viewModel::onOfflineRetryClicked,
            onGeminiApiKeySaved = viewModel::onGeminiApiKeySaved,
            onNewsApiKeySaved = viewModel::onNewsApiKeySaved,
            navItemFocusRequesters = navItemFocusRequesters
                          )

    if (showProfileDialog) {
        TvProfileDialog(
                isGuest = userProfileState.isGuest,
                name = userProfileState.name,
                onDismissRequest = { showProfileDialog = false },
                onLoginClicked = {
                    showProfileDialog = false
                    onNavigateToAuth()
                },
                onLogoutClicked = {
                    showProfileDialog = false
                    viewModel.onLogoutClicked()
                }
                       )
    }
}

@Composable
fun CatalogTvScreenContent(
        selectedDestination: TvCatalogDestination,
        screenState: TvCatalogScreenState,
        userProfileState: TvUserProfileUiState = TvUserProfileUiState(),
        canToggleFavorite: Boolean = false,
        onDestinationSelected: (TvCatalogDestination) -> Unit,
        onViewDetailsClicked: (item: CatalogItem, category: Category?) -> Unit,
        onFeaturedFavoriteClicked: () -> Unit,
        onMovieClicked: (item: CatalogItem) -> Unit,
        onNewsItemClicked: (newsItem: NewsItem) -> Unit = {},
        onProfileClicked: () -> Unit = {},
        onItemFocused: (item: CatalogItem, category: Category?) -> Unit = { _, _ -> },
        onDialogDismissed: () -> Unit = {},
        onPopupClosedPrune: () -> Unit = {},
        onSearchQueryChanged: (String) -> Unit = {},
        onSearchItemPressed: (item: CatalogItem) -> Unit = {},
        onRowEndReached: (category: Category) -> Unit = {},
        onFavoritesRowEndReached: (category: Category) -> Unit = {},
        onNewsRowEndReached: () -> Unit = {},
        onRecommendationsRetryClicked: () -> Unit = {},
        onOfflineRetryClicked: () -> Unit = {},
        onGeminiApiKeySaved: (apiKey: String) -> Unit = {},
        onNewsApiKeySaved: (apiKey: String) -> Unit = {},
        navItemFocusRequesters: Map<TvCatalogDestination, FocusRequester> = emptyMap()
                          ) {
    val focusedItem = when (screenState) {
        is TvCatalogScreenState.Movies          -> screenState.focusedItem
        is TvCatalogScreenState.TvSeries        -> screenState.focusedItem
        is TvCatalogScreenState.Favorites       -> screenState.focusedItem
        is TvCatalogScreenState.Search          -> screenState.focusedItem
        is TvCatalogScreenState.Recommendations -> screenState.focusedItem
        else                                    -> null
    }
    val focusedCategory = when (screenState) {
        is TvCatalogScreenState.Movies   -> screenState.focusedCategory
        is TvCatalogScreenState.TvSeries -> screenState.focusedCategory
        else                             -> null
    }

    val profileFocusRequester = remember { FocusRequester() }
    var moviesRailItemFocused by remember { mutableStateOf(false) }
    var profileFocused by remember { mutableStateOf(false) }
    val contentListState = rememberLazyListState()
    val aboutContentFocusRequester = remember { FocusRequester() }
    val isRailFocused = remember { mutableStateOf(false) }

    LaunchedEffect(selectedDestination) { contentListState.scrollToItem(0) }
    BackToRailHandler(
            isRailFocused = isRailFocused,
            railFocusRequester = navItemFocusRequesters[selectedDestination]
                     )


    Box(
            modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(
                            start = 0.dp,
                            top = 0.dp,
                            end = 20.dp
                            )
       ) {
        /** CatalogContentListContainer */
        Row(modifier = Modifier.fillMaxSize()) {
            RailReservedSpaceSpacer(isRailFocused)

            Column(
                    modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                  ) {
                CatalogContentList(
                        selectedDestination = selectedDestination,
                        screenState = screenState,
                        listState = contentListState,
                        navItemFocusRequesters = navItemFocusRequesters,
                        aboutContentFocusRequester = aboutContentFocusRequester,
                        modifier = Modifier.weight(1f),
                        onNewsItemClicked = onNewsItemClicked,
                        onItemFocused = onItemFocused,
                        onSearchQueryChanged = onSearchQueryChanged,
                        onSearchItemPressed = onSearchItemPressed,
                        onRowEndReached = onRowEndReached,
                        onFavoritesRowEndReached = onFavoritesRowEndReached,
                        onNewsRowEndReached = onNewsRowEndReached,
                        onRecommendationsRetryClicked = onRecommendationsRetryClicked,
                        onOfflineRetryClicked = onOfflineRetryClicked,
                        onGeminiApiKeySaved = onGeminiApiKeySaved,
                        onNewsApiKeySaved = onNewsApiKeySaved
                                  )
                when (selectedDestination) {
                    TvCatalogDestination.News            -> TvProviderFooter(
                            label = stringResource(R.string.powered_by_guardian),
                            modifier = Modifier.padding(vertical = 10.dp)
                                                                            )
                    TvCatalogDestination.Recommendations -> TvProviderFooter(
                            label = stringResource(R.string.powered_by_gemini),
                            modifier = Modifier.padding(vertical = 10.dp)
                                                                            )
                    else                                 -> PoweredByTmdbFooter(
                            modifier = Modifier.padding(vertical = 10.dp)
                                                                               )
                }
            }
        }
        TvCatalogNavRail(
                selectedDestination = selectedDestination,
                onDestinationSelected = onDestinationSelected,
                navItemFocusRequesters = navItemFocusRequesters,
                profileFocusRequester = profileFocusRequester,
                isProfileFocused = profileFocused,
                onMoviesItemFocusChanged = { moviesRailItemFocused = it },
                aboutContentFocusRequester = aboutContentFocusRequester,
                isFocusedState = isRailFocused,
                onFocusChanged = { isRailFocused.value = it })
        TvProfileHeader(
                name = userProfileState.name,
                imageUrl = userProfileState.imageUrl,
                isGuest = userProfileState.isGuest,
                focusRequester = profileFocusRequester,
                canBeFocused = moviesRailItemFocused || profileFocused,
                onFocusChanged = { profileFocused = it },
                onNavigateBack = {
                    navItemFocusRequesters[TvCatalogDestination.Movies]?.let { runCatching { it.requestFocus() } }
                },
                onClick = onProfileClicked
                       )
    }

    if (focusedItem != null) {
        TvCatalogHeroDialog(
                item = focusedItem,
                focusedCategory = focusedCategory,
                pruneOnClose = when (screenState) {
                    is TvCatalogScreenState.Recommendations -> screenState.items.any { it.isFavorite }
                    is TvCatalogScreenState.Favorites       -> screenState.rows.any { row -> row.items.any { !it.isFavorite } }
                    else                                    -> false
                },
                showFavoriteButton = canToggleFavorite,
                railFocusRequester = navItemFocusRequesters[selectedDestination],
                onViewDetailsClicked = onViewDetailsClicked,
                onFavoriteClicked = onFeaturedFavoriteClicked,
                onDismissed = onDialogDismissed,
                onPopupClosedPrune = onPopupClosedPrune
                           )
    }
}

@Composable
private fun BackToRailHandler(
        isRailFocused: State<Boolean>,
        railFocusRequester: FocusRequester?
                             ) {
    val railFocused by isRailFocused
    BackHandler(enabled = !railFocused && railFocusRequester != null) {
        railFocusRequester?.let { runCatching { it.requestFocus() } }
    }
}

