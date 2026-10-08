package com.bk.mmovies.tv.ui.details

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bk.mmovies.domain.model.result.ToggleFavoriteResult
import com.bk.mmovies.locale.LocaleMonitor
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

const val FAVORITE_TAP_COOLDOWN_MS = 500L

// Mirrors app's DetailsViewModel<Id> - :tv can't depend on :app, so this
// shared load/retry/locale-reload/optimistic-favorite-revert base is
// duplicated here rather than shared. See the app version's kdoc for the
// reasoning behind each piece. Unlike the app version, this doesn't wait on
// an InternetMonitor before reloading on a locale change - that monitor is
// :app-only, and a plain reload attempt (which simply fails and can be
// retried if offline) is an acceptable simplification for the TV app.
abstract class DetailsTvViewModel<Id : Any>(
        localeMonitor: LocaleMonitor
                                            ) : ViewModel() {

    private var currentId: Id? = null
    private var loadJob: Job? = null

    init {
        viewModelScope.launch {
            localeMonitor.currentLanguage
                    .drop(1)
                    .collectLatest {
                        val id = currentId ?: return@collectLatest
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

    private var lastFavoriteTapAt = 0L

    // The star ignores taps for FAVORITE_TAP_COOLDOWN_MS after an accepted
    // one, so rapid taps can't leave several toggle requests in flight whose
    // responses could finish out of order.
    protected fun acceptFavoriteTap(): Boolean {
        val now = SystemClock.elapsedRealtime()
        if (now - lastFavoriteTapAt < FAVORITE_TAP_COOLDOWN_MS) return false
        lastFavoriteTapAt = now
        return true
    }

    protected abstract fun onReload()

    protected abstract suspend fun fetchDetails(id: Id)

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
