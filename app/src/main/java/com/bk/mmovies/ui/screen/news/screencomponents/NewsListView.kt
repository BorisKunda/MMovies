package com.bk.mmovies.ui.screen.news.screencomponents

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.bk.mmovies.R
import com.bk.mmovies.domain.model.NewsItem
import com.bk.mmovies.ui.component.LoadingMoreFooter
import com.bk.mmovies.ui.component.PaginationEffect
import com.bk.mmovies.ui.theme.CardSurface

private val cardCornerShape = RoundedCornerShape(12.dp)
private val cardSpacing = 12.dp
private val cardPadding = 12.dp
private val imageHeight = 160.dp

@Composable
fun NewsListView(
        newsItems: List<NewsItem>,
        onReadFullArticle: (NewsItem) -> Unit,
        modifier: Modifier = Modifier,
        isLoadingNextPage: Boolean = false,
        onLoadNextPage: () -> Unit = {},
        onShare: (NewsItem) -> Unit = {}
                 ) {
    val listState = rememberLazyListState()
    listState.PaginationEffect(onLoadMore = onLoadNextPage)

    LazyColumn(
            state = listState,
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(cardSpacing),
            verticalArrangement = Arrangement.spacedBy(cardSpacing)
              ) {
        items(newsItems, key = { it.articleUrl }) { newsItem ->
            NewsCard(
                    newsItem = newsItem,
                    onReadFullArticle = { onReadFullArticle(newsItem) },
                    onShare = { onShare(newsItem) }
                    )
        }
        if (isLoadingNextPage) {
            item { LoadingMoreFooter() }
        }
    }
}

@Composable
fun NewsCard(
        newsItem: NewsItem,
        onReadFullArticle: () -> Unit,
        modifier: Modifier = Modifier,
        onShare: () -> Unit = {}
            ) {
    Card(
            modifier = modifier.fillMaxWidth(),
            shape = cardCornerShape,
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
        Column(modifier = Modifier.padding(cardPadding)) {
            if (newsItem.imageUrl.isNotBlank()) {
                AsyncImage(
                        model = newsItem.imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(imageHeight)
                                .clip(cardCornerShape)
                          )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(
                    text = newsItem.title.ifBlank { stringResource(R.string.news_untitled_article) },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                        imageVector = Icons.AutoMirrored.Filled.Article,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                        text = newsItem.sourceName.ifBlank { stringResource(R.string.news_source_unknown) },
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                if (newsItem.publishedAt.isNotBlank()) {
                    Text(
                            text = " • ${newsItem.publishedAt}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                }
            }

            if (newsItem.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                        text = newsItem.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
               ) {
                TextButton(onClick = onReadFullArticle) {
                    Text(text = stringResource(R.string.news_read_full_article))
                }
                IconButton(onClick = onShare) {
                    Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = stringResource(R.string.news_share_action),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                }
            }
        }
    }
}
