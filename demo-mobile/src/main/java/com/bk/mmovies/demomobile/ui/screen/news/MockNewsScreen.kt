package com.bk.mmovies.demomobile.ui.screen.news

import android.widget.Toast
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bk.mmovies.demomobile.R
import com.bk.mmovies.demomobile.ui.screen.news.screencomponents.NewsListView
import com.bk.mmovies.domain.model.NewsItem

@Composable
fun MockNewsScreen(viewModel: MockNewsViewModel = viewModel()) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current
    val context = LocalContext.current

    LaunchedEffect(configuration) {
        viewModel.refreshArticles()
    }

    LaunchedEffect(Unit) {
        viewModel.messageEvent.collect { messageRes ->
            Toast.makeText(context, messageRes, Toast.LENGTH_SHORT).show()
        }
    }

    MockNewsScreenContent(
            state = state,
            onReadFullArticle = viewModel::onReadFullArticle,
            onShare = viewModel::onShare
                         )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MockNewsScreenContent(
        state: MockNewsScreenState,
        onReadFullArticle: (NewsItem) -> Unit,
        onShare: (NewsItem) -> Unit
                         ) {
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
            NewsListView(
                    newsItems = state.newsItems,
                    onReadFullArticle = onReadFullArticle,
                    onShare = onShare
                        )
        }
    }
}
