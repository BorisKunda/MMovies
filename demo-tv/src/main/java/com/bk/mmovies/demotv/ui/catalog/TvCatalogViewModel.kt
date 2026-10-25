package com.bk.mmovies.demotv.ui.catalog

import android.app.Application
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Info
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.AndroidViewModel
import com.bk.mmovies.demotv.R
import com.bk.mmovies.demotv.mock.mockArticles
import com.bk.mmovies.demotv.mock.mockMovieCatalogItems
import com.bk.mmovies.demotv.mock.mockTvSeriesCatalogItems
import com.bk.mmovies.domain.model.CatalogItem
import com.bk.mmovies.domain.model.Category
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.domain.model.NewsItem
import com.bk.mmovies.domain.model.TvSeriesCategory
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface TvNavIcon {
    data class Vector(val imageVector: ImageVector) : TvNavIcon
    data class Drawable(@DrawableRes val resId: Int) : TvNavIcon
}

data class TvUserProfileUiState(
        val name: String = "",
        val imageUrl: String = "",
        val isGuest: Boolean = true
                                )

enum class TvCatalogDestination(@StringRes val labelResId: Int, val icon: TvNavIcon) {
    Movies(R.string.nav_movies, TvNavIcon.Vector(Icons.Default.Movie)),
    TvSeries(R.string.nav_tv_series, TvNavIcon.Vector(Icons.Default.LiveTv)),
    Search(R.string.nav_search, TvNavIcon.Vector(Icons.Default.Search)),
    News(R.string.nav_news, TvNavIcon.Vector(Icons.AutoMirrored.Filled.Article)),
    About(R.string.nav_about, TvNavIcon.Vector(Icons.Outlined.Info))
}

data class TvCategoryRowContent(
        val category: Category,
        val items: List<CatalogItem>
                                )

sealed interface TvCatalogScreenState {
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

    data class News(val items: List<NewsItem> = emptyList()) : TvCatalogScreenState

    data class Search(
            val query: String = "",
            val items: List<CatalogItem> = emptyList(),
            val focusedItem: CatalogItem? = null
                      ) : TvCatalogScreenState

    data object About : TvCatalogScreenState
}

private const val ROW_ROTATION_STEP = 3

class TvCatalogViewModel(application: Application) : AndroidViewModel(application) {

    private val searchableItems: List<CatalogItem> = mockMovieCatalogItems + mockTvSeriesCatalogItems

    private val _selectedDestination = MutableStateFlow(TvCatalogDestination.Movies)
    val selectedDestination: StateFlow<TvCatalogDestination> = _selectedDestination.asStateFlow()

    private val _screenState = MutableStateFlow<TvCatalogScreenState>(moviesState())
    val screenState: StateFlow<TvCatalogScreenState> = _screenState.asStateFlow()

    private val _messageEvent = MutableSharedFlow<Int>(extraBufferCapacity = 1)
    val messageEvent: SharedFlow<Int> = _messageEvent.asSharedFlow()

    val userProfileState: StateFlow<TvUserProfileUiState> =
            MutableStateFlow(TvUserProfileUiState(isGuest = true)).asStateFlow()

    fun onDestinationSelected(destination: TvCatalogDestination) {
        if (destination == _selectedDestination.value) return
        _selectedDestination.value = destination
        _screenState.value = when (destination) {
            TvCatalogDestination.Movies   -> moviesState()
            TvCatalogDestination.TvSeries -> tvSeriesState()
            TvCatalogDestination.Search   -> TvCatalogScreenState.Search()
            TvCatalogDestination.News     -> TvCatalogScreenState.News(newsItems())
            TvCatalogDestination.About    -> TvCatalogScreenState.About
        }
    }

    fun onItemFocused(item: CatalogItem, category: Category? = null) {
        _screenState.value = when (val current = _screenState.value) {
            is TvCatalogScreenState.Movies   -> current.copy(focusedItem = item, focusedCategory = category)
            is TvCatalogScreenState.TvSeries -> current.copy(focusedItem = item, focusedCategory = category)
            is TvCatalogScreenState.Search   -> current.copy(focusedItem = item)
            else                             -> current
        }
    }

    fun onDialogDismissed() {
        _screenState.value = when (val current = _screenState.value) {
            is TvCatalogScreenState.Movies   -> current.copy(focusedItem = null, focusedCategory = null)
            is TvCatalogScreenState.TvSeries -> current.copy(focusedItem = null, focusedCategory = null)
            is TvCatalogScreenState.Search   -> current.copy(focusedItem = null)
            else                             -> current
        }
    }

    fun onSearchQueryChanged(query: String) {
        val current = _screenState.value as? TvCatalogScreenState.Search ?: return
        val trimmedQuery = query.trim()
        val matches = if (trimmedQuery.isEmpty()) {
            emptyList()
        } else {
            searchableItems.filter { it.title.contains(trimmedQuery, ignoreCase = true) }
        }
        _screenState.value = current.copy(query = query, items = matches)
    }

    fun onFeaturedFavoriteClicked() {
        _messageEvent.tryEmit(R.string.demo_favorites_unavailable)
    }

    fun onProfileClicked() {
        _messageEvent.tryEmit(R.string.demo_user_profiles_unavailable)
    }

    private fun moviesState() = TvCatalogScreenState.Movies(
            rows = listOf(
                    TvCategoryRowContent(MovieCategory.PopularMovieCategory, mockMovieCatalogItems),
                    TvCategoryRowContent(MovieCategory.NowPlayingMovieCategory, mockMovieCatalogItems.rotatedBy(ROW_ROTATION_STEP)),
                    TvCategoryRowContent(MovieCategory.UpcomingMovieCategory, mockMovieCatalogItems.reversed()),
                    TvCategoryRowContent(MovieCategory.TopRatedMovieCategory, mockMovieCatalogItems.sortedByDescending { it.rating })
                         )
                                                          )

    private fun tvSeriesState() = TvCatalogScreenState.TvSeries(
            rows = listOf(
                    TvCategoryRowContent(TvSeriesCategory.PopularTvSeriesCategory, mockTvSeriesCatalogItems),
                    TvCategoryRowContent(TvSeriesCategory.OnTVTvSeriesCategory, mockTvSeriesCatalogItems.rotatedBy(ROW_ROTATION_STEP)),
                    TvCategoryRowContent(TvSeriesCategory.UpcomingTvSeriesCategory, mockTvSeriesCatalogItems.reversed()),
                    TvCategoryRowContent(TvSeriesCategory.TopRatedTvSeriesCategory, mockTvSeriesCatalogItems.sortedByDescending { it.rating })
                         )
                                                              )

    private fun newsItems(): List<NewsItem> = mockArticles(getApplication<Application>().resources)

    private fun <T> List<T>.rotatedBy(step: Int): List<T> = drop(step) + take(step)
}
