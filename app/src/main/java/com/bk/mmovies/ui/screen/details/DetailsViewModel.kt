package com.bk.mmovies.ui.screen.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bk.mmovies.connectivity.InternetMonitor
import com.bk.mmovies.domain.model.result.ToggleFavoriteResult
import com.bk.mmovies.locale.LocaleMonitor
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Shared by every details screen's ViewModel (movie, TV series, season):
 * load-by-id with a no-op guard on an unchanged id, [retry], cancelling a
 * still-in-flight load before starting a new one, and a reload triggered by
 * an app-language change. The locale reload waits for connectivity first,
 * since a locale switch often lands right as the OS is mid-reconnect (seen
 * as an immediate UnknownHostException).
 *
 * [Id] is whatever identifies "what to load" — a single TMDB id for movies
 * and TV series, or a (seriesId, seasonNumber) pair for a season.
 */
abstract class DetailsViewModel<Id : Any>(
        localeMonitor: LocaleMonitor,
        internetMonitor: InternetMonitor
                                          ) : ViewModel() {

    private var currentId: Id? = null
    private var loadJob: Job? = null

    init {
        viewModelScope.launch {
            localeMonitor.currentLanguage
                    .drop(1)
                    .collectLatest {
                        val id = currentId ?: return@collectLatest
                        internetMonitor.isInternetAvailable.first { it }
                        onReload()
                        fetch(id)
                    }
        }
    }

    protected fun load(id: Id) {
        if (currentId == id) return
        currentId = id
        fetch(id)
    }

    fun retry() {
        val id = currentId ?: return
        onReload()
        fetch(id)
    }

    private fun fetch(id: Id) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            fetchDetails(id)
        }
    }

    /** Reset the screen state back to its Loading variant. */
    protected abstract fun onReload()

    /** Fetch [id]'s details and update the screen state (Content/Error). */
    protected abstract suspend fun fetchDetails(id: Id)

    // --- Optimistic favorite toggle -----------------------------------
    //
    // Shared by every details screen that lets the user favorite/unfavorite
    // from here (movie, TV series — a season has no favorite of its own).
    // [content] is the Content state's payload (already known to be current
    // at the moment the star was tapped); the revert on failure re-reads
    // [currentContent] rather than reapplying that snapshot, since a locale
    // reload or retry() can land while the toggle call is still in flight,
    // and blindly restoring the snapshot would throw that fresher content
    // away.

    protected fun <Content : Any> revertFavoriteOnFailure(
            content: Content,
            previousIsFavorite: Boolean,
            contentId: (Content) -> Int,
            withFavorite: (Content, Boolean) -> Content,
            currentContent: () -> Content?,
            applyContent: (Content) -> Unit,
            onFailureMessage: (String) -> Unit,
            result: ToggleFavoriteResult
                                                          ) {
        if (result !is ToggleFavoriteResult.Failure) return
        val latest = currentContent()
        if (latest != null && contentId(latest) == contentId(content)) {
            applyContent(withFavorite(latest, previousIsFavorite))
        }
        onFailureMessage(result.errorMessage)
    }
}
