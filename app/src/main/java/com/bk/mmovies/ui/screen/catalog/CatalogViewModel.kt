package com.bk.mmovies.ui.screen.catalog

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bk.mmovies.R
import com.bk.mmovies.connectivity.InternetMonitor
import com.bk.mmovies.data.mapper.CatalogItemMapper
import com.bk.mmovies.domain.model.CatalogItem
import com.bk.mmovies.domain.model.Category
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.domain.model.TvSeriesCategory
import com.bk.mmovies.domain.model.UnknownCategory
import com.bk.mmovies.domain.model.canLoadNextPage
import com.bk.mmovies.domain.model.isLastPage
import com.bk.mmovies.domain.model.mergePagedItems
import com.bk.mmovies.domain.model.result.AccountDetailsResult
import com.bk.mmovies.domain.model.result.MoviesResult
import com.bk.mmovies.domain.model.result.ToggleFavoriteResult
import com.bk.mmovies.domain.model.result.TvSeriesResult
import com.bk.mmovies.domain.repository.AuthenticationRepository
import com.bk.mmovies.domain.repository.MovieRepository
import com.bk.mmovies.domain.repository.TvSeriesRepository
import com.bk.mmovies.locale.LocaleMonitor
import com.bk.mmovies.ui.screen.catalog.screencomponents.CatalogBottomTab
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
        private val movieRepository: MovieRepository,
        private val tvRepository: TvSeriesRepository,
        private val authenticationRepository: AuthenticationRepository,
        private val catalogItemMapper: CatalogItemMapper,
        private val savedStateHandle: SavedStateHandle,
        @ApplicationContext private val context: Context,
        localeMonitor: LocaleMonitor,
        internetMonitor: InternetMonitor
                                          ) : ViewModel() {

    private val unknownCategoryMessage: String
        get() = context.getString(R.string.generic_error_message)

    /** Screen state for the currently selected tab/category. */
    private val _catalogScreenState =
            MutableStateFlow<CatalogScreenState>(CatalogScreenState.Loading)
    val catalogScreenState: StateFlow<CatalogScreenState> = _catalogScreenState.asStateFlow()

    // Persisted through SavedStateHandle rather than held as plain ViewModel
    // state: a ViewModel survives configuration changes but not process death,
    // and this selection used to live in rememberSaveable, which survived both.
    private val restoredCategory: Category =
            savedStateHandle.get<Int>(KEY_SELECTED_CATEGORY_ID)
                    ?.let { Category.fromCategoryId(it) }
                    ?.takeIf { it !is UnknownCategory }
                    ?: MovieCategory.PopularMovieCategory

    private val _selectedTab = MutableStateFlow(
            savedStateHandle.get<String>(KEY_SELECTED_TAB)
                    ?.let { saved -> CatalogBottomTab.entries.firstOrNull { it.name == saved } }
            ?: CatalogBottomTab.Movies
                                               )
    val selectedTab: StateFlow<CatalogBottomTab> = _selectedTab.asStateFlow()

    private val _selectedCategory = MutableStateFlow(restoredCategory)
    val selectedCategory: StateFlow<Category> = _selectedCategory.asStateFlow()

    // Each tab keeps its own last-selected category, so switching tabs and
    // back doesn't lose where the user was browsing. Only the restored tab's
    // category is known after process death; the other falls back to Popular.
    private var lastMovieCategory: MovieCategory =
            restoredCategory as? MovieCategory ?: MovieCategory.PopularMovieCategory
    private var lastTvCategory: TvSeriesCategory =
            restoredCategory as? TvSeriesCategory ?: TvSeriesCategory.PopularTvSeriesCategory

    private val _userProfileState = MutableStateFlow(UserProfileUiState())
    val userProfileState: StateFlow<UserProfileUiState> = _userProfileState.asStateFlow()

    private val _goToDetailsNavEvent: MutableSharedFlow<Pair<Int, Category>> =
            MutableSharedFlow(extraBufferCapacity = 1)
    val goToDetailsNavEvent: SharedFlow<Pair<Int, Category>> =
            _goToDetailsNavEvent.asSharedFlow()

    private val _goToAuthNavEvent: MutableSharedFlow<Unit> =
            MutableSharedFlow(extraBufferCapacity = 1)
    val goToAuthNavEvent: SharedFlow<Unit> = _goToAuthNavEvent.asSharedFlow()

    private var loadCatalogItemsJob: Job? = null
    private var loadNextPageJob: Job? = null

    init {
        startLoad(_selectedCategory.value)
        loadUserProfile()

        // Reload the current category in the new language whenever the
        // device/app language changes, including while the app is backgrounded.
        // A locale change often lands right as the OS is mid-reconnect (seen as
        // an immediate UnknownHostException), so wait for connectivity before firing.
        viewModelScope.launch {
            localeMonitor.currentLanguage
                    .drop(1)
                    .collectLatest {
                        internetMonitor.isInternetAvailable.first { it }
                        startLoad(_selectedCategory.value)
                    }
        }
    }

    private fun loadUserProfile() {
        val loginSessionId = authenticationRepository.getSharedPrefLoginSessionId()
        if (loginSessionId == null) {
            _userProfileState.value = UserProfileUiState(isGuest = true)
            return
        }
        viewModelScope.launch {
            when (val result = authenticationRepository.getAccountDetailsResult(loginSessionId)) {
                is AccountDetailsResult.Success -> {
                    // Cached here (rather than re-fetched) so any screen that
                    // needs it for a favorite call can read it synchronously.
                    authenticationRepository.saveSharedPrefAccountId(result.accountId)
                    _userProfileState.value = UserProfileUiState(
                            name = result.name,
                            imageUrl = result.avatarUrl,
                            isGuest = false
                                                                )
                    // Refreshes the local favorite-id caches the repositories
                    // cross-reference when building category lists, then
                    // reloads the current category so it picks up correct stars.
                    movieRepository.syncFavoriteIds(
                            result.accountId,
                            loginSessionId
                                                   )
                    tvRepository.syncFavoriteIds(
                            result.accountId,
                            loginSessionId
                                                 )
                    startLoad(_selectedCategory.value)
                }
                is AccountDetailsResult.Failure -> {
                    _userProfileState.value = UserProfileUiState(isGuest = false)
                }
            }
        }
    }

    fun onLogoutClicked() {
        viewModelScope.launch {
            authenticationRepository.logout()
            _goToAuthNavEvent.tryEmit(Unit)
        }
    }

    fun onTabSelected(tab: CatalogBottomTab) {
        if (tab == _selectedTab.value) return
        _selectedTab.value = tab
        savedStateHandle[KEY_SELECTED_TAB] = tab.name
        val category: Category = when (tab) {
            CatalogBottomTab.Movies   -> lastMovieCategory
            CatalogBottomTab.TvSeries -> lastTvCategory
        }
        setSelectedCategory(category)
        startLoad(category)
    }

    fun handleCategorySelected(category: Category) {
        if (category == _selectedCategory.value) return
        when (category) {
            is MovieCategory    -> lastMovieCategory = category
            is TvSeriesCategory -> lastTvCategory = category
            // Never produced by the category picker; only reachable via a
            // stale/unrecognized categoryId, in which case there's nothing
            // to remember and no list to load.
            is UnknownCategory  -> return
        }
        setSelectedCategory(category)
        startLoad(category)
    }

    private fun setSelectedCategory(category: Category) {
        _selectedCategory.value = category
        savedStateHandle[KEY_SELECTED_CATEGORY_ID] = category.categoryId
    }

    /**
     * Re-runs the current category. Without this the error screen's "Try again"
     * button had nothing to call and the only way out of a failure was to
     * switch category and switch back.
     */
    fun retry() {
        startLoad(_selectedCategory.value)
    }

    private fun startLoad(category: Category) {
        _catalogScreenState.value = CatalogScreenState.Loading
        loadNextPageJob?.cancel()
        loadCatalogItemsJob?.cancel()
        loadCatalogItemsJob = viewModelScope.launch {
            when (category) {
                is MovieCategory    -> loadMovies(category, page = 1)
                is TvSeriesCategory -> loadTvSeries(category, page = 1)
                // Unreachable today, but leaving the state on Loading would
                // strand the screen on the skeleton with no retry affordance.
                else                -> _catalogScreenState.value =
                        CatalogScreenState.Error(unknownCategoryMessage)
            }
        }
    }

    /**
     * Fetches the page after the one currently shown and appends it to the
     * list, triggered by the list scrolling near its end. A no-op while
     * already loading or once the category's last page has been reached.
     */
    fun loadNextPage() {
        val currentState = _catalogScreenState.value
        if (currentState !is CatalogScreenState.Content) return
        if (!canLoadNextPage(currentState.endReached, currentState.isLoadingNextPage)) return
        val nextPage = currentState.currentPage + 1
        _catalogScreenState.value = currentState.copy(isLoadingNextPage = true)
        loadNextPageJob?.cancel()
        loadNextPageJob = viewModelScope.launch {
            when (val category = _selectedCategory.value) {
                is MovieCategory    -> loadMovies(category, nextPage)
                is TvSeriesCategory -> loadTvSeries(category, nextPage)
                // No list is shown for this category, so there is nothing to page.
                else                -> Unit
            }
        }
    }

    private suspend fun loadMovies(category: MovieCategory, page: Int) {
        if (category == MovieCategory.FavoritesMovieCategory) {
            loadFavoriteMovies(page)
            return
        }
        applyMoviesResult(movieRepository.getMoviesByCategory(category, page), page)
    }

    private suspend fun loadFavoriteMovies(page: Int) {
        val sessionId = authenticationRepository.getSharedPrefLoginSessionId()
        val accountId = authenticationRepository.getSharedPrefAccountId()
        // Guests have no login session, and TMDB's favorite endpoints require
        // one — never call out, just prompt for login instead.
        if (sessionId == null || accountId == null) {
            _catalogScreenState.value = CatalogScreenState.FavoritesLoginRequired
            return
        }
        applyMoviesResult(movieRepository.getFavoriteMovies(accountId, sessionId, page), page)
    }

    private fun applyMoviesResult(result: MoviesResult, requestedPage: Int) {
        when (result) {
            is MoviesResult.Success -> applyCatalogItems(
                    catalogItemMapper.fromMovies(result.movies), result.page, result.totalPages)
            is MoviesResult.Failure -> applyLoadFailure(requestedPage, result.errorMessage)
        }
    }

    private suspend fun loadTvSeries(category: TvSeriesCategory, page: Int) {
        if (category == TvSeriesCategory.FavoritesTvSeriesCategory) {
            loadFavoriteTvSeries(page)
            return
        }
        applyTvSeriesResult(tvRepository.getTvSeriesByCategory(category, page), page)
    }

    private suspend fun loadFavoriteTvSeries(page: Int) {
        val sessionId = authenticationRepository.getSharedPrefLoginSessionId()
        val accountId = authenticationRepository.getSharedPrefAccountId()
        // Guests have no login session, and TMDB's favorite endpoints require
        // one — never call out, just prompt for login instead.
        if (sessionId == null || accountId == null) {
            _catalogScreenState.value = CatalogScreenState.FavoritesLoginRequired
            return
        }
        applyTvSeriesResult(tvRepository.getFavoriteTvSeries(accountId, sessionId, page), page)
    }

    private fun applyTvSeriesResult(result: TvSeriesResult, requestedPage: Int) {
        when (result) {
            is TvSeriesResult.Success -> applyCatalogItems(
                    catalogItemMapper.fromTvSeries(result.tvSeries), result.page, result.totalPages)
            is TvSeriesResult.Failure -> applyLoadFailure(requestedPage, result.errorMessage)
        }
    }

    // "Nothing here" and "the load failed" are different things and deserve
    // different screens. Page 1 replaces the list; later pages append to
    // whatever is already showing.
    private fun applyCatalogItems(items: List<CatalogItem>, page: Int, totalPages: Int) {
        val currentState = _catalogScreenState.value
        val existingItems = (currentState as? CatalogScreenState.Content)?.catalogItems.orEmpty()
        val mergedItems = mergePagedItems(existingItems, items, page) { it.id }
        // Each page arrives pre-sorted by release date on its own (see
        // MovieRepositoryImpl/TvSeriesRepositoryImpl), which only orders
        // within that page — re-sort the merged list so date order holds
        // across page boundaries too.
        val isUpcoming = _selectedCategory.value == MovieCategory.UpcomingMovieCategory ||
                _selectedCategory.value == TvSeriesCategory.UpcomingTvSeriesCategory
        val orderedItems = if (isUpcoming) mergedItems.sortedBy { it.releaseDate } else mergedItems
        _catalogScreenState.value = if (orderedItems.isEmpty()) {
            CatalogScreenState.Empty
        } else {
            CatalogScreenState.Content(
                    catalogItems = orderedItems,
                    currentPage = page,
                    endReached = isLastPage(page, totalPages),
                    isLoadingNextPage = false
                                       )
        }
    }

    // A failed first page is a real error screen; a failed next page just
    // stops the footer spinner so scrolling back near the end retries it.
    private fun applyLoadFailure(requestedPage: Int, errorMessage: String) {
        if (requestedPage <= 1) {
            _catalogScreenState.value = CatalogScreenState.Error(errorMessage)
            return
        }
        val currentState = _catalogScreenState.value
        if (currentState is CatalogScreenState.Content) {
            _catalogScreenState.value = currentState.copy(isLoadingNextPage = false)
        }
    }

    fun handleCatalogItemClicked(itemId: Int) {
        _goToDetailsNavEvent.tryEmit(itemId to _selectedCategory.value)
    }

    fun onFavoriteClicked(catalogItem: CatalogItem) {
        val sessionId = authenticationRepository.getSharedPrefLoginSessionId() ?: return
        val accountId = authenticationRepository.getSharedPrefAccountId() ?: return
        val newIsFavorite = !catalogItem.isFavorite
        val isMoviesTab = _selectedTab.value == CatalogBottomTab.Movies

        // Optimistic: flip the star immediately, revert only if the call fails.
        // The repository persists the toggle to the local favorite-id cache too.
        updateCatalogItemFavoriteState(catalogItem.id, newIsFavorite)
        viewModelScope.launch {
            val result = if (isMoviesTab) {
                movieRepository.toggleFavorite(accountId, sessionId, catalogItem.id, newIsFavorite)
            } else {
                tvRepository.toggleFavorite(accountId, sessionId, catalogItem.id, newIsFavorite)
            }
            if (result is ToggleFavoriteResult.Failure) {
                updateCatalogItemFavoriteState(catalogItem.id, catalogItem.isFavorite)
            }
        }
    }

    /**
     * Re-syncs the displayed list's favorite stars against the local cache.
     * Toggling favorite from the Details screen updates the same Room cache
     * but not this ViewModel's in-memory state, so without this the Catalog
     * screen kept showing stale stars after navigating back from Details.
     */
    fun refreshFavoriteMarkers() {
        if (_catalogScreenState.value !is CatalogScreenState.Content) return
        val isMoviesTab = _selectedTab.value == CatalogBottomTab.Movies
        viewModelScope.launch {
            val favoriteIds = if (isMoviesTab) {
                movieRepository.getCachedFavoriteIds()
            } else {
                tvRepository.getCachedFavoriteIds()
            }
            // Re-read the state instead of relying on what was captured
            // before the suspending lookup above — a loadNextPage() can
            // complete while this is in flight, and writing back a snapshot
            // taken before that would silently discard the appended page.
            val latestState = _catalogScreenState.value
            if (latestState !is CatalogScreenState.Content) return@launch
            val isFavoritesCategory = _selectedCategory.value == MovieCategory.FavoritesMovieCategory ||
                    _selectedCategory.value == TvSeriesCategory.FavoritesTvSeriesCategory
            val items = if (isFavoritesCategory) {
                // An item unfavorited elsewhere no longer belongs in this list at all.
                latestState.catalogItems.filter { favoriteIds.contains(it.id) }
            } else {
                latestState.catalogItems.map { it.copy(isFavorite = favoriteIds.contains(it.id)) }
            }
            // Re-marking stars on an already-loaded list, not a new page fetch
            // — keep the existing pagination state (page/endReached) intact.
            _catalogScreenState.value = if (items.isEmpty()) {
                CatalogScreenState.Empty
            } else {
                latestState.copy(catalogItems = items)
            }
        }
    }

    private fun updateCatalogItemFavoriteState(itemId: Int, isFavorite: Boolean) {
        val currentState = _catalogScreenState.value
        if (currentState is CatalogScreenState.Content) {
            _catalogScreenState.value = currentState.copy(
                    catalogItems = currentState.catalogItems.map { item ->
                        if (item.id == itemId) item.copy(isFavorite = isFavorite) else item
                    }
                                                          )
        }
    }
}

private const val KEY_SELECTED_TAB = "catalog_selected_tab"
private const val KEY_SELECTED_CATEGORY_ID = "catalog_selected_category_id"

sealed interface CatalogScreenState {
    object Loading : CatalogScreenState
    object Empty : CatalogScreenState
    object FavoritesLoginRequired : CatalogScreenState
    data class Content(
            val catalogItems: List<CatalogItem>,
            val currentPage: Int = 1,
            val endReached: Boolean = false,
            val isLoadingNextPage: Boolean = false
                       ) : CatalogScreenState
    data class Error(val errorMessage: String) : CatalogScreenState
}

data class UserProfileUiState(
        val name: String = "",
        val imageUrl: String = "",
        val isGuest: Boolean = true
                             )
