package com.bk.mmovies.demomobile.ui.screen.catalog

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.bk.mmovies.demomobile.R
import com.bk.mmovies.demomobile.ui.screen.catalog.screencomponents.CatalogBottomTab
import com.bk.mmovies.demomobile.ui.screen.catalog.screencomponents.CategoryType
import com.bk.mmovies.domain.model.CatalogItem
import com.bk.mmovies.domain.model.CatalogMediaType
import com.bk.mmovies.domain.model.Category
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.domain.model.TvSeriesCategory
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

data class MockCatalogScreenState(
        val selectedTab: CatalogBottomTab,
        val selectedCategory: Category,
        @StringRes val categoryLabelRes: Int,
        val categoryType: CategoryType,
        val catalogItems: List<CatalogItem>,
        val isCategorySelectorVisible: Boolean
                                 )

class MockCatalogViewModel : ViewModel() {

    private val _screenState = MutableStateFlow(screenStateFor(CatalogBottomTab.Movies))
    val screenState: StateFlow<MockCatalogScreenState> = _screenState.asStateFlow()

    private val _messageEvent = MutableSharedFlow<Int>(extraBufferCapacity = 1)
    val messageEvent: SharedFlow<Int> = _messageEvent.asSharedFlow()

    private val _goToMovieDetailsNavEvent = MutableSharedFlow<Int>(extraBufferCapacity = 1)
    val goToMovieDetailsNavEvent: SharedFlow<Int> = _goToMovieDetailsNavEvent.asSharedFlow()

    private val _goToTvSeriesDetailsNavEvent = MutableSharedFlow<Int>(extraBufferCapacity = 1)
    val goToTvSeriesDetailsNavEvent: SharedFlow<Int> = _goToTvSeriesDetailsNavEvent.asSharedFlow()

    fun onFavoriteClicked(catalogItem: CatalogItem) {
        _messageEvent.tryEmit(R.string.demo_favorites_unavailable)
    }

    fun onCatalogItemClicked(itemId: Int) {
        val clickedItem = _screenState.value.catalogItems.firstOrNull { it.id == itemId } ?: return
        when (clickedItem.mediaType) {
            CatalogMediaType.MOVIE     -> _goToMovieDetailsNavEvent.tryEmit(itemId)
            CatalogMediaType.TV_SERIES -> _goToTvSeriesDetailsNavEvent.tryEmit(itemId)
        }
    }

    fun onTabSelected(tab: CatalogBottomTab) {
        _screenState.value = screenStateFor(tab)
    }

    fun onCategoryPopupInteracted() {
        _messageEvent.tryEmit(R.string.demo_categories_unavailable)
    }

    fun onUserProfileClicked() {
        _messageEvent.tryEmit(R.string.demo_user_profiles_unavailable)
    }

    private fun screenStateFor(tab: CatalogBottomTab): MockCatalogScreenState = when (tab) {
        CatalogBottomTab.Movies   -> MockCatalogScreenState(
                selectedTab = tab,
                selectedCategory = MovieCategory.PopularMovieCategory,
                categoryLabelRes = R.string.tab_movies,
                categoryType = CategoryType.MOVIE,
                catalogItems = mockMovieCatalogItems,
                isCategorySelectorVisible = true
                                                           )

        CatalogBottomTab.TvSeries -> MockCatalogScreenState(
                selectedTab = tab,
                selectedCategory = TvSeriesCategory.PopularTvSeriesCategory,
                categoryLabelRes = R.string.tab_tv_series,
                categoryType = CategoryType.TV,
                catalogItems = mockTvSeriesCatalogItems,
                isCategorySelectorVisible = true
                                                           )

        CatalogBottomTab.News     -> MockCatalogScreenState(
                selectedTab = tab,
                selectedCategory = MovieCategory.PopularMovieCategory,
                categoryLabelRes = R.string.tab_movies,
                categoryType = CategoryType.MOVIE,
                catalogItems = emptyList(),
                isCategorySelectorVisible = false
                                                           )
    }
}
