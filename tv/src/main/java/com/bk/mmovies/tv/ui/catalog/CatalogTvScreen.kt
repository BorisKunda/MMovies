package com.bk.mmovies.tv.ui.catalog

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bk.mmovies.domain.model.CatalogItem
import com.bk.mmovies.domain.model.Category
import com.bk.mmovies.domain.model.NewsItem
import com.bk.mmovies.domain.model.TvSeriesCategory
import com.bk.mmovies.tv.R
import com.bk.mmovies.tv.ui.catalog.content.CatalogNavRail
import com.bk.mmovies.tv.ui.catalog.content.RailReservedSpaceSpacer
import com.bk.mmovies.tv.ui.catalog.content.TvCatalogHeroDialog
import com.bk.mmovies.tv.ui.catalog.content.TvProfileDialog
import com.bk.mmovies.tv.ui.catalog.content.TvProfileHeader
import com.bk.mmovies.tv.ui.catalog.content.TvSearchBar
import com.bk.mmovies.tv.ui.catalog.content.TvSearchResultsRow
import com.bk.mmovies.tv.ui.catalog.content.TvRecommendationsContent
import com.bk.mmovies.tv.ui.catalog.content.NewsKeySetup
import com.bk.mmovies.tv.ui.catalog.content.TvNewsRow
import com.bk.mmovies.tv.ui.catalog.content.TvCatalogRow
import com.bk.mmovies.tv.ui.catalog.content.rows.TvRowShimmer
import com.bk.mmovies.tv.ui.component.TvOfflineView
import com.bk.mmovies.tv.ui.component.TvProviderFooter
import com.bk.mmovies.tv.ui.legal.TvAboutContent
import com.bk.mmovies.ui.component.PoweredByTmdbFooter
import kotlinx.coroutines.launch

