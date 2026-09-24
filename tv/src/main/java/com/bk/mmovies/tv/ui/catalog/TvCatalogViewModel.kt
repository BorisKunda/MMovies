package com.bk.mmovies.tv.ui.catalog

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Info
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bk.mmovies.core.R as CoreR
import com.bk.mmovies.data.mapper.CatalogItemMapper
import com.bk.mmovies.domain.model.CatalogItem
import com.bk.mmovies.domain.model.CatalogMediaType
import com.bk.mmovies.domain.model.Category
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.domain.model.NewsItem
import com.bk.mmovies.domain.model.RecommendationSuggestion
import com.bk.mmovies.domain.model.SearchResultMediaType
import com.bk.mmovies.domain.model.SearchResultModel
import com.bk.mmovies.domain.model.TvSeriesCategory
import com.bk.mmovies.domain.model.canLoadNextPage
import com.bk.mmovies.domain.model.isLastPage
import com.bk.mmovies.domain.model.mergePagedItems
import com.bk.mmovies.domain.model.result.AccountDetailsResult
import com.bk.mmovies.domain.model.result.GuestSessionIdResult
import com.bk.mmovies.domain.model.result.MoviesResult
import com.bk.mmovies.domain.model.result.NewsResult
import com.bk.mmovies.domain.model.result.RecommendationResult
import com.bk.mmovies.domain.model.result.SearchResult
import com.bk.mmovies.domain.model.result.ToggleFavoriteResult
import com.bk.mmovies.domain.model.result.TvSeriesResult
import com.bk.mmovies.domain.repository.AiRecommendationRepository
import com.bk.mmovies.domain.repository.AuthenticationRepository
import com.bk.mmovies.domain.repository.MovieRepository
import com.bk.mmovies.domain.repository.NewsRepository
import com.bk.mmovies.domain.repository.SearchRepository
import com.bk.mmovies.domain.repository.TvSeriesRepository
import com.bk.mmovies.tv.BuildConfig
import com.bk.mmovies.tv.R
import com.bk.mmovies.tv.ui.catalog.TvCatalogScreenState.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Either a Material ImageVector (the original nav items) or a raster drawable
// (ic_ai, supplied as a PNG rather than a vector) - CatalogNavRailItem
// branches on this to render either kind through the same Icon() call site.
sealed interface TvNavIcon {
    data class Vector(val imageVector: ImageVector) : TvNavIcon
    data class Drawable(@DrawableRes val resId: Int) : TvNavIcon
}

data class TvUserProfileUiState(
        val name: String = "",
        val imageUrl: String = "",
        val isGuest: Boolean = true
                                )

// Home and Settings are nav-rail chrome only (no browsable catalog content
// behind them), so they're deliberately left out of this destination set.
// Recommendations sits last (below Search per the design), and unlike every
// other destination has no real backing content yet - see
// TvCatalogScreenState.Recommendations.
enum class TvCatalogDestination(@StringRes val labelResId: Int, val icon: TvNavIcon) {
    Movies(R.string.nav_movies, TvNavIcon.Vector(Icons.Default.Movie)),
    TvSeries(R.string.nav_tv_series, TvNavIcon.Vector(Icons.Default.LiveTv)),
    Search(R.string.nav_search, TvNavIcon.Vector(Icons.Default.Search)),
    Favorites(R.string.nav_favorites, TvNavIcon.Vector(Icons.Default.FavoriteBorder)),
    News(R.string.nav_news, TvNavIcon.Vector(Icons.AutoMirrored.Filled.Article)),
    Recommendations(R.string.nav_recommendations, TvNavIcon.Drawable(CoreR.drawable.ic_ai)),
    About(R.string.nav_about, TvNavIcon.Vector(Icons.Outlined.Info))
}

data class TvCategoryRowContent(
        val category: Category,
        val items: List<CatalogItem>,
        val currentPage: Int = 1,
        val endReached: Boolean = false,
        val isLoadingNextPage: Boolean = false
                                )

sealed interface TvCatalogScreenState {
    // focusedItem drives the hero preview banner: whichever item the D-pad
    // is currently sitting on in any row, so it stays real content-backed
    // instead of static mock text.
    data class Movies(
            val rows: List<TvCategoryRowContent>,
            val focusedItem: CatalogItem? = null,
            val focusedCategory: Category? = null
                      ) : TvCatalogScreenState

    data class TvSeries(
            val rows: List<TvCategoryRowContent>,
            val focusedItem: CatalogItem? = null,
            val focusedCategory: Category? = null
                        ) : TvCatalogScreenState

    // loginRequired mirrors the phone app's FavoritesLoginRequired state:
    // TMDB favorites need a real logged-in session (guest sessions can only
    // rate, not favorite), and the tv app has no login flow wired yet.
    data class Favorites(
            val rows: List<TvCategoryRowContent> = emptyList(),
            val loginRequired: Boolean = false,
            val isLoading: Boolean = false,
            // The item whose preview popup is open (like Movies/TvSeries).
            val focusedItem: CatalogItem? = null
                         ) : TvCatalogScreenState

    data class News(
            val items: List<NewsItem> = emptyList(),
            val keySetupRequired: Boolean = false,
            val currentPage: Int = 1,
            val endReached: Boolean = false,
            val isLoadingNextPage: Boolean = false,
            // First page in flight (as opposed to isLoadingNextPage) - the
            // screen shows a row shimmer instead of a stale/empty row.
            val isLoading: Boolean = false
                    ) : TvCatalogScreenState

    data class Search(
            val query: String = "",
            val items: List<CatalogItem> = emptyList(),
            // A non-blank query's results are still being fetched (including
            // the debounce wait) - the results row shows a shimmer meanwhile.
            val isSearching: Boolean = false,
            // The result whose preview popup is open (like Movies/TvSeries).
            val focusedItem: CatalogItem? = null,
            // Shown when query is blank - mirrors :app's SearchViewModel,
            // just without its per-item remove affordance (see
            // onRecentSearchClicked's doc for why).
            val recentSearches: List<String> = emptyList()
                      ) : TvCatalogScreenState

    // loginRequired: same reasoning as Favorites - recommendations are built
    // from the account's favorites/recent searches. keySetupRequired: no
    // Gemini key saved (runtime or BuildConfig) yet, so items/errorMessage
    // are both meaningless until one is entered.
    data class Recommendations(
            val loginRequired: Boolean = false,
            val keySetupRequired: Boolean = false,
            val isLoading: Boolean = false,
            val items: List<CatalogItem> = emptyList(),
            val errorMessage: String? = null,
            // The suggestion whose preview popup is open (like Movies/TvSeries).
            val focusedItem: CatalogItem? = null
                               ) : TvCatalogScreenState

