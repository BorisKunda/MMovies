package com.bk.mmovies.tv.ui.catalog.content

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bk.mmovies.domain.model.CatalogItem
import com.bk.mmovies.domain.model.Category
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.domain.model.NewsItem
import com.bk.mmovies.domain.model.TvSeriesCategory
import com.bk.mmovies.tv.R
import com.bk.mmovies.tv.theme.tvHeaderTextStyle
import com.bk.mmovies.tv.ui.catalog.TvCatalogDestination
import com.bk.mmovies.tv.ui.catalog.TvCatalogScreenState
import com.bk.mmovies.tv.ui.catalog.content.about.TvAboutContent
import com.bk.mmovies.tv.ui.catalog.content.carousel.TvCatalogRow
import com.bk.mmovies.tv.ui.catalog.content.carousel.TvNewsRow
import com.bk.mmovies.tv.ui.catalog.content.carousel.TvSearchResultsRow
import com.bk.mmovies.tv.ui.catalog.content.carousel.TvSearchRow
import com.bk.mmovies.tv.ui.catalog.content.carousel.TvShimmerRow
import com.bk.mmovies.tv.ui.catalog.content.keysetup.NewsKeySetup
import com.bk.mmovies.tv.ui.catalog.content.recommendations.TvRecommendationsContent
import com.bk.mmovies.tv.ui.component.TvOfflineView
import kotlinx.coroutines.launch

private val catalogContentPadStart = 20.dp
private val catalogContentMarginTop = 80.dp
private val catalogContentPadEnd = 10.dp
private val catalogContentPadBottom = 32.dp
private val catalogRowsSpacing = 36.dp

private const val SCROLL_STEP_PX = 400f
private const val SHIMMER_ROW_PLACEHOLDER_COUNT = 2

@Composable
fun CatalogContentList(
        selectedDestination: TvCatalogDestination,
        screenState: TvCatalogScreenState,
        listState: LazyListState,
        navItemFocusRequesters: Map<TvCatalogDestination, FocusRequester>,
        aboutContentFocusRequester: FocusRequester,
        modifier: Modifier = Modifier,
        onNewsItemClicked: (newsItem: NewsItem) -> Unit = {},
        onLoginClicked: () -> Unit = {},
        onItemFocused: (item: CatalogItem, category: Category?) -> Unit = { _, _ -> },
        onSearchQueryChanged: (String) -> Unit = {},
        onSearchItemPressed: (item: CatalogItem) -> Unit = {},
        onRowEndReached: (category: Category) -> Unit = {},
        onFavoritesRowEndReached: (category: Category) -> Unit = {},
        onNewsRowEndReached: () -> Unit = {},
        onRecommendationsRetryClicked: () -> Unit = {},
        onOfflineRetryClicked: () -> Unit = {},
        onGeminiApiKeySaved: (apiKey: String) -> Unit = {},
        onNewsApiKeySaved: (apiKey: String) -> Unit = {}
                       ) {
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
            state = listState,
            modifier = modifier
                    .fillMaxWidth()
                    .padding(top = catalogContentMarginTop)
                    .focusRestorer(),
            contentPadding = PaddingValues(
                    start = catalogContentPadStart,
                    end = catalogContentPadEnd,
                    bottom = catalogContentPadBottom
                                          ),
            verticalArrangement = Arrangement.spacedBy(catalogRowsSpacing)
              ) {
        when (screenState) {
            is TvCatalogScreenState.Movies          -> if (screenState.rows.isEmpty()) {
                items(SHIMMER_ROW_PLACEHOLDER_COUNT) { TvShimmerRow() }
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
                items(SHIMMER_ROW_PLACEHOLDER_COUNT) { TvShimmerRow() }
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
                    Column {
                        TvCatalogRow(
                                category = MovieCategory.FavoritesMovieCategory,
                                items = emptyList(),
                                onItemPressed = {},
                                emptyMessage = stringResource(R.string.favorites_login_required),
                                headerLabelRes = R.string.nav_favorites
                                    )
                        Button(
                                onClick = onLoginClicked,
                                modifier = Modifier.padding(start = 25.dp)
                              ) {
                            Text(text = stringResource(R.string.login_action))
                        }
                    }
                }
            } else if (screenState.isLoading) {
                items(SHIMMER_ROW_PLACEHOLDER_COUNT) { TvShimmerRow() }
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
                    TvShimmerRow()
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
                            style = tvHeaderTextStyle
                        )
                    Spacer(modifier = Modifier.height(16.dp))
                    val searchFieldFocusRequester = remember { FocusRequester() }
                    TvSearchRow(
                            query = screenState.query,
                            onQueryChanged = onSearchQueryChanged,
                            focusRequester = searchFieldFocusRequester,
                            navRailFocusRequester = navItemFocusRequesters[selectedDestination]
                               )
                    Spacer(modifier = Modifier.height(24.dp))
                    when {
                        screenState.query.isBlank() -> Unit
                        screenState.isSearching     -> TvShimmerRow()
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
                                            coroutineScope.launch { listState.animateScrollBy(SCROLL_STEP_PX) }
                                            true
                                        }
                                        keyEvent.key == Key.DirectionUp                                       -> {
                                            if (listState.canScrollBackward) {
                                                coroutineScope.launch { listState.animateScrollBy(-SCROLL_STEP_PX) }
                                            }
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
}
