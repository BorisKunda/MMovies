package com.bk.mmovies.ui.screen.search.screencomponents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bk.mmovies.R
import com.bk.mmovies.domain.model.SearchResultMediaType
import com.bk.mmovies.domain.model.SearchResultModel
import com.bk.mmovies.domain.model.SeriesAirDateLabel
import com.bk.mmovies.ui.component.EmptyStateView
import com.bk.mmovies.ui.component.GenericErrorScreen
import com.bk.mmovies.ui.component.LoaderView
import com.bk.mmovies.ui.component.LoadingMoreFooter
import com.bk.mmovies.ui.component.PaginationEffect
import com.bk.mmovies.ui.component.SeriesAirDateLabelText
import com.bk.mmovies.ui.screen.search.SearchResultsUiState
import com.bk.mmovies.ui.theme.CardSurface

// How far the client-side media-type filter will auto-page looking for a
// match before giving up and showing the empty state.
private const val MAX_FILTER_AUTO_PAGE = 5

private val listBottomPadding = 8.dp
private val rowPaddingHorizontal = 20.dp
private val rowPaddingVertical = 10.dp
private val posterWidth = 52.dp
private val posterHeight = 78.dp
private val posterCornerShape = 6.dp
private val rowSpacerWidth = 12.dp
private val badgeHorizontalPadding = 8.dp
private val badgeVerticalPadding = 3.dp
private val badgeCornerShape = 8.dp
private val subtitlePaddingTop = 2.dp
private const val TITLE_MAX_LINES = 2

private val recentSearchIconSize = 20.dp
private val recentSectionHeaderPadding = 20.dp

private val filterRowHorizontalPadding = 20.dp
private val filterRowVerticalPadding = 8.dp
private val filterChipSpacing = 8.dp

// Any number of chips can be selected at once (or none, meaning "all types
// shown") — each tap just toggles that one chip's membership in the set.
@Composable
fun SearchFilterRow(selectedFilters: Set<SearchResultMediaType>, onFilterToggled: (SearchResultMediaType) -> Unit) {
    LazyRow(
            contentPadding = PaddingValues(horizontal = filterRowHorizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(filterChipSpacing),
            modifier = Modifier.padding(vertical = filterRowVerticalPadding)
           ) {
        items(items = SearchResultMediaType.entries, key = { it }) { mediaType ->
            val label = stringResource(mediaType.labelResId)
            FilterChip(
                    selected = mediaType in selectedFilters,
                    onClick = { onFilterToggled(mediaType) },
                    label = { Text(label) },
                    colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                                                 )
                      )
        }
    }
}

@Composable
fun SearchIdleContent(recentSearches: List<String>, onRecentSearchClicked: (String) -> Unit,
        onRemoveRecentSearchClicked: (String) -> Unit, onClearRecentSearchesClicked: () -> Unit) {
    if (recentSearches.isEmpty()) {
        EmptyStateView(
                imageResId = R.drawable.tmdb_logo_square,
                title = stringResource(R.string.search_idle_title),
                message = stringResource(R.string.search_idle_message)
                      )
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                                horizontal = recentSectionHeaderPadding,
                                vertical = rowPaddingVertical
                                ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
           ) {
            Text(
                    text = stringResource(R.string.search_recent_title),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            Text(
                    text = stringResource(R.string.search_recent_clear_all_action),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.clickable { onClearRecentSearchesClicked() }
                )
        }
        LazyColumn(
                content = {
                    items(items = recentSearches, key = { it }) { recentQuery ->
                        RecentSearchRow(
                                query = recentQuery,
                                onClick = { onRecentSearchClicked(recentQuery) },
                                onRemoveClick = { onRemoveRecentSearchClicked(recentQuery) }
                                       )
                    }
                },
                contentPadding = PaddingValues(bottom = listBottomPadding)
                  )
    }
}

@Composable
private fun RecentSearchRow(query: String, onClick: () -> Unit, onRemoveClick: () -> Unit) {
    Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick() }
                    .padding(
                            horizontal = rowPaddingHorizontal,
                            vertical = rowPaddingVertical
                            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
       ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.size(recentSearchIconSize)
                )
            Spacer(modifier = Modifier.width(rowSpacerWidth))
            Text(
                    text = query,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge
                )
        }
        IconButton(onClick = onRemoveClick) {
            Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.search_recent_remove_action),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
        }
    }
}

