package com.bk.mmovies.tv.ui.catalog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.bk.mmovies.core.R as CoreR
import com.bk.mmovies.domain.model.NewsItem

private val newsItemWidth = 260.dp
private val newsItemHeight = 146.dp
private val newsItemShape = RoundedCornerShape(10.dp)
private val rememberedAccentHeight = 4.dp
private val rememberedAccentColor = Color(0xFFE7A23D)

// How many items from the end of the row trigger the next page fetch - same
// value/reasoning as TvRow's own END_REACHED_THRESHOLD.
private const val END_REACHED_THRESHOLD = 3

@Composable
fun TvNewsRow(
        // Identifies this row across recompositions - keys the remembered-
        // focus state below, mirroring TvRow's rowId. News only ever has the
        // one row, but a fixed id still lets the memory survive this
        // composable's own dispose/recompose cycle when navigating to a
        // details screen and back (see rowId's doc on TvRow for why
        // rememberSaveable, not plain remember, is required for that).
        rowId: String = "news",
        title: String,
        items: List<NewsItem>,
        onNewsItemClicked: (NewsItem) -> Unit = {},
        onEndReached: () -> Unit = {},
        // The News nav rail item. D-pad Left from the first card is sent here
        // explicitly: default spatial focus search picks whichever rail item
        // is nearest in height to the card (e.g. Favorites), not News.
        navRailFocusRequester: FocusRequester? = null
             ) {
    val listState = rememberLazyListState()

    // Row-level focus memory, mirroring TvRow's own copy of this exact
    // pattern - without it, returning to the News tab (or backing out of an
    // article) always reset focus to the first card instead of restoring
    // whichever one the user had actually been on.
    var lastFocusedKey by rememberSaveable(rowId) { mutableStateOf<String?>(null) }
    var rowHasFocus by remember(rowId) { mutableStateOf(false) }
    val focusRequesters = remember(rowId) { mutableMapOf<String, FocusRequester>() }

    LaunchedEffect(listState, items.size) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                .collect { lastVisibleIndex ->
                    if (lastVisibleIndex != null && lastVisibleIndex >= items.size - END_REACHED_THRESHOLD) {
                        onEndReached()
                    }
                }
    }

    Column {
        Text(
                text = title,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall
            )

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
                state = listState,
                modifier = Modifier
                        .padding(10.dp)
                        .focusGroup()
                        .onFocusChanged { focusState ->
                            if (focusState.hasFocus && !rowHasFocus) {
                                lastFocusedKey?.let { key -> focusRequesters[key]?.let { runCatching { it.requestFocus() } } }
                            }
                            rowHasFocus = focusState.hasFocus
                        },
                horizontalArrangement = Arrangement.spacedBy(12.dp)
               ) {
            itemsIndexed(items, key = { _, item -> item.articleUrl }) { index, newsItem ->
                val focusRequester = remember(newsItem.articleUrl) {
                    focusRequesters.getOrPut(newsItem.articleUrl) { FocusRequester() }
                }
                TvNewsCard(
                        newsItem,
                        isRemembered = newsItem.articleUrl == lastFocusedKey,
                        leftFocusRequester = if (index == 0) navRailFocusRequester else null,
                        isLast = index == items.lastIndex,
                        focusRequester = focusRequester,
                        onFocusChanged = { isFocused -> if (isFocused) lastFocusedKey = newsItem.articleUrl },
                        onClick = { onNewsItemClicked(newsItem) }
                          )
            }
        }
    }
}

@Composable
private fun TvNewsCard(
        newsItem: NewsItem,
        isRemembered: Boolean,
        leftFocusRequester: FocusRequester?,
        // The last card swallows DirectionRight: the only thing to its right is
        // the header's profile button, which spatial focus search would jump to.
        isLast: Boolean,
        focusRequester: FocusRequester,
        onFocusChanged: (isFocused: Boolean) -> Unit,
        onClick: () -> Unit
                       ) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Box(
            modifier = Modifier
                    .width(newsItemWidth)
                    .height(newsItemHeight)
                    .then(
                            if (isFocused) {
                                Modifier.border(
                                        width = 3.dp,
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = newsItemShape
                                                )
                            } else {
                                Modifier
                            }
                         )
                    .focusRequester(focusRequester)
                    .then(
                            if (leftFocusRequester != null || isLast) {
                                Modifier.onPreviewKeyEvent { keyEvent ->
                                    when {
                                        leftFocusRequester != null &&
                                                keyEvent.type == KeyEventType.KeyDown &&
                                                keyEvent.key == Key.DirectionLeft  -> {
                                            runCatching { leftFocusRequester.requestFocus() }
                                            true
                                        }
                                        isLast &&
                                                keyEvent.type == KeyEventType.KeyDown &&
                                                keyEvent.key == Key.DirectionRight -> true
                                        else                                       -> false
                                    }
                                }
                            } else {
                                Modifier
                            }
                         )
                    // onFocusChanged only observes the focus target that
                    // comes AFTER it in the chain, so it must precede
                    // clickable() - same rule as TvItemView.
                    .onFocusChanged { focusState -> onFocusChanged(focusState.isFocused) }
                    .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = onClick
                              )
       ) {
        if (newsItem.imageUrl.isNotBlank()) {
            AsyncImage(
                    model = newsItem.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                            .fillMaxSize()
                            .clip(newsItemShape)
                      )
        } else {
            Image(
                    painter = painterResource(CoreR.drawable.placeholder_wide),
                    contentDescription = null,
                    modifier = Modifier
                            .fillMaxSize()
                            .clip(newsItemShape)
                 )
        }
        Box(
                modifier = Modifier
                        .fillMaxSize()
                        .clip(newsItemShape)
                        .background(Color.Black.copy(alpha = 0.35f))
           )
        Column(
                modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp)
              ) {
            Text(
                    text = newsItem.title,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                )
            if (newsItem.sourceName.isNotBlank()) {
                Text(
                        text = newsItem.sourceName,
                        modifier = Modifier.padding(top = 4.dp),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodySmall
                    )
            }
        }
        if (isRemembered && !isFocused) {
            Box(
                    modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(rememberedAccentHeight)
                            .background(rememberedAccentColor)
               )
        }
    }
}
