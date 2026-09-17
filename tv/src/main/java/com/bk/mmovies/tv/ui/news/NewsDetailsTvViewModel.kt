package com.bk.mmovies.tv.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bk.mmovies.domain.model.result.NewsArticleContentResult
import com.bk.mmovies.domain.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface NewsArticleBodyUiState {
    data object Loading : NewsArticleBodyUiState
    data class Loaded(val body: String, val imageUrl: String = "") : NewsArticleBodyUiState

    // Not an error screen - the screen already has a real title/description
    // from the nav args, so a failed full-article fetch just means falling
    // back to that summary rather than blocking the whole screen.
    data object Unavailable : NewsArticleBodyUiState
}

@HiltViewModel
class NewsDetailsTvViewModel @Inject constructor(
        private val newsRepository: NewsRepository
                                                  ) : ViewModel() {

    private val _bodyState = MutableStateFlow<NewsArticleBodyUiState>(NewsArticleBodyUiState.Loading)
    val bodyState: StateFlow<NewsArticleBodyUiState> = _bodyState.asStateFlow()

    private var loadedArticleUrl: String? = null

    fun loadArticleContent(articleUrl: String) {
        if (loadedArticleUrl == articleUrl) return
        loadedArticleUrl = articleUrl
        _bodyState.value = NewsArticleBodyUiState.Loading
        viewModelScope.launch {
            _bodyState.value = when (val result = newsRepository.getNewsArticleContent(articleUrl)) {
                is NewsArticleContentResult.Success -> NewsArticleBodyUiState.Loaded(result.content, result.imageUrl)
                is NewsArticleContentResult.Failure  -> NewsArticleBodyUiState.Unavailable
            }
        }
    }
}
