package com.bk.mmovies.ui.screen.news

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bk.mmovies.R
import com.bk.mmovies.connectivity.openWebPage
import com.bk.mmovies.core.R as CoreR
import com.bk.mmovies.data.source.remote.NEWS_API_WEBPAGE_ACCOUNT
import com.bk.mmovies.data.source.remote.NEWS_API_WEBPAGE_GET_STARTED
import com.bk.mmovies.domain.model.NewsItem
import com.bk.mmovies.ui.component.EmptyStateView
import com.bk.mmovies.ui.component.GenericErrorScreen
import com.bk.mmovies.ui.component.InvalidApiKeyView
import com.bk.mmovies.ui.screen.news.screencomponents.NewsCardLoadingPlaceholderList
import com.bk.mmovies.ui.screen.news.screencomponents.NewsListView

/**
 * News screen, hosted as the News tab inside [com.bk.mmovies.ui.screen.catalog.CatalogScreen]'s
 * bottom tab bar.
 *
 * [onOpenArticle] is delegated to the caller (rather than pushing
 * [ArticleWebViewScreen] internally) so it can be rendered as a full-screen
 * overlay above the host's own bottom nav bar/footer instead of being boxed
 * into this tab's content area.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(onOpenArticle: (String) -> Unit, viewModel: NewsViewModel = hiltViewModel()) {
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                        title = {
                            Text(
                                    text = stringResource(R.string.news_screen_title),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.titleMedium
                                )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.background
                                                                  )
                         )
            }
            ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            NewsScreenBody(
                    screenState = screenState,
                    onRetry = viewModel::retry,
                    onReadFullArticle = { onOpenArticle(it.articleUrl) },
                    onLoadNextPage = viewModel::loadNextPage,
                    onShare = { shareNewsItem(context, it) },
                    onOpenNewsApiAccount = { openWebPage(context, NEWS_API_WEBPAGE_ACCOUNT) },
                    onApiKeyHelp = { openWebPage(context, NEWS_API_WEBPAGE_GET_STARTED) },
                    onSaveApiKey = viewModel::saveApiKey
                          )
        }
    }

    val invalidApiKeyToastMessage = stringResource(CoreR.string.toast_invalid_api_key)
    LaunchedEffect(Unit) {
        viewModel.invalidApiKeyToastEvent.collect {
            Toast.makeText(context, invalidApiKeyToastMessage, Toast.LENGTH_SHORT).show()
        }
    }
}

// Internal (not private) so the preview file in ui/screen/news/preview can
// render each screen state without needing a Hilt-backed NewsViewModel.
@Composable
internal fun NewsScreenBody(
        screenState: NewsScreenState,
        onRetry: () -> Unit,
        onReadFullArticle: (NewsItem) -> Unit,
        onLoadNextPage: () -> Unit = {},
        onShare: (NewsItem) -> Unit = {},
        onOpenNewsApiAccount: () -> Unit = {},
        onApiKeyHelp: () -> Unit = {},
        onSaveApiKey: (String) -> Unit = {}
                          ) {
    when (screenState) {
        is NewsScreenState.NeedsApiKey -> {
            InvalidApiKeyView(
                    onOpenTmdbSettingsClicked = onOpenNewsApiAccount,
                    onApiKeyHelpClicked = onApiKeyHelp,
                    onSaveClicked = onSaveApiKey,
                    missingKey = screenState.missingKey,
                    openSettingsLabel = stringResource(CoreR.string.open_newsapi_account)
                             )
        }

        is NewsScreenState.Loading -> {
            NewsCardLoadingPlaceholderList()
        }

        is NewsScreenState.Empty   -> {
            EmptyStateView(
                    imageResId = R.drawable.ic_popcorn_bucket,
                    title = stringResource(R.string.empty_news_title),
                    message = stringResource(R.string.empty_news_message)
                          )
        }

        is NewsScreenState.Error   -> {
            GenericErrorScreen(screenState.errorMessage, onRetry)
        }

        is NewsScreenState.Content -> {
            NewsListView(
                    newsItems = screenState.newsItems,
                    onReadFullArticle = onReadFullArticle,
                    isLoadingNextPage = screenState.isLoadingNextPage,
                    onLoadNextPage = onLoadNextPage,
                    onShare = onShare
                        )
        }
    }
}
