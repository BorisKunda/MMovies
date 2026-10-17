package com.bk.mmovies.tv.ui.news

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.bk.mmovies.tv.R
import com.bk.mmovies.tv.ui.news.preview.NewsDetailsPreviewScaffold
import com.bk.mmovies.tv.ui.news.preview.PREVIEW_BODY
import kotlinx.coroutines.launch

private val heroHeight = 320.dp
private val contentPadding = 32.dp
private val titleToMetaSpacing = 8.dp
private val metaToBodySpacing = 20.dp
private val loadingRowSpacing = 8.dp
private val posterCardCornerRadius = 16.dp
private val posterCardElevation = 8.dp
private val articleBodyStartPadding = 40.dp
private val articleBodyEndPadding = 20.dp

private const val SECONDARY_TEXT_ALPHA = 0.85f
// Same reasoning as WebViewTvScreen/TvEpisodeDetailsDialog's own copy of
// this: a plain Modifier.verticalScroll only responds to touch drag/fling,
// never D-pad key events, and this screen has no other focusable element to
// hang native scroll-into-view off of.
private const val SCROLL_STEP_PX = 400f

// This screen's own composition can race focusRequester()'s modifier
// attaching against the requestFocus() call below - a single attempt can
// silently land on nothing, leaving the entire article unscrollable by
// D-pad with no visible sign anything is wrong. Retrying across a few
// frames is a harmless no-op once focus is already there - same pattern
// used by the details screens' own initial-focus grab.
private const val FOCUS_REQUEST_RETRY_FRAMES = 5

/**
 * Native replacement for opening a news article in WebViewTvScreen - the
 * Guardian-backed :tv News feature can fetch a real full article body (see
 * NewsRepository.getNewsArticleContent), so there's no need to hand the user
 * off to the actual theguardian.com page just to read it.
 */
@Composable
fun NewsDetailsTvScreen(
        title: String,
        description: String,
        articleUrl: String,
        imageUrl: String,
        sourceName: String,
        author: String,
        publishedAt: String,
        onBack: () -> Unit,
        viewModel: NewsDetailsTvViewModel = hiltViewModel()
) {
    BackHandler(onBack = onBack)

    val bodyState by viewModel.bodyState.collectAsStateWithLifecycle()

    LaunchedEffect(articleUrl) { viewModel.loadArticleContent(articleUrl) }

    NewsDetailsTvContent(
            title = title,
            description = description,
            imageUrl = imageUrl,
            sourceName = sourceName,
            author = author,
            publishedAt = publishedAt,
            bodyState = bodyState
    )
}