private const val SCROLL_STEP_PX = 400f
private const val SHIMMER_ROW_PLACEHOLDER_COUNT = 2
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
    val coroutineScope = rememberCoroutineScope()
    val aboutContentFocusRequester = remember { FocusRequester() }
    val isRailFocused = remember { mutableStateOf(false) }

    Box(
            modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
       ) {
        Row(modifier = Modifier.fillMaxSize()) {
            RailReservedSpaceSpacer(isRailFocused)

            Column(
                    modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                  ) {
                TvProfileHeader(
                        name = userProfileState.name,
                        imageUrl = userProfileState.imageUrl,
                        isGuest = userProfileState.isGuest,
                        modifier = Modifier.padding(
                                start = 40.dp,
                                top = 24.dp,
                                end = 40.dp
                                                   ),
                        focusRequester = profileFocusRequester,
                        canBeFocused = moviesRailItemFocused || profileFocused,
                        onFocusChanged = { profileFocused = it },
                        onNavigateBack = {
                            navItemFocusRequesters[TvCatalogDestination.Movies]?.let { runCatching { it.requestFocus() } }
                        },
                        onClick = onProfileClicked
                               )

                LazyColumn(
                        state = contentListState,
                        modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                        contentPadding = PaddingValues(
                                start = 40.dp,
                                top = 20.dp,
                                end = 40.dp,
                                bottom = 32.dp
                                                      ),
                        verticalArrangement = Arrangement.spacedBy(36.dp)
                          ) {
                    when (screenState) {
                        is TvCatalogScreenState.Movies          -> if (screenState.rows.isEmpty()) {
                            items(SHIMMER_ROW_PLACEHOLDER_COUNT) { TvRowShimmer() }
                        } else {
                            items(screenState.rows) { row ->
                                TvCatalogRow(
                                        category = row.category,
                                        items = row.items,
                                        onItemPressed = {},
                                        onItemFocused = { item ->
                                            onItemFocused(
                                                    item,
                                                    row.category
                                                         )
                                        },
                                        onEndReached = { onRowEndReached(row.category) },
                                        navRailFocusRequester = navItemFocusRequesters[selectedDestination]
                                            )
                            }
                        }
                        is TvCatalogScreenState.TvSeries        -> if (screenState.rows.isEmpty()) {
                            items(SHIMMER_ROW_PLACEHOLDER_COUNT) { TvRowShimmer() }
                        } else {
                            items(screenState.rows) { row ->
                                TvCatalogRow(
                                        category = row.category,
                                        items = row.items,
                                        onItemPressed = {},
                                        onItemFocused = { item ->
                                            onItemFocused(
                                                    item,
                                                    row.category
                                                         )
                                        },
                                        onEndReached = { onRowEndReached(row.category) },
                                        navRailFocusRequester = navItemFocusRequesters[selectedDestination]
                                            )
                            }
                        }
                        is TvCatalogScreenState.Favorites       -> if (screenState.loginRequired) {
                            item {
                                Text(
                                        text = stringResource(R.string.favorites_login_required),
                                        modifier = Modifier.padding(top = 8.dp),
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                            }
                        } else if (screenState.isLoading) {
                            items(SHIMMER_ROW_PLACEHOLDER_COUNT) { TvRowShimmer() }
                        } else {
                            items(screenState.rows) { row ->
                                TvCatalogRow(
                                        category = row.category,
                                        items = row.items,
                                        onItemPressed = { item ->
                                            onItemFocused(
                                                    item,
                                                    null
                                                         )
                                        },
                                        onEndReached = { onFavoritesRowEndReached(row.category) },
                                        emptyMessage = stringResource(
                                                if (row.category is TvSeriesCategory) R.string.favorites_empty_tv_series
                                                else R.string.favorites_empty_movies
                                                                     ),
                                        navRailFocusRequester = navItemFocusRequesters[selectedDestination]
                                            )
                            }
                        }
                        is TvCatalogScreenState.News            -> item {
                            if (screenState.keySetupRequired) {
                                NewsKeySetup(
                                        onApiKeySaved = { apiKey ->
                                            navItemFocusRequesters[selectedDestination]?.let { runCatching { it.requestFocus() } }
                                            onNewsApiKeySaved(apiKey)
                                        }
                                            )
                            } else if (screenState.isLoading) {
                                TvRowShimmer()
                            } else {
                                TvNewsRow(
                                        title = stringResource(TvCatalogDestination.News.labelResId),
                                        items = screenState.items,
                                        onNewsItemClicked = onNewsItemClicked,
                                        onEndReached = onNewsRowEndReached,
                                        navRailFocusRequester = navItemFocusRequesters[TvCatalogDestination.News]
                                         )
                            }
                        }
                        is TvCatalogScreenState.Search          -> item {
                            Column {
                                Text(
                                        text = stringResource(TvCatalogDestination.Search.labelResId),
                                        color = MaterialTheme.colorScheme.onBackground,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                Spacer(modifier = Modifier.height(16.dp))
                                val searchFieldFocusRequester = remember { FocusRequester() }
                                TvSearchBar(
                                        query = screenState.query,
                                        onQueryChanged = onSearchQueryChanged,
                                        focusRequester = searchFieldFocusRequester,
                                        navRailFocusRequester = navItemFocusRequesters[selectedDestination]
                                           )
                                Spacer(modifier = Modifier.height(24.dp))
                                when {
                                    screenState.query.isBlank() -> Unit
                                    screenState.isSearching     -> TvRowShimmer()
                                    else                        -> TvSearchResultsRow(
                                            items = screenState.items,
                                            onItemPressed = onSearchItemPressed,
                                            navRailFocusRequester = navItemFocusRequesters[selectedDestination]
                                                                                 )
                                }
                            }
                        }
                        is TvCatalogScreenState.Recommendations -> item {
                            TvRecommendationsContent(
                                    screenState = screenState,
                                    onApiKeySaved = { apiKey ->
                                        navItemFocusRequesters[selectedDestination]?.let { runCatching { it.requestFocus() } }
                                        onGeminiApiKeySaved(apiKey)
                                    },
                                    onItemPressed = { item ->
                                        onItemFocused(
                                                item,
                                                null
                                                     )
                                    },
                                    onRetryClicked = {
                                        navItemFocusRequesters[selectedDestination]?.let { runCatching { it.requestFocus() } }
                                        onRecommendationsRetryClicked()
                                    },
                                    navRailFocusRequester = navItemFocusRequesters[selectedDestination]
                                                  )
                        }
                        is TvCatalogScreenState.Offline         -> item {
                            TvOfflineView(
                                    onRetryClicked = {
                                        navItemFocusRequesters[selectedDestination]?.let { runCatching { it.requestFocus() } }
                                        onOfflineRetryClicked()
                                    },
                                    navRailFocusRequester = navItemFocusRequesters[selectedDestination]
                                         )
                        }
                        is TvCatalogScreenState.About           -> item {
                            Box(
                                    modifier = Modifier
                                            .focusRequester(aboutContentFocusRequester)
                                            .onPreviewKeyEvent { keyEvent ->
                                                if (keyEvent.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                                                when {
                                                    keyEvent.key == Key.DirectionDown                                     -> {
                                                        coroutineScope.launch { contentListState.animateScrollBy(SCROLL_STEP_PX) }
                                                        true
                                                    }
                                                    keyEvent.key == Key.DirectionUp && contentListState.canScrollBackward -> {
                                                        coroutineScope.launch { contentListState.animateScrollBy(-SCROLL_STEP_PX) }
                                                        true
                                                    }
                                                    keyEvent.key == Key.DirectionLeft                                     -> {
                                                        navItemFocusRequesters[TvCatalogDestination.About]?.let { runCatching { it.requestFocus() } }
                                                        true
                                                    }
                                                    else                                                                  -> false
                                                }
                                            }
                                            .focusable()
                               ) {
                                TvAboutContent()
                            }
                        }
                    }
                }

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

        CatalogNavRail(
                selectedDestination = selectedDestination,
                onDestinationSelected = onDestinationSelected,
                navItemFocusRequesters = navItemFocusRequesters,
                profileFocusRequester = profileFocusRequester,
                onMoviesItemFocusChanged = { moviesRailItemFocused = it },
                aboutContentFocusRequester = aboutContentFocusRequester,
                isFocusedState = isRailFocused,
                onFocusChanged = { isRailFocused.value = it }
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

