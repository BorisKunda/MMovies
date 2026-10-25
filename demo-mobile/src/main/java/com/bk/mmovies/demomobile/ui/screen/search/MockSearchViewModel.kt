package com.bk.mmovies.demomobile.ui.screen.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.bk.mmovies.demomobile.ui.screen.catalog.mockMovieCatalogItems
import com.bk.mmovies.demomobile.ui.screen.catalog.mockTvSeriesCatalogItems
import com.bk.mmovies.demomobile.ui.screen.details.mockCatalogItemsDetails
import com.bk.mmovies.domain.model.CastMemberModel
import com.bk.mmovies.domain.model.CatalogItem
import com.bk.mmovies.domain.model.SearchResultMediaType
import com.bk.mmovies.domain.model.SearchResultModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

private const val MAX_RECENT_SEARCHES = 10
private const val MAX_KNOWN_FOR_TITLES = 3

sealed interface MockSearchResultsState {
    object Idle : MockSearchResultsState
    object Empty : MockSearchResultsState
    data class Content(val results: List<SearchResultModel>) : MockSearchResultsState
}

class MockSearchViewModel(application: Application) : AndroidViewModel(application) {

    private val searchableResults: List<SearchResultModel> = buildSearchableResults()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _resultsState = MutableStateFlow<MockSearchResultsState>(MockSearchResultsState.Idle)
    val resultsState: StateFlow<MockSearchResultsState> = _resultsState.asStateFlow()

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

    private val _selectedFilters = MutableStateFlow<Set<SearchResultMediaType>>(emptySet())
    val selectedFilters: StateFlow<Set<SearchResultMediaType>> = _selectedFilters.asStateFlow()

    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery
        _resultsState.value = searchFor(newQuery.trim())
    }

    fun onFilterToggled(mediaType: SearchResultMediaType) {
        _selectedFilters.value = _selectedFilters.value.let { current ->
            if (mediaType in current) current - mediaType else current + mediaType
        }
    }

    fun onSearchSubmitted() {
        val trimmedQuery = _query.value.trim()
        if (trimmedQuery.isEmpty()) return
        _recentSearches.value = (listOf(trimmedQuery) + _recentSearches.value.filterNot {
            it.equals(trimmedQuery, ignoreCase = true)
        }).take(MAX_RECENT_SEARCHES)
        _resultsState.value = searchFor(trimmedQuery)
    }

    fun onRecentSearchClicked(recentQuery: String) {
        _query.value = recentQuery
        onSearchSubmitted()
    }

    fun onRemoveRecentSearchClicked(recentQuery: String) {
        _recentSearches.value = _recentSearches.value - recentQuery
    }

    fun onClearRecentSearchesClicked() {
        _recentSearches.value = emptyList()
    }

    private val _goToMovieDetailsNavEvent = MutableSharedFlow<Int>(extraBufferCapacity = 1)
    val goToMovieDetailsNavEvent: SharedFlow<Int> = _goToMovieDetailsNavEvent.asSharedFlow()

    private val _goToTvSeriesDetailsNavEvent = MutableSharedFlow<Int>(extraBufferCapacity = 1)
    val goToTvSeriesDetailsNavEvent: SharedFlow<Int> = _goToTvSeriesDetailsNavEvent.asSharedFlow()

    fun onResultClicked(result: SearchResultModel) {
        when (result.mediaType) {
            SearchResultMediaType.MOVIE     -> _goToMovieDetailsNavEvent.tryEmit(result.id)
            SearchResultMediaType.TV_SERIES -> _goToTvSeriesDetailsNavEvent.tryEmit(result.id)
            SearchResultMediaType.PERSON    -> Unit
        }
    }

    private fun searchFor(trimmedQuery: String): MockSearchResultsState {
        if (trimmedQuery.isEmpty()) return MockSearchResultsState.Idle
        val matches = searchableResults.filter { it.title.contains(trimmedQuery, ignoreCase = true) }
        return if (matches.isEmpty()) MockSearchResultsState.Empty else MockSearchResultsState.Content(matches)
    }

    private fun buildSearchableResults(): List<SearchResultModel> {
        val movieResults = mockMovieCatalogItems.map { it.toSearchResult(SearchResultMediaType.MOVIE) }
        val seriesResults = mockTvSeriesCatalogItems.map { it.toSearchResult(SearchResultMediaType.TV_SERIES) }

        val movieTitlesByPersonId = linkedMapOf<Int, MutableList<String>>()
        val castMembersByPersonId = linkedMapOf<Int, CastMemberModel>()
        mockCatalogItemsDetails(getApplication<Application>().resources).forEach { details ->
            val people = details.cast + listOfNotNull(details.director) + details.writers
            people.forEach { person ->
                castMembersByPersonId[person.id] = person
                movieTitlesByPersonId.getOrPut(person.id) { mutableListOf() }.add(details.title)
            }
        }
        val peopleResults = castMembersByPersonId.map { (personId, castMember) ->
            SearchResultModel(
                    id = personId,
                    mediaType = SearchResultMediaType.PERSON,
                    title = castMember.name,
                    imageUrl = castMember.profileUrl,
                    subtitle = movieTitlesByPersonId.getValue(personId).distinct().take(MAX_KNOWN_FOR_TITLES).joinToString()
                             )
        }

        return movieResults + seriesResults + peopleResults
    }

    private fun CatalogItem.toSearchResult(mediaType: SearchResultMediaType) = SearchResultModel(
            id = id,
            mediaType = mediaType,
            title = title,
            imageUrl = imageUrl,
            backdropUrl = backdropUrl,
            subtitle = releaseDate,
            rating = rating
                                                                                               )
}
