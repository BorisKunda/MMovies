package com.bk.mmovies.tv.ui.catalog.content.carousel

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
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
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
import com.bk.mmovies.tv.theme.tvContentTextStyle
import com.bk.mmovies.tv.theme.tvHeaderTextStyle

private val newsItemWidth = 260.dp
private val newsItemHeight = 146.dp
private val newsItemShape = RoundedCornerShape(10.dp)

// How many items from the end of the row trigger the next page fetch - same
// value/reasoning as TvRow's own END_REACHED_THRESHOLD.
private const val END_REACHED_THRESHOLD = 3

@Composable
fun TvNewsRow(
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
                style = tvHeaderTextStyle
            )

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
                state = listState,
                modifier = Modifier
                        .padding(10.dp)
                        .focusGroup(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
               ) {
            itemsIndexed(items, key = { _, item -> item.articleUrl }) { index, newsItem ->
                TvNewsCard(
                        newsItem,
                        leftFocusRequester = if (index == 0) navRailFocusRequester else null,
                        isLast = index == items.lastIndex,
                        onClick = { onNewsItemClicked(newsItem) }
                          )
            }
        }
    }
}

@Composable
private fun TvNewsCard(
        newsItem: NewsItem,
        leftFocusRequester: FocusRequester?,
        // The last card swallows DirectionRight: the only thing to its right is
        // the header's profile button, which spatial focus search would jump to.
        isLast: Boolean,
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
                    style = tvContentTextStyle
                )
            if (newsItem.sourceName.isNotBlank()) {
                Text(
                        text = newsItem.sourceName,
                        modifier = Modifier.padding(top = 4.dp),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        style = tvContentTextStyle
                    )
            }
        }
    }
}