@Composable
fun SearchResultsContent(state: SearchResultsUiState, selectedFilters: Set<SearchResultMediaType>,
        onResultClicked: (SearchResultModel) -> Unit, onRetry: () -> Unit, onLoadNextPage: () -> Unit = {},
        getTvSeriesAirDateLabel: suspend (seriesId: Int) -> SeriesAirDateLabel = { SeriesAirDateLabel.Upcoming("", "") }) {
    when (state) {
        is SearchResultsUiState.Idle    -> Unit
        is SearchResultsUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoaderView()
            }
        }

        is SearchResultsUiState.Content -> {
            val filteredResults = if (selectedFilters.isEmpty()) {
                state.results
            } else {
                state.results.filter { it.mediaType in selectedFilters }
            }
            if (filteredResults.isNotEmpty()) {
                SearchResultsList(
                        results = filteredResults,
                        onResultClicked = onResultClicked,
                        isLoadingNextPage = state.isLoadingNextPage,
                        onLoadNextPage = onLoadNextPage,
                        getTvSeriesAirDateLabel = getTvSeriesAirDateLabel
                                  )
            } else if (!state.endReached && state.currentPage < MAX_FILTER_AUTO_PAGE) {
                // Filtering is client-side: the active filter can exclude
                // every result fetched so far while a later, unfetched page
                // still holds a match. There's no list here for
                // PaginationEffect to attach to, so keep paging directly
                // until either a match turns up or the last page is hit.
                // Capped so a filter that matches nothing walks a handful of
                // pages instead of every page TMDB will serve.
                LaunchedEffect(state.currentPage, state.isLoadingNextPage, selectedFilters) {
                    if (!state.isLoadingNextPage) onLoadNextPage()
                }
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LoaderView()
                }
            } else {
                EmptyStateView(
                        imageResId = R.drawable.tmdb_logo_square,
                        title = stringResource(R.string.search_empty_title),
                        message = stringResource(R.string.search_empty_message)
                              )
            }
        }

        is SearchResultsUiState.Empty   -> {
            EmptyStateView(
                    imageResId = R.drawable.tmdb_logo_square,
                    title = stringResource(R.string.search_empty_title),
                    message = stringResource(R.string.search_empty_message)
                          )
        }

        is SearchResultsUiState.Error   -> {
            GenericErrorScreen(state.errorMessage, onTryAgainClicked = onRetry)
        }
    }
}

@Composable
private fun SearchResultsList(
        results: List<SearchResultModel>,
        onResultClicked: (SearchResultModel) -> Unit,
        isLoadingNextPage: Boolean = false,
        onLoadNextPage: () -> Unit = {},
        getTvSeriesAirDateLabel: suspend (seriesId: Int) -> SeriesAirDateLabel = { SeriesAirDateLabel.Upcoming("", "") }
                              ) {
    val listState = rememberLazyListState()
    listState.PaginationEffect(onLoadMore = onLoadNextPage)

    LazyColumn(
            state = listState,
            content = {
                items(items = results, key = { "${it.mediaType}_${it.id}" }) { result ->
                    SearchResultRow(
                            result = result,
                            onClick = { onResultClicked(result) },
                            getTvSeriesAirDateLabel = getTvSeriesAirDateLabel
                                   )
                }
                if (isLoadingNextPage) {
                    items(count = 1) { LoadingMoreFooter() }
                }
            },
            contentPadding = PaddingValues(bottom = listBottomPadding),
            modifier = Modifier.fillMaxSize()
              )
}

@Composable
private fun SearchResultRow(
        result: SearchResultModel,
        onClick: () -> Unit,
        getTvSeriesAirDateLabel: suspend (seriesId: Int) -> SeriesAirDateLabel = { SeriesAirDateLabel.Upcoming("", "") }
                            ) {
    // Search results only carry first_air_date, not status/last_air_date, so
    // a TV row's status-based label (year range / ongoing / upcoming) is
    // fetched lazily per row instead — same as the catalog list.
    var tvAirDateLabel by remember(result.id) { mutableStateOf<SeriesAirDateLabel?>(null) }
    LaunchedEffect(result.id) {
        if (result.mediaType == SearchResultMediaType.TV_SERIES) {
            tvAirDateLabel = getTvSeriesAirDateLabel(result.id)
        }
    }

    Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClickLabel = stringResource(R.string.movie_open_details_action)) { onClick() }
                    .padding(
                            horizontal = rowPaddingHorizontal,
                            vertical = rowPaddingVertical
                            ),
            verticalAlignment = Alignment.CenterVertically
       ) {
        AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                        .data(result.imageUrl)
                        .crossfade(true)
                        .build(),
                placeholder = painterResource(R.drawable.placeholder),
                error = painterResource(R.drawable.placeholder),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                        .size(width = posterWidth, height = posterHeight)
                        .clip(RoundedCornerShape(posterCornerShape))
                  )
        Spacer(modifier = Modifier.width(rowSpacerWidth))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                    text = result.title,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = TITLE_MAX_LINES,
                    overflow = TextOverflow.Ellipsis
                )
            if (result.mediaType == SearchResultMediaType.TV_SERIES) {
                tvAirDateLabel?.let { label ->
                    SeriesAirDateLabelText(
                            label = label,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = subtitlePaddingTop)
                                           )
                }
            } else if (result.subtitle.isNotBlank()) {
                Text(
                        text = result.subtitle,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = subtitlePaddingTop)
                    )
            }
            MediaTypeBadge(mediaType = result.mediaType, modifier = Modifier.padding(top = subtitlePaddingTop))
        }
    }
}

@Composable
private fun MediaTypeBadge(mediaType: SearchResultMediaType, modifier: Modifier = Modifier) {
    val label = stringResource(mediaType.labelResId)
    Box(
            modifier = modifier
                    .clip(RoundedCornerShape(badgeCornerShape))
                    .background(CardSurface)
                    .padding(horizontal = badgeHorizontalPadding, vertical = badgeVerticalPadding)
       ) {
        Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
            )
    }
}
