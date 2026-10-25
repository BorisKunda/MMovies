package com.bk.mmovies.demotv.ui.catalog

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.layout.LazyLayoutCacheWindow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bk.mmovies.demotv.R
import com.bk.mmovies.demotv.ui.catalog.content.CatalogContentList
import com.bk.mmovies.demotv.ui.catalog.content.RailReservedSpaceSpacer
import com.bk.mmovies.demotv.ui.catalog.content.TvCatalogNavRail
import com.bk.mmovies.demotv.ui.catalog.content.component.TvCatalogHeroDialog
import com.bk.mmovies.demotv.ui.catalog.content.component.profile.TvProfileHeader
import com.bk.mmovies.demotv.ui.component.TvProviderFooter
import com.bk.mmovies.domain.model.CatalogItem
import com.bk.mmovies.domain.model.Category
import com.bk.mmovies.domain.model.NewsItem
import com.bk.mmovies.ui.component.PoweredByTmdbFooter

internal const val FOCUS_REQUEST_RETRY_FRAMES = 5
private val CONTENT_LIST_CACHE_WINDOW = 220.dp

@Composable
fun CatalogTvScreen(
        viewModel: TvCatalogViewModel = viewModel(),
        onViewDetailsClicked: (item: CatalogItem, category: Category?) -> Unit = { _, _ -> },
        onNewsItemClicked: (newsItem: NewsItem) -> Unit = {},
        firstItemFocusRequester: FocusRequester? = null
                   ) {
    val selectedDestination by viewModel.selectedDestination.collectAsStateWithLifecycle()
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()
    val userProfileState by viewModel.userProfileState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.messageEvent.collect {
            Toast.makeText(context, context.getString(it), Toast.LENGTH_SHORT).show()
        }
    }

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
            onDestinationSelected = viewModel::onDestinationSelected,
            onViewDetailsClicked = onViewDetailsClicked,
            onFeaturedFavoriteClicked = viewModel::onFeaturedFavoriteClicked,
            onNewsItemClicked = onNewsItemClicked,
            onProfileClicked = viewModel::onProfileClicked,
            onItemFocused = viewModel::onItemFocused,
            onDialogDismissed = viewModel::onDialogDismissed,
            onSearchQueryChanged = viewModel::onSearchQueryChanged,
            onSearchItemPressed = { item -> viewModel.onItemFocused(item) },
            navItemFocusRequesters = navItemFocusRequesters
                          )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CatalogTvScreenContent(
        selectedDestination: TvCatalogDestination,
        screenState: TvCatalogScreenState,
        userProfileState: TvUserProfileUiState = TvUserProfileUiState(),
        onDestinationSelected: (TvCatalogDestination) -> Unit,
        onViewDetailsClicked: (item: CatalogItem, category: Category?) -> Unit,
        onFeaturedFavoriteClicked: () -> Unit,
        onNewsItemClicked: (newsItem: NewsItem) -> Unit = {},
        onProfileClicked: () -> Unit = {},
        onItemFocused: (item: CatalogItem, category: Category?) -> Unit = { _, _ -> },
        onDialogDismissed: () -> Unit = {},
        onSearchQueryChanged: (String) -> Unit = {},
        onSearchItemPressed: (item: CatalogItem) -> Unit = {},
        navItemFocusRequesters: Map<TvCatalogDestination, FocusRequester> = emptyMap()
                          ) {
    val focusedItem = when (screenState) {
        is TvCatalogScreenState.Movies   -> screenState.focusedItem
        is TvCatalogScreenState.TvSeries -> screenState.focusedItem
        is TvCatalogScreenState.Search   -> screenState.focusedItem
        else                             -> null
    }
    val focusedCategory = when (screenState) {
        is TvCatalogScreenState.Movies   -> screenState.focusedCategory
        is TvCatalogScreenState.TvSeries -> screenState.focusedCategory
        else                             -> null
    }

    val profileFocusRequester = remember { FocusRequester() }
    var moviesRailItemFocused by remember { mutableStateOf(false) }
    var profileFocused by remember { mutableStateOf(false) }
    val contentListState = rememberLazyListState(
            cacheWindow = LazyLayoutCacheWindow(
                    ahead = CONTENT_LIST_CACHE_WINDOW,
                    behind = CONTENT_LIST_CACHE_WINDOW
                                               )
                                                )
    val aboutContentFocusRequester = remember { FocusRequester() }
    val isRailFocused = remember { mutableStateOf(false) }
    val dialogCloseCoroutineScope = rememberCoroutineScope()

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
                        onSearchItemPressed = onSearchItemPressed
                                  )
                when (selectedDestination) {
                    TvCatalogDestination.News -> TvProviderFooter(
                            label = stringResource(R.string.powered_by_guardian),
                            modifier = Modifier.alpha(0f).padding(vertical = 10.dp)
                                                                 )
                    else                      -> PoweredByTmdbFooter(
                            modifier = Modifier.alpha(0f).padding(vertical = 10.dp)
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
                pruneOnClose = false,
                showFavoriteButton = true,
                railFocusRequester = navItemFocusRequesters[selectedDestination],
                onViewDetailsClicked = onViewDetailsClicked,
                onFavoriteClicked = onFeaturedFavoriteClicked,
                onDismissed = onDialogDismissed,
                onPopupClosedPrune = {},
                closeCoroutineScope = dialogCloseCoroutineScope
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
