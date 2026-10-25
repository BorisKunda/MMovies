package com.bk.mmovies.demotv.ui.news

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.bk.mmovies.demotv.mock.mockArticles
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface NewsArticleBodyUiState {
    data object Loading : NewsArticleBodyUiState
    data class Loaded(val body: String, val imageUrl: String = "") : NewsArticleBodyUiState
    data object Unavailable : NewsArticleBodyUiState
}

class NewsDetailsTvViewModel(application: Application) : AndroidViewModel(application) {

    private val _bodyState = MutableStateFlow<NewsArticleBodyUiState>(NewsArticleBodyUiState.Loading)
    val bodyState: StateFlow<NewsArticleBodyUiState> = _bodyState.asStateFlow()

    fun loadArticleContent(articleUrl: String) {
        val article = mockArticles(getApplication<Application>().resources).firstOrNull { it.articleUrl == articleUrl }
        _bodyState.value = if (article != null && article.content.isNotBlank()) {
            NewsArticleBodyUiState.Loaded(article.content, article.imageUrl)
        } else {
            NewsArticleBodyUiState.Unavailable
        }
    }
}
