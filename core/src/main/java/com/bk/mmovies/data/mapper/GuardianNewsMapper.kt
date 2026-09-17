package com.bk.mmovies.data.mapper

import com.bk.mmovies.data.source.remote.dto.GuardianArticleDto
import com.bk.mmovies.domain.model.NewsItem
import com.bk.mmovies.locale.LocaleMonitor
import javax.inject.Inject

private val HTML_TAG_REGEX = Regex("<[^>]*>")

class GuardianNewsMapper @Inject constructor(
        private val localeMonitor: LocaleMonitor
                                             ) {

    fun toModels(dtos: List<GuardianArticleDto>): List<NewsItem> = dtos.mapNotNull { toModel(it) }

    fun toModel(dto: GuardianArticleDto): NewsItem? {
        val articleUrl = dto.webUrl?.takeIf { it.isNotBlank() } ?: return null
        // trailText (the Guardian's short summary) comes back as an HTML
        // fragment (e.g. "<p>...</p>") rather than plain text.
        val summary = dto.fields?.trailText?.let { HTML_TAG_REGEX.replace(it, "") }?.trim().orEmpty()
        return NewsItem(
                title = dto.webTitle ?: "",
                description = summary,
                articleUrl = articleUrl,
                imageUrl = dto.fields?.thumbnail ?: "",
                sourceName = GUARDIAN_SOURCE_NAME,
                author = dto.fields?.byline ?: "",
                publishedAt = dto.webPublicationDate?.let { getFormattedDate(it) } ?: "",
                publishedAtIso = dto.webPublicationDate ?: "",
                content = summary
                       )
    }

    // The Guardian's webPublicationDate is the same ISO-8601 UTC instant
    // shape NewsAPI uses ("2026-09-15T16:29:19Z"), so formatNewsApiDate
    // (internal to this package) is reused as-is rather than duplicated.
    private fun getFormattedDate(publishedAt: String): String =
            formatNewsApiDate(publishedAt, localeMonitor.currentLanguage.value.locale) ?: publishedAt

    private companion object {
        const val GUARDIAN_SOURCE_NAME = "The Guardian"
    }
}
