package com.bk.mmovies.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private val footerPadding = 16.dp
private val footerIndicatorSize = 24.dp

// Fires onLoadMore on every layout pass where the user is scrolled within
// `buffer` items of the end of the list, so the next page starts loading
// before they hit the bottom edge. Deliberately not deduped on the
// near-end boolean: if a loaded page doesn't move the last visible item
// past the buffer window (e.g. a short/filtered list), the boolean never
// changes and a LaunchedEffect keyed on it would never re-fire even though
// more pages are still available. onLoadMore's own isLoadingNextPage/
// endReached guard on the caller side makes the repeated calls harmless.
@Composable
fun LazyListState.PaginationEffect(buffer: Int = 4, onLoadMore: () -> Unit) {
    LaunchedEffect(this, buffer) {
        snapshotFlow { layoutInfo }
                .collect { info ->
                    val totalItems = info.totalItemsCount
                    val lastVisibleIndex = info.visibleItemsInfo.lastOrNull()?.index ?: 0
                    if (totalItems > 0 && lastVisibleIndex >= totalItems - 1 - buffer) {
                        onLoadMore()
                    }
                }
    }
}

@Composable
fun LoadingMoreFooter(modifier: Modifier = Modifier) {
    Box(
            modifier = modifier
                    .fillMaxWidth()
                    .padding(footerPadding),
            contentAlignment = Alignment.Center
       ) {
        CircularProgressIndicator(
                modifier = Modifier.size(footerIndicatorSize),
                color = MaterialTheme.colorScheme.primary
                                  )
    }
}
