package com.bk.mmovies.demomobile.ui.screen.news

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.bk.mmovies.demomobile.R
import com.bk.mmovies.domain.model.NewsItem
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

data class MockNewsScreenState(
        val newsItems: List<NewsItem>
                              )

class MockNewsViewModel(application: Application) : AndroidViewModel(application) {

    private val _screenState = MutableStateFlow(MockNewsScreenState(loadArticles()))
    val screenState: StateFlow<MockNewsScreenState> = _screenState.asStateFlow()

    private val _messageEvent = MutableSharedFlow<Int>(extraBufferCapacity = 1)
    val messageEvent: SharedFlow<Int> = _messageEvent.asSharedFlow()

    fun refreshArticles() {
        _screenState.value = MockNewsScreenState(loadArticles())
    }

    fun onReadFullArticle(newsItem: NewsItem) {
        _messageEvent.tryEmit(R.string.demo_full_article_unavailable)
    }

    fun onShare(newsItem: NewsItem) = Unit

    private fun loadArticles(): List<NewsItem> = mockArticles(getApplication<Application>().resources)
}
