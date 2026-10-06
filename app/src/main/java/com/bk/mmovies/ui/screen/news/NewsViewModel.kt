package com.bk.mmovies.ui.screen.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bk.mmovies.connectivity.InternetMonitor
import com.bk.mmovies.domain.model.NewsItem
import com.bk.mmovies.domain.model.canLoadNextPage
import com.bk.mmovies.domain.model.isLastPage
import com.bk.mmovies.domain.model.mergePagedItems
import com.bk.mmovies.domain.model.result.NewsResult
import com.bk.mmovies.domain.repository.NewsRepository
import com.bk.mmovies.locale.LocaleMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
        private val newsRepository: NewsRepository,
        localeMonitor: LocaleMonitor,
        internetMonitor: InternetMonitor
                                        ) : ViewModel() {

    private val _screenState = MutableStateFlow<NewsScreenState>(NewsScreenState.Loading)
    val screenState: StateFlow<NewsScreenState> = _screenState.asStateFlow()

    private val _invalidApiKeyToastEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val invalidApiKeyToastEvent: SharedFlow<Unit> = _invalidApiKeyToastEvent.asSharedFlow()

    private var loadNewsJob: Job? = null
    private var loadNextPageJob: Job? = null
    private var retryWhenOnline = false

    init {
        loadNews()
        // NewsAPI results are requested per app language, so a language
        // change reloads the feed (shimmer, then new-language articles)
        // instead of leaving the old-language list until the app restarts.
        // Waits for connectivity since a locale change can land mid-reconnect.
        viewModelScope.launch {
            localeMonitor.currentLanguage
                    .drop(1)
                    .collectLatest {
                        internetMonitor.isInternetAvailable.first { it }
                        // Right after a locale change DNS can still be down
                        // even though the connectivity flag already says
                        // "available" (and it then never flips again), so
                        // retry connectivity failures a few times here.
                        repeat(LANGUAGE_CHANGE_MAX_ATTEMPTS) { attempt ->
                            if (attempt > 0) delay(LANGUAGE_CHANGE_RETRY_DELAY_MS)
                            loadNews()
                            loadNewsJob?.join()
                            if (!retryWhenOnline) return@collectLatest
                        }
                    }
        }
        // The check above can pass on a stale "available" flag while DNS is
        // still down right after a locale change, so a page-1 load that failed
        // for connectivity reasons is retried once the network comes (back).
        viewModelScope.launch {
            internetMonitor.isInternetAvailable.collectLatest { isAvailable ->
                if (isAvailable && retryWhenOnline) loadNews()
            }
        }
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

    /**
     * Stores [apiKey] and loads page 1 with it. A key NewsAPI rejects is
     * discarded again (see [applyResult]) so it can't keep failing every later
     * load, and the key input comes back with an "invalid" toast.
     */
    fun saveApiKey(apiKey: String) {
        newsRepository.saveSharedPrefApiKey(apiKey)
        loadNews()
    }

    private fun loadNews() {
        loadNextPageJob?.cancel()
        loadNewsJob?.cancel()
        retryWhenOnline = false
        loadNewsJob =viewModelScope.launch {
            if (!newsRepository.hasApiKey()) {
                _screenState.value = NewsScreenState.NeedsApiKey(missingKey = true)
                return@launch
            }
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
                if (page <= 1 && result.isInvalidApiKey) {
                    newsRepository.removeSharedPrefApiKey()
                    _invalidApiKeyToastEvent.tryEmit(Unit)
                    _screenState.value = NewsScreenState.NeedsApiKey(missingKey = false)
                    return
                }
                if (page <= 1) {
                    retryWhenOnline = result.isConnectivityFailure
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

private const val LANGUAGE_CHANGE_MAX_ATTEMPTS = 5
private const val LANGUAGE_CHANGE_RETRY_DELAY_MS = 2_000L

sealed interface NewsScreenState {
    data object Loading : NewsScreenState
    data object Empty : NewsScreenState
    // missingKey = false means a key was entered/baked in but NewsAPI rejected it.
    data class NeedsApiKey(val missingKey: Boolean) : NewsScreenState
    data class Content(
            val newsItems: List<NewsItem>,
            val currentPage: Int = 1,
            val endReached: Boolean = false,
            val isLoadingNextPage: Boolean = false
                       ) : NewsScreenState
    data class Error(val errorMessage: String) : NewsScreenState
}
