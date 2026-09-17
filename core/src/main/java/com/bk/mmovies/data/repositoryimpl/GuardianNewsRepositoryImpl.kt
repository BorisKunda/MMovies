package com.bk.mmovies.data.repositoryimpl

import android.content.Context
import androidx.core.content.edit
import androidx.core.text.HtmlCompat
import com.bk.mmovies.core.R
import com.bk.mmovies.data.mapper.GuardianNewsMapper
import com.bk.mmovies.data.source.remote.GUARDIAN_API_KEY
import com.bk.mmovies.data.source.remote.GUARDIAN_ELEMENT_TYPE_IMAGE
import com.bk.mmovies.data.source.remote.NetworkManager
import com.bk.mmovies.data.source.remote.api.GuardianApi
import com.bk.mmovies.data.source.remote.dto.GuardianArticleDto
import com.bk.mmovies.data.source.remote.dto.GuardianItemResponseWrapperDto
import com.bk.mmovies.data.source.remote.dto.GuardianSearchResponseWrapperDto
import com.bk.mmovies.data.source.remote.result.ApiCallResult
import com.bk.mmovies.data.source.remote.result.NetworkError
import com.bk.mmovies.data.source.remote.result.isConnectivityFailure
import com.bk.mmovies.domain.model.result.NewsArticleContentResult
import com.bk.mmovies.domain.model.result.NewsResult
import com.bk.mmovies.domain.repository.NewsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.net.URI
import javax.inject.Inject

/**
 * :tv-only [NewsRepository] backed by the Guardian Content API instead of
 * NewsAPI (see [com.bk.mmovies.data.repositoryimpl.NewsRepositoryImpl], which
 * :app keeps using) - see TvNewsModule for the binding.
 */
