package com.bk.mmovies.ui.screen.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.domain.model.SearchResultMediaType
import com.bk.mmovies.domain.model.SearchResultModel
import com.bk.mmovies.domain.model.result.SearchResult
import com.bk.mmovies.domain.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Long enough that typing a whole word doesn't fire a call per keystroke,
// short enough that suggestions still feel live.
private const val SEARCH_DEBOUNCE_MILLIS = 400L

@HiltViewModel
class SearchViewModel @Inject constructor(
        private val searchRepository: SearchRepository
                                          ) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _resultsState = MutableStateFlow<SearchResultsUiState>(SearchResultsUiState.Idle)
    val resultsState: StateFlow<SearchResultsUiState> = _resultsState.asStateFlow()

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

    // Empty set means no filter applied (all media types shown).
    private val _selectedFilters = MutableStateFlow<Set<SearchResultMediaType>>(emptySet())
    val selectedFilters: StateFlow<Set<SearchResultMediaType>> = _selectedFilters.asStateFlow()

    private val _goToMovieDetailsNavEvent: MutableSharedFlow<Pair<Int, MovieCategory>> =
            MutableSharedFlow(extraBufferCapacity = 1)
    val goToMovieDetailsNavEvent: SharedFlow<Pair<Int, MovieCategory>> = _goToMovieDetailsNavEvent.asSharedFlow()

    private val _goToTvSeriesDetailsNavEvent: MutableSharedFlow<Int> = MutableSharedFlow(extraBufferCapacity = 1)
    val goToTvSeriesDetailsNavEvent: SharedFlow<Int> = _goToTvSeriesDetailsNavEvent.asSharedFlow()

    private var searchJob: Job? = null
    private var debounceJob: Job? = null

    init {
        loadRecentSearches()
    }

    private fun loadRecentSearches() {
        viewModelScope.launch {
            _recentSearches.value = searchRepository.getRecentSearches()
        }
    }

    // Debounced on a job we own (rather than a collectLatest on _query) so
    // an immediate search — onSearchSubmitted/onRecentSearchClicked/retry —
    // can cancel the pending delay instead of racing it.
    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery
        debounceJob?.cancel()
        val trimmedQuery = newQuery.trim()
        if (trimmedQuery.isEmpty()) {
            _resultsState.value = SearchResultsUiState.Idle
            return
        }
        debounceJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MILLIS)
            runSearch(trimmedQuery)
        }
    }

    // Toggles one media type in/out of the filter set; an empty set means
    // "all types shown".
    fun onFilterToggled(mediaType: SearchResultMediaType) {
        _selectedFilters.value = _selectedFilters.value.let { current ->
            if (mediaType in current) current - mediaType else current + mediaType
        }
    }

    fun onSearchSubmitted() {
        val trimmedQuery = _query.value.trim()
        if (trimmedQuery.isEmpty()) return
        viewModelScope.launch {
            searchRepository.addRecentSearch(trimmedQuery)
            loadRecentSearches()
        }
        runSearch(trimmedQuery)
    }

    fun onRecentSearchClicked(recentQuery: String) {
        _query.value = recentQuery
        onSearchSubmitted()
    }

    fun onRemoveRecentSearchClicked(recentQuery: String) {
        viewModelScope.launch {
            searchRepository.removeRecentSearch(recentQuery)
            loadRecentSearches()
        }
    }

    fun onClearRecentSearchesClicked() {
        viewModelScope.launch {
            searchRepository.clearRecentSearches()
            loadRecentSearches()
        }
    }

    // Movie/TV results navigate out; a person result has nowhere of its own
    // to navigate to, so the screen shows it in the existing ActorDetailsDialog.
    fun onResultClicked(result: SearchResultModel) {
        val trimmedQuery = _query.value.trim()
        if (trimmedQuery.isNotEmpty()) {
            viewModelScope.launch {
                searchRepository.addRecentSearch(trimmedQuery)
                loadRecentSearches()
            }
        }
        when (result.mediaType) {
            SearchResultMediaType.MOVIE     -> {
                val category = if (result.isUpcoming) {
                    MovieCategory.UpcomingMovieCategory
                } else {
                    MovieCategory.PopularMovieCategory
                }
                _goToMovieDetailsNavEvent.tryEmit(result.id to category)
            }
            SearchResultMediaType.TV_SERIES -> _goToTvSeriesDetailsNavEvent.tryEmit(result.id)
            SearchResultMediaType.PERSON    -> Unit
        }
    }

    fun retry() {
        val trimmedQuery = _query.value.trim()
        if (trimmedQuery.isEmpty()) return
        runSearch(trimmedQuery)
    }

    private fun runSearch(query: String) {
        debounceJob?.cancel()
        _resultsState.value = SearchResultsUiState.Loading
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            when (val result = searchRepository.search(query)) {
                is SearchResult.Success -> {
                    _resultsState.value = if (result.results.isEmpty()) {
                        SearchResultsUiState.Empty
                    } else {
                        SearchResultsUiState.Content(result.results)
                    }
                }
                is SearchResult.Failure -> {
                    _resultsState.value = SearchResultsUiState.Error(result.errorMessage)
                }
            }
        }
    }
}

sealed interface SearchResultsUiState {
    object Idle : SearchResultsUiState
    object Loading : SearchResultsUiState
    object Empty : SearchResultsUiState
    data class Content(val results: List<SearchResultModel>) : SearchResultsUiState
    data class Error(val errorMessage: String) : SearchResultsUiState
}