    // Every request behind Movies/TV Series/Favorites/News failed for lack of
    // a connection: shown instead of empty rows, with Try again / Open network
    // settings. Never cached, so revisiting the tab tries again.
    data object Offline : TvCatalogScreenState

    // Static Terms & Privacy content combined into one scrollable page - no
    // network call behind it, so unlike every other destination there's
    // nothing to load in onDestinationSelected beyond setting this state.
    data object About : TvCatalogScreenState
}

// Real, non-favorites categories shown as the "4 rows" per destination.
// TvSeriesCategory actually has 5 non-favorites options (Popular,
// AiringToday, OnTV, TopRated, Upcoming) - AiringToday is dropped here so
// both destinations line up at exactly 4 rows.
private val movieRowCategories = listOf(
        MovieCategory.PopularMovieCategory,
        MovieCategory.NowPlayingMovieCategory,
        MovieCategory.UpcomingMovieCategory,
        MovieCategory.TopRatedMovieCategory
                                        )

private val tvSeriesRowCategories = listOf(
        TvSeriesCategory.PopularTvSeriesCategory,
        TvSeriesCategory.OnTVTvSeriesCategory,
        TvSeriesCategory.UpcomingTvSeriesCategory,
        TvSeriesCategory.TopRatedTvSeriesCategory
                                           )

