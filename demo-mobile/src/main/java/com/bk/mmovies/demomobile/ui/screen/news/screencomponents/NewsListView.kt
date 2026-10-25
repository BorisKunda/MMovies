package com.bk.mmovies.demomobile.ui.screen.news.screencomponents

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import com.bk.mmovies.demomobile.R
import com.bk.mmovies.domain.model.NewsItem
import com.bk.mmovies.ui.theme.CardSurface

private val cardCornerShape = RoundedCornerShape(12.dp)
private val cardSpacing = 12.dp
private val cardPadding = 12.dp
private val imageHeight = 160.dp

private const val RESOURCE_URI_SCHEME = "android.resource"

@Composable
fun NewsListView(
        newsItems: List<NewsItem>,
        onReadFullArticle: (NewsItem) -> Unit,
        modifier: Modifier = Modifier,
        onShare: (NewsItem) -> Unit = {}
                 ) {
    val listState = rememberLazyListState()

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
                        model = rememberArticleImageModel(newsItem.imageUrl),
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

@Composable
private fun rememberArticleImageModel(imageUrl: String): Any {
    val context = LocalContext.current
    return remember(imageUrl) {
        val uri = imageUrl.toUri()
        if (uri.scheme == RESOURCE_URI_SCHEME) {
            context.resources.getIdentifier(uri.lastPathSegment, "drawable", context.packageName)
        } else {
            imageUrl
        }
    }
}