@Composable
internal fun NewsDetailsTvContent(
        title: String,
        description: String,
        imageUrl: String,
        sourceName: String,
        author: String,
        publishedAt: String,
        bodyState: NewsArticleBodyUiState
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    // Nothing on this screen is otherwise focusable (no buttons, no list
    // items), so without grabbing focus here the D-pad UP/DOWN events below
    // never reach onKeyEvent at all - Compose only dispatches key events to
    // whatever currently holds focus.
    LaunchedEffect(Unit) {
        repeat(FOCUS_REQUEST_RETRY_FRAMES) {
            runCatching { focusRequester.requestFocus() }
            withFrameNanos {}
        }
    }

    Column(
            modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .focusRequester(focusRequester)
                    .focusable()
                    .onKeyEvent { keyEvent ->
                        if (keyEvent.type != KeyEventType.KeyDown) return@onKeyEvent false
                        val step = when (keyEvent.key) {
                            Key.DirectionDown -> SCROLL_STEP_PX
                            Key.DirectionUp   -> -SCROLL_STEP_PX
                            else              -> return@onKeyEvent false
                        }
                        coroutineScope.launch { scrollState.animateScrollBy(step) }
                        true
                    }
                    .verticalScroll(scrollState)
    ) {
        // The row's own thumbnail is a small ~140px crop - swap in the much
        // larger image fetched alongside the full body once available.
        val articleImageUrl = (bodyState as? NewsArticleBodyUiState.Loaded)
                                      ?.imageUrl
                                      ?.takeIf { it.isNotBlank() }
                              ?: imageUrl

        val metaText = listOf(
                sourceName,
                author,
                publishedAt
        ).filter { it.isNotBlank() }
                .joinToString(" • ")

        Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .align(Alignment.Start)
                        .padding(metaToBodySpacing)
        ) {
            if (articleImageUrl.isNotBlank()) {
                ArticleImage(imageUrl = articleImageUrl, imageKey = imageUrl)
            }
            Spacer(Modifier.size(metaToBodySpacing))
            Column(
                    Modifier
                            .wrapContentWidth()
                            .wrapContentHeight()
            ) {
                ArticleTitle(title)
                if (metaText.isNotBlank()) {
                    ArticleByline(metaText)
                }
            }
        }

        val bodyText = when (bodyState) {
            is NewsArticleBodyUiState.Loaded -> bodyState.body
            else                             -> description
        }
        if (bodyText.isNotBlank()) {
            ArticleContent(bodyText)
        }

        when (bodyState) {
            is NewsArticleBodyUiState.Loading -> {
                Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(loadingRowSpacing),
                        modifier = Modifier.padding(
                                start = articleBodyStartPadding,
                                end = articleBodyEndPadding,
                                top = metaToBodySpacing
                        )
                ) {
                    CircularProgressIndicator(
                            modifier = Modifier
                                    .width(16.dp)
                                    .height(16.dp)
                    )
                    Text(
                            text = stringResource(R.string.news_details_loading_full_article),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = SECONDARY_TEXT_ALPHA),
                            style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            is NewsArticleBodyUiState.Unavailable -> {
                Text(
                        text = stringResource(R.string.news_details_full_article_unavailable),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = SECONDARY_TEXT_ALPHA),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(
                                start = articleBodyStartPadding,
                                end = articleBodyEndPadding,
                                top = metaToBodySpacing
                        )
                )
            }

            is NewsArticleBodyUiState.Loaded -> Unit
        }
    }
}

@Composable
fun ArticleImage(imageUrl: String, imageKey: String = imageUrl) {
    // Retain the displayed image while the full-resolution replacement loads.
    var lastLoadedPainter by remember(imageKey) { mutableStateOf<Painter?>(null) }
    Card(
            shape = RoundedCornerShape(posterCardCornerRadius),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = posterCardElevation),
            modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp)
                    .fillMaxWidth(0.5f)
    ) {
        AsyncImage(
                model = imageUrl,
                contentDescription = null,
                placeholder = lastLoadedPainter,
                error = lastLoadedPainter,
                onSuccess = { lastLoadedPainter = it.painter },
                contentScale = ContentScale.Crop,
                modifier = Modifier
                        .fillMaxWidth()
                        .height(heroHeight)
        )
    }
}

@Composable fun ArticleTitle(title: String) {
    Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top=20.dp, start = 10.dp)
    )
}

@Composable
fun ArticleByline(bylineText: String) {
    Text(
            text = bylineText,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = SECONDARY_TEXT_ALPHA),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = 10.dp, top = titleToMetaSpacing)
    )
}

@Composable
fun ArticleContent(articleContentText: String) {
    Text(
            text = articleContentText,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = SECONDARY_TEXT_ALPHA),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = articleBodyStartPadding, end = articleBodyEndPadding)
    )
}

@Preview(
        device = "id:tv_1080p",
        name = "News details - loaded"
)
@Composable
private fun NewsDetailsTvScreenLoadedPreview() {
    NewsDetailsPreviewScaffold(bodyState = NewsArticleBodyUiState.Loaded(body = PREVIEW_BODY))
}

@Preview(
        device = "id:tv_1080p",
        name = "News details - loading"
)
@Composable
private fun NewsDetailsTvScreenLoadingPreview() {
    NewsDetailsPreviewScaffold(bodyState = NewsArticleBodyUiState.Loading)
}

@Preview(
        device = "id:tv_1080p",
        name = "News details - unavailable"
)
@Composable
private fun NewsDetailsTvScreenUnavailablePreview() {
    NewsDetailsPreviewScaffold(bodyState = NewsArticleBodyUiState.Unavailable)
}