@HiltViewModel
class TvCatalogViewModel @Inject constructor(
        private val movieRepository: MovieRepository,
        private val tvSeriesRepository: TvSeriesRepository,
        private val newsRepository: NewsRepository,
        private val searchRepository: SearchRepository,
        private val catalogItemMapper: CatalogItemMapper,
        private val authenticationRepository: AuthenticationRepository,
        private val aiRecommendationRepository: AiRecommendationRepository
                                             ) : ViewModel() {

    companion object {
        // Exists to avoid firing a real network call for every keystroke -
        // only once the user types faster than this gap allows does the
        // actual request go out. Matches Google's own documented ~300ms
        // debounce for TV search (Leanback SearchFragment).
        private const val SEARCH_DEBOUNCE_MS = 300L
    }

    private val _selectedDestination = MutableStateFlow(TvCatalogDestination.Movies)
    val selectedDestination: StateFlow<TvCatalogDestination> = _selectedDestination.asStateFlow()

    private val _screenState =
            MutableStateFlow<TvCatalogScreenState>(TvCatalogScreenState.Movies(emptyList()))
    val screenState: StateFlow<TvCatalogScreenState> = _screenState.asStateFlow()

    private val _messageEvent: MutableSharedFlow<String> = MutableSharedFlow(extraBufferCapacity = 1)
    val messageEvent: SharedFlow<String> = _messageEvent.asSharedFlow()

    private val _userProfileState = MutableStateFlow(TvUserProfileUiState())
    val userProfileState: StateFlow<TvUserProfileUiState> = _userProfileState.asStateFlow()

    // Whether the hero preview dialog's Favorite button should show at all -
    // TMDB favorites need a real logged-in session (guest sessions can only
    // rate, not favorite), same rule as loadFavorites()'s loginRequired.
    val canToggleFavorite: Boolean
        get() = authenticationRepository.getSharedPrefLoginSessionId() != null &&
                authenticationRepository.getSharedPrefAccountId() != null

    private var searchJob: Job? = null

    // Guards loadMovies/loadTvSeries/loadFavorites/loadNews: the nav rail
    // selects a destination purely on D-pad focus passing over it (see
    // CatalogNavRailItem), so a quick scroll down the rail fires several of
    // these in a row before the first one's network call resolves. Without
    // cancelling the previous one, a slower load for a since-abandoned
    // destination could resolve after a faster one and overwrite the
    // currently-selected tab's content with stale data.
    private var contentLoadJob: Job? = null

    // Tracks the in-flight onRowEndReached/onFavoritesRowEndReached
    // pagination call for each row's category. Unlike contentLoadJob, a full
    // destination reload (loadMovies/loadTvSeries/loadFavorites) doesn't
    // touch these - without tracking and cancelling them here, a still-
    // pending page-2+ fetch from before that reload (e.g. from a row the
    // user paginated right before logging out or switching tabs) can resolve
    // afterward and silently merge stale items into the fresh state, since
    // its updateRow closure only pattern-matches on screen-state *type*, not
    // on "is this still the same load".
    private val rowLoadJobs = mutableMapOf<Category, Job>()

    // The successful Recommendations result for the signed-in account, kept
    // until the ViewModel (i.e. app launch) goes away. Gemini is slow and
    // quota-limited, so once it has answered it isn't called again for the same
    // favorites: favoriteKeys is the set of favorites the result was built
    // from, and a visit that finds a different set (a title added or removed
    // anywhere) makes a new request. A failure is not cached: coming back to
    // the tab after visiting another one tries again, as does the error
    // state's Retry button - but nothing retries automatically while the user
    // stays on the tab.
    private data class RecommendationsCache(
            val accountId: Int,
            val state: TvCatalogScreenState.Recommendations,
            val favoriteKeys: Set<Pair<Int, CatalogMediaType>>
                                           )
    private var recommendationsCache: RecommendationsCache? = null

    private fun cancelRowLoadJobs() {
        rowLoadJobs.values.forEach { it.cancel() }
        rowLoadJobs.clear()
    }

    // Per-destination content cache, alive for as long as this ViewModel is
    // (i.e. reset by a fresh app launch, never invalidated manually) - lets
    // revisiting a tab within the same session reuse what's already loaded
    // instead of re-fetching. Favorites is deliberately excluded: its content
    // can change from outside this screen (toggling a favorite), so it always
    // reloads on selection - see loadFavorites(). Recommendations keeps its
    // own per-account cache instead (recommendationsCache).
    private val destinationCache = mutableMapOf<TvCatalogDestination, TvCatalogScreenState>()

    init {
        // TEMPORARY: :tv has no API-key entry flow yet (unlike :app's
        // ApiKeySetupView), and :tv is a separate application/process with
        // its own empty SharedPreferences - seed it from BuildConfig (see
        // tv/build.gradle.kts) so catalog calls authenticate, until a real
        // TV setup screen exists. Only seeds when nothing is saved yet, so
        // it never clobbers a real key from a future setup flow.
        if (authenticationRepository.getSharedPrefApiKey().isNullOrBlank() && BuildConfig.TMDB_API_KEY.isNotBlank()) {
            authenticationRepository.saveSharedPrefApiKey(BuildConfig.TMDB_API_KEY)
        }
        loadMovies()
        loadUserProfile()
    }

    // Mirrors :app's CatalogViewModel.loadUserProfile (see its kdoc) - shown
    // above the catalog content per the profile header design, so the same
    // name/avatar/guest state that drives the mobile UserAvatarButton is
    // available here too.
    private fun loadUserProfile() {
        val loginSessionId = authenticationRepository.getSharedPrefLoginSessionId()
        if (loginSessionId == null) {
            _userProfileState.value = TvUserProfileUiState(isGuest = true)
            return
        }
        viewModelScope.launch {
            when (val result = authenticationRepository.getAccountDetailsResult(loginSessionId)) {
                is AccountDetailsResult.Success -> {
                    authenticationRepository.saveSharedPrefAccountId(result.accountId)
                    _userProfileState.value = TvUserProfileUiState(
                            name = result.name,
                            imageUrl = result.avatarUrl,
                            isGuest = false
                                                                   )
                }
                is AccountDetailsResult.Failure -> {
                    _userProfileState.value = TvUserProfileUiState(isGuest = false)
                }
            }
        }
    }

    // Triggered from the profile dialog's "Log out" option (only shown when
    // already logged in). Mirrors :app's CatalogViewModel.onLogoutClicked
    // (deletes the TMDB session + clears account-scoped local data) but,
    // unlike mobile, doesn't navigate anywhere afterward - it drops straight
    // into a fresh guest session so the catalog itself just keeps working,
    // matching this screen's "log out means guest mode, not a forced trip
    // back to the auth screen" design. Clearing destinationCache and
    // reloading whatever's currently selected is what gets rid of stale
    // isFavorite=true stars left over from the logged-in account's rows.
    fun onLogoutClicked() {
        viewModelScope.launch {
            authenticationRepository.logout()
            when (val result = authenticationRepository.getGuestSessionIdResult()) {
                is GuestSessionIdResult.Success -> authenticationRepository.saveSharedPrefGuestSessionId(result.guestSessionId)
                is GuestSessionIdResult.Failure -> Unit
            }
            _userProfileState.value = TvUserProfileUiState(isGuest = true)
            destinationCache.clear()
            // A row pagination job left over from whichever destination was
            // selected before logout (not necessarily the one reloaded
            // below) could otherwise resolve afterward and merge stale,
            // pre-logout items into the fresh state.
            cancelRowLoadJobs()
            when (_selectedDestination.value) {
                TvCatalogDestination.Movies          -> loadMovies()
                TvCatalogDestination.TvSeries        -> loadTvSeries()
                TvCatalogDestination.Favorites        -> loadFavorites()
                TvCatalogDestination.News             -> loadNews()
                TvCatalogDestination.Recommendations  -> loadRecommendations()
                TvCatalogDestination.Search, TvCatalogDestination.About -> Unit
            }
        }
    }

    fun onDestinationSelected(destination: TvCatalogDestination) {
        if (destination == _selectedDestination.value) return
        _selectedDestination.value = destination
        when (destination) {
            TvCatalogDestination.Movies    -> loadCachedOrFetch(destination) { loadMovies() }
            TvCatalogDestination.TvSeries  -> loadCachedOrFetch(destination) { loadTvSeries() }
            TvCatalogDestination.Favorites -> loadFavorites()
            TvCatalogDestination.News      -> loadCachedOrFetch(destination) { loadNews() }
            // Search starts empty until the user actually types something -
            // there's no sensible default query to run.
            TvCatalogDestination.Search    -> {
                contentLoadJob?.cancel()
                _screenState.value = Search()
                loadRecentSearches()
            }
            TvCatalogDestination.Recommendations -> loadRecommendations()
            TvCatalogDestination.About     -> {
                contentLoadJob?.cancel()
                _screenState.value = TvCatalogScreenState.About
            }
        }
    }

    private inline fun loadCachedOrFetch(destination: TvCatalogDestination, fetch: () -> Unit) {
        val cached = destinationCache[destination]
        if (cached != null) {
            contentLoadJob?.cancel()
            // The cache can be written while the preview dialog is open (e.g.
            // toggling Favorite from it stores the state with focusedItem
            // set), and dismissing the dialog only clears the live state -
            // so a stale focusedItem here would reopen the dialog on revisit.
            _screenState.value = cached.withoutFocusedItem()
        } else {
            fetch()
        }
    }

    // Called on an explicit D-pad center press on a row item - opens the
    // preview dialog for it. A no-op for destinations that don't carry a
    // focusedItem (Favorites/News/Search). category is the row's own
    // Category, carried along so "View details" can route a movie to the
    // right MovieDetailsDestination categoryId (needed for its TBA styling).
    fun onItemFocused(item: CatalogItem, category: Category? = null) {
        _screenState.value = when (val current = _screenState.value) {
            is TvCatalogScreenState.Movies          -> current.copy(focusedItem = item, focusedCategory = category)
            is TvCatalogScreenState.TvSeries        -> current.copy(focusedItem = item, focusedCategory = category)
            is TvCatalogScreenState.Favorites       -> current.copy(focusedItem = item)
            is TvCatalogScreenState.Search          -> current.copy(focusedItem = item)
            is TvCatalogScreenState.Recommendations -> current.copy(focusedItem = item)
            else                                    -> current
        }
    }

    // "Try again" on the Offline screen: reload whichever tab is showing it.
    fun onOfflineRetryClicked() {
        // The account request behind the profile badge fails offline too and
        // leaves it blank ("?") - fetch it again along with the tab.
        _userProfileState.value.let { profile ->
            if (!profile.isGuest && profile.name.isBlank()) loadUserProfile()
        }
        when (_selectedDestination.value) {
            TvCatalogDestination.Movies    -> loadMovies()
            TvCatalogDestination.TvSeries  -> loadTvSeries()
            TvCatalogDestination.Favorites -> loadFavorites()
            TvCatalogDestination.News      -> loadNews()
            TvCatalogDestination.Recommendations -> loadRecommendations()
            else                           -> Unit
        }
    }

    fun onGeminiApiKeySaved(apiKey: String) {
        aiRecommendationRepository.saveSharedPrefApiKey(apiKey)
        // Immediately try to load real suggestions with the key just
        // entered, rather than leaving the key-setup form on screen until
        // the user reselects the tab.
        loadRecommendations()
    }

    // Retry button on the Recommendations error state - retries without
    // needing to leave and re-enter the tab.
    fun onRecommendationsRetryClicked() {
        recommendationsCache = null
        loadRecommendations()
    }

    private fun loadRecommendations() {
        contentLoadJob?.cancel()
        if (_userProfileState.value.isGuest) {
            _screenState.value = TvCatalogScreenState.Recommendations(loginRequired = true)
            return
        }
        if (!aiRecommendationRepository.hasApiKey()) {
            _screenState.value = TvCatalogScreenState.Recommendations(keySetupRequired = true)
            return
        }
        val sessionId = authenticationRepository.getSharedPrefLoginSessionId()
        val accountId = authenticationRepository.getSharedPrefAccountId()
        if (sessionId == null || accountId == null) {
            _screenState.value = TvCatalogScreenState.Recommendations(loginRequired = true)
            return
        }

        _screenState.value = TvCatalogScreenState.Recommendations(isLoading = true)
        contentLoadJob = viewModelScope.launch {
            val cached = recommendationsCache?.takeIf { it.accountId == accountId }

            // The current favorites decide whether the cached result is still
            // valid. (Two cheap TMDB calls - never a Gemini call.)
            val favorites = fetchFavoriteCatalogItems(accountId, sessionId)
            if (favorites == null) {
                // Couldn't read favorites (offline): keep showing what we had, or
                // say there's no connection - not "no favorites yet".
                _screenState.value = cached?.state?.withoutFocusedItem() ?: TvCatalogScreenState.Offline
                return@launch
            }
            val favoriteKeys = favorites.map { it.id to it.mediaType }.toSet()

            // Same favorites as when the cached row was built: reuse it until the
            // app is relaunched. Favorites changed (added or removed): fall
            // through to a new request, which also leaves out anything favorited
            // from the row in the meantime.
            if (cached != null && cached.favoriteKeys == favoriteKeys) {
                _screenState.value = cached.state.withoutFocusedItem()
                return@launch
            }

            when (val result = aiRecommendationRepository.getRecommendations(favorites)) {
                is RecommendationResult.Success -> {
                    // The prompt only *asks* Gemini not to repeat favorites - enforce
                    // it here, on the resolved real titles (same id + media type).
                    val items = resolveSuggestions(result.suggestions).filterNot { (it.id to it.mediaType) in favoriteKeys }
                    val state = TvCatalogScreenState.Recommendations(items = items)
                    _screenState.value = state
                    // Only real suggestions are pinned - with no favorites no
                    // Gemini call was spent, and some may show up later.
                    recommendationsCache = if (state.items.isNotEmpty()) RecommendationsCache(accountId, state, favoriteKeys) else null
                }
                is RecommendationResult.Failure -> {
                    // Not cached, and nothing retries on its own while the user
                    // stays here (each call spends quota): the next attempt is
                    // either the Retry button or a fresh visit to this tab
                    // after navigating elsewhere.
                    _screenState.value = TvCatalogScreenState.Recommendations(errorMessage = result.errorMessage)
                    recommendationsCache = null
                }
            }
        }
    }

    // The account's favorite movies and series, or null if either request
    // failed - a failure must not be mistaken for "no favorites".
    private suspend fun fetchFavoriteCatalogItems(accountId: Int, sessionId: String): List<CatalogItem>? = coroutineScope {
        val moviesDeferred = async { movieRepository.getFavoriteMovies(accountId, sessionId) }
        val tvSeriesDeferred = async { tvSeriesRepository.getFavoriteTvSeries(accountId, sessionId) }
        val moviesResult = moviesDeferred.await() as? MoviesResult.Success ?: return@coroutineScope null
        val tvSeriesResult = tvSeriesDeferred.await() as? TvSeriesResult.Success ?: return@coroutineScope null
        catalogItemMapper.fromMovies(moviesResult.movies) + catalogItemMapper.fromTvSeries(tvSeriesResult.tvSeries)
    }

    // Never trust a suggestion's query as a real title on its own (see
    // RecommendationSuggestion's doc) - resolve it against a real catalog
    // search and only keep what the search actually finds. mediaTypeHint,
    // when present, picks the matching result over the first one so an
    // ambiguous title (e.g. a movie and a show sharing a name) doesn't
    // silently resolve to the wrong kind.
    private suspend fun resolveSuggestions(suggestions: List<RecommendationSuggestion>): List<CatalogItem> = coroutineScope {
        suggestions.map { suggestion ->
            async {
                val candidates = when (val result = searchRepository.search(suggestion.query)) {
                    is SearchResult.Success -> result.results.mapNotNull { it.toCatalogItemOrNull() }
                    is SearchResult.Failure -> emptyList()
                }
                suggestion.mediaTypeHint?.let { hint -> candidates.firstOrNull { it.mediaType == hint } } ?: candidates.firstOrNull()
            }
        }.awaitAll().filterNotNull().distinctBy { it.id to it.mediaType }.withFavoriteFlags()
    }

    // Closes the preview dialog (back press / scrim dismissal).
    fun onDialogDismissed() {
        _screenState.value = _screenState.value.withoutFocusedItem()
    }

    // Cards whose favorite state was flipped from the popup have to leave
    // their row once the popup is closed: a title favorited from
    // Recommendations (which never lists favorites), or un-favorited from
    // Favorites (which lists nothing but). Deliberately a separate step from
    // onDialogDismissed: the screen first moves D-pad focus off the card (to
    // the rail item), because removing a card while it still holds focus makes
    // Compose fall back to the first focusable node - the Movies rail item,
    // which auto-navigates and hijacks the tab.
    fun onPopupClosedPrune() {
        when (val current = _screenState.value) {
            is TvCatalogScreenState.Recommendations -> {
                if (current.items.none { it.isFavorite }) return
                val remaining = current.copy(items = current.items.filterNot { it.isFavorite })
                _screenState.value = remaining
                recommendationsCache = recommendationsCache?.copy(state = remaining)
            }
            is TvCatalogScreenState.Favorites       -> {
                val rows = current.rows.map { row -> row.copy(items = row.items.filter { it.isFavorite }) }
                if (rows != current.rows) _screenState.value = current.copy(rows = rows)
            }
            else                                    -> Unit
        }
    }

    // Clears the preview popup's item on any state that has one. Also used to
    // sanitize states restored from a cache: a cache write can happen while the
    // popup is open (e.g. toggling Favorite), and restoring that stale
    // focusedItem would reopen the popup on a later revisit.
    private fun TvCatalogScreenState.withoutFocusedItem(): TvCatalogScreenState = when (this) {
        is TvCatalogScreenState.Movies          -> copy(focusedItem = null, focusedCategory = null)
        is TvCatalogScreenState.TvSeries        -> copy(focusedItem = null, focusedCategory = null)
        is TvCatalogScreenState.Favorites       -> copy(focusedItem = null)
        is TvCatalogScreenState.Search          -> copy(focusedItem = null)
        is TvCatalogScreenState.Recommendations -> copy(focusedItem = null)
        else                                    -> this
    }

    // Toggles favorite on whichever item the hero dialog is currently
    // showing. Optimistic like the details screens' equivalent, updating
    // both focusedItem and the item's entry in its row so the row still
    // reflects the new state after the dialog closes; reverts on failure.
    fun onFeaturedFavoriteClicked() {
        val current = _screenState.value
        val item = when (current) {
            is TvCatalogScreenState.Movies          -> current.focusedItem
            is TvCatalogScreenState.TvSeries        -> current.focusedItem
            is TvCatalogScreenState.Favorites       -> current.focusedItem
            is TvCatalogScreenState.Search          -> current.focusedItem
            is TvCatalogScreenState.Recommendations -> current.focusedItem
            else                                    -> null
        } ?: return
        val sessionId = authenticationRepository.getSharedPrefLoginSessionId() ?: return
        val accountId = authenticationRepository.getSharedPrefAccountId() ?: return

        val newIsFavorite = !item.isFavorite
        applyFavoriteState(item.id, item.mediaType, newIsFavorite)

        viewModelScope.launch {
            val result = when (item.mediaType) {
                CatalogMediaType.MOVIE     -> movieRepository.toggleFavorite(accountId, sessionId, item.id, newIsFavorite)
                CatalogMediaType.TV_SERIES -> tvSeriesRepository.toggleFavorite(accountId, sessionId, item.id, newIsFavorite)
            }
            if (result is ToggleFavoriteResult.Failure) {
                applyFavoriteState(item.id, item.mediaType, !newIsFavorite)
                _messageEvent.tryEmit(result.errorMessage)
            } else {
                // The popup can now be opened from Favorites/Search/
                // Recommendations too, so the Movies/TV Series rows (and their
                // cache) may hold this title with a now-stale star.
                refreshFavoriteStates()
            }
        }
    }

    // Toggling favorite from a details screen (MovieDetailsTvScreen /
    // TvSeriesDetailsTvScreen) only updates that screen's own local state and
    // the shared repository's in-memory favorite-id cache - it has no way to
    // reach back into this ViewModel's already-fetched rows/destinationCache,
    // which is why e.g. a series favorited from its details screen could
    // come back to the catalog still showing "Add to Favorites" in the hero.
    // Re-syncing every row (and the cache) against that repository cache -
    // already correct the instant any toggle succeeds anywhere in the app -
    // fixes it. Called from CatalogTvScreen's LaunchedEffect(Unit), which
    // reruns on every genuine return to this screen (see its comment).
    // reloadFavoritesTab: on a return from a details screen the Favorites tab
    // may now list a title that was un-favorited there (or miss a new one), and
    // star-flipping isn't enough - it needs the real list. False for the
    // popup's own toggle, which must not reload the list under an open popup.
    fun refreshFavoriteStates(reloadFavoritesTab: Boolean = false) {
        if (reloadFavoritesTab && _screenState.value is TvCatalogScreenState.Favorites) {
            loadFavorites()
        }
        viewModelScope.launch {
            val movieFavoriteIds = movieRepository.getCachedFavoriteIds()
            val tvFavoriteIds = tvSeriesRepository.getCachedFavoriteIds()

            fun CatalogItem.withRefreshedIsFavorite(): CatalogItem {
                val favoriteIds = if (mediaType == CatalogMediaType.MOVIE) movieFavoriteIds else tvFavoriteIds
                val refreshedIsFavorite = favoriteIds.contains(id)
                return if (refreshedIsFavorite != isFavorite) copy(isFavorite = refreshedIsFavorite) else this
            }

            fun List<TvCategoryRowContent>.withRefreshedItems() =
                    map { row -> row.copy(items = row.items.map { it.withRefreshedIsFavorite() }) }

            fun TvCatalogScreenState.withRefreshedFavorites(): TvCatalogScreenState = when (this) {
                is TvCatalogScreenState.Movies   -> copy(
                        rows = rows.withRefreshedItems(),
                        focusedItem = focusedItem?.withRefreshedIsFavorite()
                                                         )
                is TvCatalogScreenState.TvSeries -> copy(
                        rows = rows.withRefreshedItems(),
                        focusedItem = focusedItem?.withRefreshedIsFavorite()
                                                         )
                else                             -> this
            }

            _screenState.value = _screenState.value.withRefreshedFavorites()
            destinationCache[TvCatalogDestination.Movies]?.let {
                destinationCache[TvCatalogDestination.Movies] = it.withRefreshedFavorites()
            }
            destinationCache[TvCatalogDestination.TvSeries]?.let {
                destinationCache[TvCatalogDestination.TvSeries] = it.withRefreshedFavorites()
            }
        }
    }

    private fun applyFavoriteState(itemId: Int, mediaType: CatalogMediaType, isFavorite: Boolean) {
        fun List<TvCategoryRowContent>.withUpdatedItem() = map { row ->
            row.copy(items = row.items.map {
                if (it.id == itemId && it.mediaType == mediaType) it.copy(isFavorite = isFavorite) else it
            })
        }
        fun CatalogItem.withUpdatedIsFavorite() =
                if (id == itemId && this.mediaType == mediaType) copy(isFavorite = isFavorite) else this

        fun List<CatalogItem>.withUpdatedItems() = map { it.withUpdatedIsFavorite() }

        _screenState.value = when (val current = _screenState.value) {
            is TvCatalogScreenState.Movies          -> current.copy(
                    rows = current.rows.withUpdatedItem(),
                    focusedItem = current.focusedItem?.withUpdatedIsFavorite()
                                                                    )
            is TvCatalogScreenState.TvSeries        -> current.copy(
                    rows = current.rows.withUpdatedItem(),
                    focusedItem = current.focusedItem?.withUpdatedIsFavorite()
                                                                    )
            // Favorites keeps the row (star flips, item stays) until the next
            // visit reloads it - removing a card under the open popup would
            // pull focus out from under it.
            is TvCatalogScreenState.Favorites       -> current.copy(
                    rows = current.rows.withUpdatedItem(),
                    focusedItem = current.focusedItem?.withUpdatedIsFavorite()
                                                                    )
            is TvCatalogScreenState.Search          -> current.copy(
                    items = current.items.withUpdatedItems(),
                    focusedItem = current.focusedItem?.withUpdatedIsFavorite()
                                                                    )
            is TvCatalogScreenState.Recommendations -> current.copy(
                    items = current.items.withUpdatedItems(),
                    focusedItem = current.focusedItem?.withUpdatedIsFavorite()
                                                                    )
            else                                    -> current
        }
        // Keep the per-destination cache in sync so a later tab revisit
        // doesn't revert this favorite toggle back to its pre-toggle state.
        // (Cached states are stored without the popup's item - see
        // withoutFocusedItem.)
        when (val latest = _screenState.value) {
            is TvCatalogScreenState.Movies          -> destinationCache[TvCatalogDestination.Movies] = latest.withoutFocusedItem()
            is TvCatalogScreenState.TvSeries        -> destinationCache[TvCatalogDestination.TvSeries] = latest.withoutFocusedItem()
            is TvCatalogScreenState.Recommendations ->
                recommendationsCache = recommendationsCache?.copy(state = latest.copy(focusedItem = null))
            else                                    -> Unit
        }
    }

    fun onSearchQueryChanged(query: String) {
        val current = _screenState.value as? TvCatalogScreenState.Search ?: return
        _screenState.value = current.copy(query = query, isSearching = query.isNotBlank())

        searchJob?.cancel()
        if (query.isBlank()) {
            // Keep the already-loaded recentSearches rather than wiping them -
            // this state also carries no items, but this branch runs on
            // every deleted keystroke back to empty, not just on first entry.
            _screenState.value = current.copy(query = query, items = emptyList(), isSearching = false)
            return
        }
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            val items = when (val result = searchRepository.search(query)) {
                is SearchResult.Success -> result.results.mapNotNull { it.toCatalogItemOrNull() }.withFavoriteFlags()
                is SearchResult.Failure -> emptyList()
            }
            // The query may have changed again while this was in flight;
            // only apply the result if it's still the latest search state.
            val latest = _screenState.value as? TvCatalogScreenState.Search ?: return@launch
            if (latest.query == query) {
                _screenState.value = latest.copy(items = items, isSearching = false)
            }
        }
    }

    private fun loadRecentSearches() {
        viewModelScope.launch {
            val recentSearches = searchRepository.getRecentSearches()
            val current = _screenState.value as? TvCatalogScreenState.Search ?: return@launch
            _screenState.value = current.copy(recentSearches = recentSearches)
        }
    }

    // Mirrors :app's onRecentSearchClicked: re-running a past query also
    // bumps it back to the front of the MRU list (addRecentSearch), same as
    // typing it fresh and getting a result would.
    fun onRecentSearchClicked(query: String) {
        onSearchQueryChanged(query)
        viewModelScope.launch {
            searchRepository.addRecentSearch(query)
            loadRecentSearches()
        }
    }

    fun onClearRecentSearchesClicked() {
        viewModelScope.launch {
            searchRepository.clearRecentSearches()
            loadRecentSearches()
        }
    }

    // Records the query that actually led somewhere - mirrors :app's
    // onResultClicked/onSearchSubmitted, which only ever save a query once
    // it either produced a picked result or was explicitly submitted, never
    // on every debounced keystroke. TV search has no separate submit
    // affordance, so a picked result is the only such moment here. No
    // per-item remove affordance is offered on this screen (unlike :app's
    // RecentSearchRow) - D-pad-reachable removal would need its own
    // focusable target interleaved with the chip list, which is deferred
    // rather than shipped half-considered; onClearRecentSearchesClicked
    // covers the "start over" case in the meantime.
    fun onSearchResultClicked(item: CatalogItem) {
        val query = (_screenState.value as? TvCatalogScreenState.Search)?.query?.trim()
        if (query.isNullOrEmpty()) return
        viewModelScope.launch {
            searchRepository.addRecentSearch(query)
        }
    }

    // Search results include PERSON entries, which have no equivalent in
    // CatalogMediaType and nowhere to route to on tv yet, so they're dropped
    // rather than mapped to a fake movie/tv entry.
    // Search results (and the AI suggestions resolved through search) come
    // back without any favorite information, so every one would show "Favorite"
    // in its popup even when it already is one. Stamp isFavorite from the
    // repositories' favorite-id caches - the same source refreshFavoriteStates
    // uses for the Movies/TV Series rows.
    private suspend fun List<CatalogItem>.withFavoriteFlags(): List<CatalogItem> {
        if (isEmpty()) return this
        val movieFavoriteIds = movieRepository.getCachedFavoriteIds()
        val tvFavoriteIds = tvSeriesRepository.getCachedFavoriteIds()
        return map {
            val favoriteIds = if (it.mediaType == CatalogMediaType.MOVIE) movieFavoriteIds else tvFavoriteIds
            it.copy(isFavorite = favoriteIds.contains(it.id))
        }
    }

    private fun SearchResultModel.toCatalogItemOrNull(): CatalogItem? {
        val mediaType = when (mediaType) {
            SearchResultMediaType.MOVIE     -> CatalogMediaType.MOVIE
            SearchResultMediaType.TV_SERIES -> CatalogMediaType.TV_SERIES
            SearchResultMediaType.PERSON    -> return null
        }
        return CatalogItem(
                id = id,
                title = title,
                imageUrl = imageUrl,
                backdropUrl = backdropUrl,
                releaseDate = subtitle,
                mediaType = mediaType,
                rating = rating
                           )
    }

    private fun loadMovies() {
        contentLoadJob?.cancel()
        cancelRowLoadJobs()
        // No rows yet -> the screen renders row shimmers (rather than leaving
        // the previously selected tab's content up until the load resolves).
        _screenState.value = TvCatalogScreenState.Movies(emptyList())
        contentLoadJob = viewModelScope.launch {
            // Each row with whether its request failed for lack of a connection.
            val outcomes = coroutineScope {
                movieRowCategories.map { category ->
                    async {
                        when (val result = movieRepository.getMoviesByCategory(category, page = 1)) {
                            is MoviesResult.Success -> TvCategoryRowContent(
                                    category = category,
                                    items = catalogItemMapper.fromMovies(result.movies),
                                    currentPage = result.page,
                                    endReached = isLastPage(result.page, result.totalPages)
                                                                            ) to false
                            is MoviesResult.Failure -> TvCategoryRowContent(category, emptyList(), endReached = true) to result.isConnectivityFailure
                        }
                    }
                }.awaitAll()
            }
            if (outcomes.all { it.second }) {
                _screenState.value = TvCatalogScreenState.Offline
                return@launch
            }
            val rows = outcomes.map { it.first }
            // No default focusedItem anymore - the preview is now a dialog
            // that only appears once the user explicitly selects an item,
            // not something shown automatically on load.
            val state = TvCatalogScreenState.Movies(rows = rows)
            _screenState.value = state
            destinationCache[TvCatalogDestination.Movies] = state
        }
    }

    private fun loadTvSeries() {
        contentLoadJob?.cancel()
        cancelRowLoadJobs()
        _screenState.value = TvCatalogScreenState.TvSeries(emptyList())
        contentLoadJob = viewModelScope.launch {
            val outcomes = coroutineScope {
                tvSeriesRowCategories.map { category ->
                    async {
                        when (val result = tvSeriesRepository.getTvSeriesByCategory(category, page = 1)) {
                            is TvSeriesResult.Success -> TvCategoryRowContent(
                                    category = category,
                                    items = catalogItemMapper.fromTvSeries(result.tvSeries),
                                    currentPage = result.page,
                                    endReached = isLastPage(result.page, result.totalPages)
                                                                              ) to false
                            is TvSeriesResult.Failure -> TvCategoryRowContent(category, emptyList(), endReached = true) to result.isConnectivityFailure
                        }
                    }
                }.awaitAll()
            }
            if (outcomes.all { it.second }) {
                _screenState.value = TvCatalogScreenState.Offline
                return@launch
            }
            val state = TvCatalogScreenState.TvSeries(rows = outcomes.map { it.first })
            _screenState.value = state
            destinationCache[TvCatalogDestination.TvSeries] = state
        }
    }

    // Triggered when a row's LazyRow scrolls near its last item. Only the
    // one row for this category is updated/refetched - the other 3 rows on
    // screen keep whatever they've already loaded.
    fun onRowEndReached(category: Category) {
        val current = _screenState.value
        val rows = when (current) {
            is TvCatalogScreenState.Movies   -> current.rows
            is TvCatalogScreenState.TvSeries -> current.rows
            else                             -> return
        }
        val row = rows.firstOrNull { it.category == category } ?: return
        if (!canLoadNextPage(row.endReached, row.isLoadingNextPage)) return

        fun updateRow(transform: (TvCategoryRowContent) -> TvCategoryRowContent) {
            _screenState.value = when (val latest = _screenState.value) {
                is TvCatalogScreenState.Movies   -> latest.copy(rows = latest.rows.map { if (it.category == category) transform(it) else it })
                is TvCatalogScreenState.TvSeries -> latest.copy(rows = latest.rows.map { if (it.category == category) transform(it) else it })
                else                             -> latest
            }
            // Keep the per-destination cache in sync so a later tab revisit
            // doesn't revert this row back to its state at initial load.
            when (val latest = _screenState.value) {
                is TvCatalogScreenState.Movies   -> destinationCache[TvCatalogDestination.Movies] = latest
                is TvCatalogScreenState.TvSeries -> destinationCache[TvCatalogDestination.TvSeries] = latest
                else                             -> Unit
            }
        }

        updateRow { it.copy(isLoadingNextPage = true) }
        val nextPage = row.currentPage + 1
        rowLoadJobs[category]?.cancel()
        rowLoadJobs[category] = viewModelScope.launch {
            when (category) {
                is MovieCategory    -> when (val result = movieRepository.getMoviesByCategory(category, nextPage)) {
                    is MoviesResult.Success -> updateRow {
                        it.copy(
                                items = mergePagedItems(it.items, catalogItemMapper.fromMovies(result.movies), nextPage) { item -> item.id },
                                currentPage = result.page,
                                endReached = isLastPage(result.page, result.totalPages),
                                isLoadingNextPage = false
                               )
                    }
                    is MoviesResult.Failure -> updateRow { it.copy(isLoadingNextPage = false) }
                }
                is TvSeriesCategory -> when (val result = tvSeriesRepository.getTvSeriesByCategory(category, nextPage)) {
                    is TvSeriesResult.Success -> updateRow {
                        it.copy(
                                items = mergePagedItems(it.items, catalogItemMapper.fromTvSeries(result.tvSeries), nextPage) { item -> item.id },
                                currentPage = result.page,
                                endReached = isLastPage(result.page, result.totalPages),
                                isLoadingNextPage = false
                               )
                    }
                    is TvSeriesResult.Failure -> updateRow { it.copy(isLoadingNextPage = false) }
                }
                // Favorites categories aren't paged from here (loadFavorites
                // fetches them separately and isn't row-driven).
                else                -> updateRow { it.copy(isLoadingNextPage = false) }
            }
        }
    }

    private fun loadFavorites() {
        contentLoadJob?.cancel()
        cancelRowLoadJobs()
        val sessionId = authenticationRepository.getSharedPrefLoginSessionId()
        val accountId = authenticationRepository.getSharedPrefAccountId()
        if (sessionId == null || accountId == null) {
            _screenState.value = TvCatalogScreenState.Favorites(loginRequired = true)
            return
        }
        _screenState.value = TvCatalogScreenState.Favorites(isLoading = true)
        contentLoadJob = viewModelScope.launch {
            var moviesOffline = false
            var tvSeriesOffline = false
            val (movieRow, tvSeriesRow) = coroutineScope {
                val moviesDeferred = async { movieRepository.getFavoriteMovies(accountId, sessionId) }
                val tvSeriesDeferred = async { tvSeriesRepository.getFavoriteTvSeries(accountId, sessionId) }
                val movieRow = when (val result = moviesDeferred.await()) {
                    is MoviesResult.Success -> TvCategoryRowContent(
                            category = MovieCategory.FavoritesMovieCategory,
                            items = catalogItemMapper.fromMovies(result.movies),
                            currentPage = result.page,
                            endReached = isLastPage(result.page, result.totalPages)
                                                                    )
                    is MoviesResult.Failure -> {
                        moviesOffline = result.isConnectivityFailure
                        TvCategoryRowContent(MovieCategory.FavoritesMovieCategory, emptyList(), endReached = true)
                    }
                }
                val tvSeriesRow = when (val result = tvSeriesDeferred.await()) {
                    is TvSeriesResult.Success -> TvCategoryRowContent(
                            category = TvSeriesCategory.FavoritesTvSeriesCategory,
                            items = catalogItemMapper.fromTvSeries(result.tvSeries),
                            currentPage = result.page,
                            endReached = isLastPage(result.page, result.totalPages)
                                                                      )
                    is TvSeriesResult.Failure -> {
                        tvSeriesOffline = result.isConnectivityFailure
                        TvCategoryRowContent(TvSeriesCategory.FavoritesTvSeriesCategory, emptyList(), endReached = true)
                    }
                }
                movieRow to tvSeriesRow
            }
            _screenState.value = if (moviesOffline && tvSeriesOffline) {
                // Both requests failed for lack of a connection - not an empty
                // favorites list.
                TvCatalogScreenState.Offline
            } else {
                TvCatalogScreenState.Favorites(rows = listOf(movieRow, tvSeriesRow))
            }
        }
    }

    // Favorites pagination is separate from onRowEndReached above: favorites
    // are fetched from the dedicated getFavoriteMovies/getFavoriteTvSeries
    // endpoints (which need accountId/sessionId), not the generic
    // by-category endpoint - and FavoritesMovieCategory/
    // FavoritesTvSeriesCategory are themselves MovieCategory/TvSeriesCategory
    // subtypes, so routing them through onRowEndReached's category branch
    // would silently call the wrong endpoint.
    fun onFavoritesRowEndReached(category: Category) {
        val current = _screenState.value as? TvCatalogScreenState.Favorites ?: return
        val row = current.rows.firstOrNull { it.category == category } ?: return
        if (!canLoadNextPage(row.endReached, row.isLoadingNextPage)) return
        val sessionId = authenticationRepository.getSharedPrefLoginSessionId() ?: return
        val accountId = authenticationRepository.getSharedPrefAccountId() ?: return

        fun updateRow(transform: (TvCategoryRowContent) -> TvCategoryRowContent) {
            val latest = _screenState.value as? TvCatalogScreenState.Favorites ?: return
            _screenState.value = latest.copy(rows = latest.rows.map { if (it.category == category) transform(it) else it })
        }

        updateRow { it.copy(isLoadingNextPage = true) }
        val nextPage = row.currentPage + 1
        rowLoadJobs[category]?.cancel()
        rowLoadJobs[category] = viewModelScope.launch {
            when (category) {
                is MovieCategory    -> when (val result = movieRepository.getFavoriteMovies(accountId, sessionId, nextPage)) {
                    is MoviesResult.Success -> updateRow {
                        it.copy(
                                items = mergePagedItems(it.items, catalogItemMapper.fromMovies(result.movies), nextPage) { item -> item.id },
                                currentPage = result.page,
                                endReached = isLastPage(result.page, result.totalPages),
                                isLoadingNextPage = false
                               )
                    }
                    is MoviesResult.Failure -> updateRow { it.copy(isLoadingNextPage = false) }
                }
                is TvSeriesCategory -> when (val result = tvSeriesRepository.getFavoriteTvSeries(accountId, sessionId, nextPage)) {
                    is TvSeriesResult.Success -> updateRow {
                        it.copy(
                                items = mergePagedItems(it.items, catalogItemMapper.fromTvSeries(result.tvSeries), nextPage) { item -> item.id },
                                currentPage = result.page,
                                endReached = isLastPage(result.page, result.totalPages),
                                isLoadingNextPage = false
                               )
                    }
                    is TvSeriesResult.Failure -> updateRow { it.copy(isLoadingNextPage = false) }
                }
                else                -> updateRow { it.copy(isLoadingNextPage = false) }
            }
        }
    }

    fun onNewsApiKeySaved(apiKey: String) {
        newsRepository.saveSharedPrefApiKey(apiKey)
        contentLoadJob?.cancel()
        contentLoadJob = viewModelScope.launch {
            val state = when (val result = newsRepository.getNews()) {
                is NewsResult.Success -> TvCatalogScreenState.News(
                        items = result.newsItems,
                        currentPage = result.page,
                        endReached = isLastPage(result.page, result.totalPages)
                                                                   )
                is NewsResult.Failure -> {
                    // A rejected key is discarded rather than left saved -
                    // otherwise every later News load (and every app restart)
                    // would keep silently failing against the same bad key.
                    if (result.isInvalidApiKey) {
                        newsRepository.removeSharedPrefApiKey()
                        _messageEvent.tryEmit(result.errorMessage)
                    }
                    TvCatalogScreenState.News(
                            keySetupRequired = result.isInvalidApiKey,
                            endReached = !result.isInvalidApiKey
                                             )
                }
            }
            _screenState.value = state
            destinationCache[TvCatalogDestination.News] = state
        }
    }

    private fun loadNews() {
        contentLoadJob?.cancel()
        if (!newsRepository.hasApiKey()) {
            _screenState.value = TvCatalogScreenState.News(keySetupRequired = true)
            return
        }
        _screenState.value = TvCatalogScreenState.News(isLoading = true)
        contentLoadJob = viewModelScope.launch {
            val state = when (val result = newsRepository.getNews()) {
                is NewsResult.Success -> TvCatalogScreenState.News(
                        items = result.newsItems,
                        currentPage = result.page,
                        endReached = isLastPage(result.page, result.totalPages)
                                                                   )
                is NewsResult.Failure -> if (result.isConnectivityFailure) {
                    // Not cached: revisiting the tab tries again.
                    _screenState.value = TvCatalogScreenState.Offline
                    return@launch
                } else {
                    TvCatalogScreenState.News(endReached = true)
                }
            }
            _screenState.value = state
            destinationCache[TvCatalogDestination.News] = state
        }
    }

    // Mirrors onRowEndReached's shape for the Movies/TvSeries rows - News
    // only ever has the one row, so there's no category to disambiguate.
    fun onNewsRowEndReached() {
        val current = _screenState.value as? TvCatalogScreenState.News ?: return
        if (!canLoadNextPage(current.endReached, current.isLoadingNextPage)) return

        _screenState.value = current.copy(isLoadingNextPage = true)
        val nextPage = current.currentPage + 1
        viewModelScope.launch {
            val latest = _screenState.value as? TvCatalogScreenState.News ?: return@launch
            val state = when (val result = newsRepository.getNews(nextPage)) {
                is NewsResult.Success -> latest.copy(
                        items = mergePagedItems(latest.items, result.newsItems, nextPage) { it.articleUrl },
                        currentPage = result.page,
                        endReached = isLastPage(result.page, result.totalPages),
                        isLoadingNextPage = false
                                                     )
                is NewsResult.Failure -> latest.copy(isLoadingNextPage = false)
            }
            _screenState.value = state
            destinationCache[TvCatalogDestination.News] = state
        }
    }
}
