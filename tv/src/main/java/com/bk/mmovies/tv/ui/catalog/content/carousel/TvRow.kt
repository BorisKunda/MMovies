package com.bk.mmovies.tv.ui.catalog.content.carousel

import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import com.bk.mmovies.domain.model.CatalogItem
import com.bk.mmovies.tv.ui.catalog.content.component.TvItem

// How many items from the end of the row trigger the next page fetch -
// large enough that the request is already in flight before the user's
// D-pad actually reaches the last loaded item.
private const val END_REACHED_THRESHOLD = 3

@Composable
fun TvRow(
        // Identifies this row across recompositions (typically the backing
        // category's id) - keys the per-row focus-entry state below, so a
        // genuinely new row (different id) starts fresh.
        rowId: String,
        list: List<CatalogItem>,
        onItemPressed: (item: CatalogItem) -> Unit,
        onItemFocused: (item: CatalogItem) -> Unit = {},
        onEndReached: () -> Unit = {},
        isUpcoming: Boolean = false,
        // The current destination's nav rail item. D-pad Left from the row's
        // first item is sent here explicitly: default spatial focus search
        // picks whichever rail item is nearest in height to the item (e.g.
        // News from a Movies row), not the rail item of the tab being viewed.
        navRailFocusRequester: FocusRequester? = null,
        // When focus enters the row from outside and there's no remembered
        // item to restore, start on the first item instead of whichever card
        // spatial focus search found nearest to where focus came from (e.g.
        // the middle card, when entering from a wide search field above).
        focusFirstOnEntry: Boolean = false
         ) {
    val listState = rememberLazyListState()

    var rowHasFocus by remember(rowId) { mutableStateOf(false) }
    val focusRequesters = remember(rowId) { mutableMapOf<String, FocusRequester>() }

    LaunchedEffect(listState, list.size) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                .collect { lastVisibleIndex ->
                    if (lastVisibleIndex != null && lastVisibleIndex >= list.size - END_REACHED_THRESHOLD) {
                        onEndReached()
                    }
                }
    }

    LazyRow(
            state = listState,
            modifier = Modifier
                  //  .padding(10.dp)
                    .then(if (focusFirstOnEntry) Modifier.focusGroup() else Modifier.focusRestorer())
                    .onFocusChanged { focusState ->
                        if (focusState.hasFocus && !rowHasFocus && focusFirstOnEntry) {
                            list.firstOrNull()?.let { first ->
                                focusRequesters["${first.mediaType}_${first.id}"]?.let { runCatching { it.requestFocus() } }
                            }
                        }
                        rowHasFocus = focusState.hasFocus
                    },
            contentPadding = PaddingValues(end = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
           ) {
        itemsIndexed(
                list,
                // mediaType included: TMDB ids aren't unique across movies
                // vs tv series, and Search mixes both in one row - a bare
                // id key would crash LazyRow the first time they collided.
                key = { _, item -> "${item.mediaType}_${item.id}" }) { index, item ->
            val itemKey = "${item.mediaType}_${item.id}"
            val focusRequester = remember(itemKey) { focusRequesters.getOrPut(itemKey) { FocusRequester() } }
            TvItem(
                    posterUrl = item.imageUrl,
                    title = item.title,
                    isUpcoming = isUpcoming,
                    focusRequester = focusRequester,
                    onKeyPressed = { keyEvent ->
                        // The preview banner only reacts to an explicit
                        // D-pad center press now, not to focus just passing
                        // over an item while navigating the row.
                        if (keyEvent.type == KeyEventType.KeyUp &&
                            keyEvent.key == Key.DirectionCenter
                        ) {
                            onItemFocused(item)
                            onItemPressed(item)
                        }
                        when {
                            index == 0 &&
                                    navRailFocusRequester != null &&
                                    keyEvent.type == KeyEventType.KeyDown &&
                                    keyEvent.key == Key.DirectionLeft -> {
                                runCatching { navRailFocusRequester.requestFocus() }
                                true
                            }
                            // Nothing lies to the right of a row's last card except
                            // the profile button in the header, which spatial focus
                            // search would otherwise jump to. Swallow the key; if
                            // more pages are loading, the next press moves on.
                            index == list.lastIndex &&
                                    keyEvent.type == KeyEventType.KeyDown &&
                                    keyEvent.key == Key.DirectionRight -> true
                            else                                       -> false
                        }
                    }
                  )
        }
    }
}