class GuardianNewsRepositoryImpl @Inject constructor(
        private val api: GuardianApi,
        private val networkManager: NetworkManager,
        private val guardianNewsMapper: GuardianNewsMapper,
        @ApplicationContext private val context: Context
                                                      ) : NewsRepository {

    // Own, self-contained prefs file - deliberately separate from
    // NewsRepositoryImpl's "news_api_key_preferences" even though both
    // implementations can exist in the same :core module, so a Guardian key
    // saved here never collides with a NewsAPI key saved elsewhere.
    private val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    private val failureMessage: String
        get() = context.getString(R.string.error_news_load_failed)

    override fun saveSharedPrefApiKey(apiKey: String) {
        sharedPreferences.edit { putString(GUARDIAN_API_KEY_PREF, apiKey.trim()) }
    }

    override fun getSharedPrefApiKey(): String? {
        return sharedPreferences.getString(GUARDIAN_API_KEY_PREF, null)
    }

    override fun removeSharedPrefApiKey() {
        sharedPreferences.edit { remove(GUARDIAN_API_KEY_PREF) }
    }

    override fun hasApiKey(): Boolean {
        return !getSharedPrefApiKey().isNullOrBlank() || GUARDIAN_API_KEY.isNotBlank()
    }

    override suspend fun getNews(page: Int): NewsResult {
        val apiKey = getSharedPrefApiKey() ?: GUARDIAN_API_KEY
        val apiCallResult: ApiCallResult<GuardianSearchResponseWrapperDto> = networkManager.executeApiCall(
                "GetGuardianNews_page$page",
                apiCall = { -> api.search(page = page, apiKey = apiKey) })

        return toNewsResult(apiCallResult, page)
    }

    override suspend fun getNewsArticleContent(articleUrl: String): NewsArticleContentResult {
        val id = articleUrl.toGuardianContentId() ?: return NewsArticleContentResult.Failure(failureMessage)
        val apiKey = getSharedPrefApiKey() ?: GUARDIAN_API_KEY
        val apiCallResult: ApiCallResult<GuardianItemResponseWrapperDto> = networkManager.executeApiCall(
                "GetGuardianArticleContent",
                apiCall = { -> api.getArticle(id = id, apiKey = apiKey) })

        return when (apiCallResult) {
            is ApiCallResult.Success -> {
                val content = apiCallResult.data.response?.content
                val bodyHtml = content?.fields?.body
                if (bodyHtml.isNullOrBlank()) {
                    NewsArticleContentResult.Failure(failureMessage)
                } else {
                    // HtmlCompat.fromHtml handles entity decoding and paragraph/br
                    // spacing correctly, unlike a hand-rolled tag-stripping
                    // regex - the body is nested markup (multiple <p>,
                    // possibly <a>/<em>/<blockquote>), not the single flat
                    // trailText snippet GuardianNewsMapper strips. Used instead of
                    // android.text.Html directly since that class's fromHtml(String, Int)
                    // overload requires API 24, above this project's minSdk 23.
                    val plainText = HtmlCompat.fromHtml(bodyHtml, HtmlCompat.FROM_HTML_MODE_LEGACY).toString().trim()
                    NewsArticleContentResult.Success(plainText, content.largestImageUrl())
                }
            }
            is ApiCallResult.Failure -> NewsArticleContentResult.Failure(failureMessage, apiCallResult.error.isConnectivityFailure)
        }
    }

    // The thumbnail used for the news row card is a small ~140px crop -
    // show-elements=image (requested by getArticle) returns the same picture
    // at every size Guardian's own site uses, so the widest one is picked out
    // for the details screen's much larger hero image instead of upscaling
    // that thumbnail.
    private fun GuardianArticleDto.largestImageUrl(): String =
            elements
                    ?.firstOrNull { it.type == GUARDIAN_ELEMENT_TYPE_IMAGE }
                    ?.assets
                    ?.maxByOrNull { it.typeData?.width?.toIntOrNull() ?: 0 }
                    ?.file
                    ?: ""

    // The Guardian's content id is exactly an article's webUrl path with the
    // scheme/host stripped (e.g. "film/2026/sep/16/..."), so no separate id
    // needs to be threaded through NewsItem just for this one lookup.
    private fun String.toGuardianContentId(): String? =
            try {
                URI(this).path?.removePrefix("/")?.takeIf { it.isNotBlank() }
            } catch (e: Exception) {
                null
            }

    private fun toNewsResult(
            apiCallResult: ApiCallResult<GuardianSearchResponseWrapperDto>,
            page: Int
                             ): NewsResult = when (apiCallResult) {
        is ApiCallResult.Success -> {
            val response = apiCallResult.data.response
            NewsResult.Success(
                    // Unlike NewsAPI's broad country+category sweep, film|
                    // tv-and-radio is already editorially scoped to movies/TV
                    // - re-applying the keyword allowlist here would drop
                    // legitimate reviews/articles whose titles don't happen
                    // to contain one of those literal words. tv-and-radio
                    // does also carry pure radio content with nothing to do
                    // with TV, which this doesn't filter out - a real but
                    // minor amount of noise, not worth the false-negative
                    // risk of blunt title-keyword filtering to remove.
                    newsItems = guardianNewsMapper.toModels(response?.results.orEmpty()),
                    page = page,
                    totalPages = (response?.pages ?: page).coerceAtLeast(1)
                              )
        }
        is ApiCallResult.Failure -> {
            val error = apiCallResult.error
            // A 401 here means the key itself was rejected (Guardian's own
            // response for an invalid api-key param) rather than a transient
            // network/server issue - callers use isInvalidApiKey to decide
            // whether to discard a just-saved key instead of leaving a bad
            // one in place.
            if (error is NetworkError.HttpError && error.responseCode == 401) {
                NewsResult.Failure(context.getString(R.string.toast_invalid_api_key), isInvalidApiKey = true)
            } else {
                NewsResult.Failure(failureMessage, error.isConnectivityFailure)
            }
        }
    }

    private companion object {
        const val PREFERENCES_NAME = "guardian_api_key_preferences"
        const val GUARDIAN_API_KEY_PREF = "guardian_api_key"
    }
}
