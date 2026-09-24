package com.bk.mmovies.tv.ui.component

import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import com.bk.mmovies.domain.model.CatalogItem

// How many items from the end of the row trigger the next page fetch -
// large enough that the request is already in flight before the user's
// D-pad actually reaches the last loaded item.
private const val END_REACHED_THRESHOLD = 3

@Composable
fun TvRow(
        // Identifies this row across recompositions (typically the backing
        // category's id) - keys the remembered-focus state below, so two
        // different rows never share/clobber each other's "last focused
        // item" memory, and a genuinely new row (different id) starts fresh
        // rather than inheriting a stale key from whatever row previously
        // occupied this composable slot.
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

    // Row-level focus memory: remembers which item last held focus in this
    // row (by key, not index - pagination only appends, so a key stays
    // valid even as more items load) and, the moment the row as a whole
    // regains focus (rather than never having lost it), redirects focus
    // straight to that item instead of wherever plain spatial focus-search
    // would have landed. Mirrors YouTube TV's row-level focus memory - see
    // the catalog focus map doc.
    //
    // rememberSaveable (not plain remember) so this also survives the round
    // trip through a pushed details screen - Navigation-Compose keeps each
    // back-stack entry's SaveableStateRegistry alive while it's off-screen,
    // even though this composable itself is fully disposed/recomposed, so a
    // plain remember would silently reset to card 1 on the way back. The
    // FocusRequesters below don't need the same treatment - they're just
    // recreated lazily, keyed the same way, once the row recomposes.
    var lastFocusedKey by rememberSaveable(rowId) { mutableStateOf<String?>(null) }
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
                    .padding(10.dp)
                    .focusGroup()
                    .onFocusChanged { focusState ->
                        if (focusState.hasFocus && !rowHasFocus) {
                            // A remembered key can be stale (e.g. from an earlier
                            // search's results) - only trust it if it's still here.
                            val rememberedKey = lastFocusedKey?.takeIf { key -> list.any { "${it.mediaType}_${it.id}" == key } }
                            val target = rememberedKey ?: list.firstOrNull()
                                    ?.takeIf { focusFirstOnEntry }
                                    ?.let { "${it.mediaType}_${it.id}" }
                            target?.let { key -> focusRequesters[key]?.let { runCatching { it.requestFocus() } } }
                        }
                        rowHasFocus = focusState.hasFocus
                    },
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
                    isRemembered = itemKey == lastFocusedKey,
                    focusRequester = focusRequester,
                    onFocusChanged = { isFocused -> if (isFocused) lastFocusedKey = itemKey },
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
