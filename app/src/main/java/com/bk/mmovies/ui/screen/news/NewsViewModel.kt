package com.bk.mmovies.ui.screen.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bk.mmovies.domain.model.NewsItem
import com.bk.mmovies.domain.model.canLoadNextPage
import com.bk.mmovies.domain.model.isLastPage
import com.bk.mmovies.domain.model.mergePagedItems
import com.bk.mmovies.domain.model.result.NewsResult
import com.bk.mmovies.domain.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
        private val newsRepository: NewsRepository
                                        ) : ViewModel() {

    private val _screenState = MutableStateFlow<NewsScreenState>(NewsScreenState.Loading)
    val screenState: StateFlow<NewsScreenState> = _screenState.asStateFlow()

    private var loadNewsJob: Job? = null
    private var loadNextPageJob: Job? = null

    init {
        loadNews()
    }

    fun retry() {
        loadNews()
    }

    /**
     * Fetches the page after the one currently shown and appends it to the
     * list, triggered by the list scrolling near its end - mirrors
     * [com.bk.mmovies.ui.screen.catalog.CatalogViewModel.loadNextPage]. A
     * no-op while already loading or once the last page has been reached.
     */
    fun loadNextPage() {
        val currentState = _screenState.value
        if (currentState !is NewsScreenState.Content) return
        if (!canLoadNextPage(currentState.endReached, currentState.isLoadingNextPage)) return
        val nextPage = currentState.currentPage + 1
        _screenState.value = currentState.copy(isLoadingNextPage = true)
        loadNextPageJob?.cancel()
        loadNextPageJob = viewModelScope.launch {
            applyResult(newsRepository.getNews(nextPage), nextPage)
        }
    }

    private fun loadNews() {
        loadNextPageJob?.cancel()
        loadNewsJob?.cancel()
        loadNewsJob = viewModelScope.launch {
            _screenState.value = NewsScreenState.Loading
            applyResult(newsRepository.getNews(page = 1), page = 1)
        }
    }

    // Page 1 replaces the list; later pages append to whatever is already
    // showing. A failed page 1 is a real error screen; a failed next page
    // just stops the footer spinner so scrolling back near the end retries it.
    private fun applyResult(result: NewsResult, page: Int) {
        when (result) {
            is NewsResult.Success -> {
                val currentState = _screenState.value
                val existingItems = (currentState as? NewsScreenState.Content)?.newsItems.orEmpty()
                // distinctBy also guards a single page's own results: NewsAPI
                // can return the same article twice in one response (e.g. a
                // syndicated repost under two source aliases), which would
                // otherwise crash NewsListView's key-by-articleUrl LazyColumn.
                val mergedItems = mergePagedItems(existingItems, result.newsItems, page) { it.articleUrl }
                        .distinctBy { it.articleUrl }
                val endReached = isLastPage(page, result.totalPages)
                if (mergedItems.isEmpty() && !endReached) {
                    // Every article on this page got dropped by the
                    // repository's title-keyword filter, but more pages
                    // exist - keep paging forward instead of surfacing a
                    // premature Empty that would hide real matches on a
                    // later page (and that retry() could never recover from,
                    // since it always restarts at page 1).
                    loadNextPageJob?.cancel()
                    loadNextPageJob = viewModelScope.launch {
                        applyResult(newsRepository.getNews(page + 1), page + 1)
                    }
                    return
                }
                _screenState.value = if (mergedItems.isEmpty()) {
                    NewsScreenState.Empty
                } else {
                    NewsScreenState.Content(
                            newsItems = mergedItems,
                            currentPage = page,
                            endReached = endReached,
                            isLoadingNextPage = false
                                           )
                }
            }

            is NewsResult.Failure -> {
                if (page <= 1) {
                    _screenState.value = NewsScreenState.Error(result.errorMessage)
                    return
                }
                val currentState = _screenState.value
                if (currentState is NewsScreenState.Content) {
                    _screenState.value = currentState.copy(isLoadingNextPage = false)
                }
            }
        }
    }
}

sealed interface NewsScreenState {
    data object Loading : NewsScreenState
    data object Empty : NewsScreenState
    data class Content(
            val newsItems: List<NewsItem>,
            val currentPage: Int = 1,
            val endReached: Boolean = false,
            val isLoadingNextPage: Boolean = false
                       ) : NewsScreenState
    data class Error(val errorMessage: String) : NewsScreenState
}
