package com.bk.mmovies.data.mapper

import com.bk.mmovies.data.source.remote.dto.ArticleDto
import com.bk.mmovies.domain.model.NewsItem
import com.bk.mmovies.locale.LocaleMonitor
import javax.inject.Inject

class NewsMapper @Inject constructor(
        private val localeMonitor: LocaleMonitor
                                     ) {

    // An article with no url can't be opened by the user, which is the
    // whole point of surfacing it (NewsAPI never returns the full body), so
    // it's dropped rather than kept with a dead "Read full article" action.
    fun toModels(dtos: List<ArticleDto>): List<NewsItem> = dtos.mapNotNull { toModel(it) }

    fun toModel(dto: ArticleDto): NewsItem? {
        val articleUrl = dto.url?.takeIf { it.isNotBlank() }?.repairDoubleEscapedUnicode() ?: return null
        return NewsItem(
                title = dto.title ?: "",
                description = dto.description ?: "",
                articleUrl = articleUrl,
                imageUrl = dto.imageUrl ?: "",
                sourceName = dto.source?.name ?: "",
                author = dto.author ?: "",
                publishedAt = dto.publishedAt?.let { getFormattedDate(it) } ?: "",
                publishedAtIso = dto.publishedAt ?: "",
                content = dto.content ?: ""
                       )
    }

    private fun getFormattedDate(publishedAt: String): String =
            formatNewsApiDate(publishedAt, localeMonitor.currentLanguage.value.locale) ?: publishedAt

    // Some NewsAPI-scraped urls (seen from ABC News specifically) come back
    // with a JS unicode escape that's itself been escaped again, e.g.
    // "...id\\u003d123" instead of "...id=123" - the query param never gets
    // its real value, and a WebView loading that literal string just renders
    // blank with no error. One or more backslashes followed by 4 hex digits
    // is always meant to be that one unicode character, so this decodes it
    // regardless of how many extra backslashes got stacked on.
    private fun String.repairDoubleEscapedUnicode(): String =
            escapedUnicodeRegex.replace(this) { match -> match.groupValues[1].toInt(16).toChar().toString() }
}

private val escapedUnicodeRegex = Regex("""\\+u([0-9a-fA-F]{4})""")
