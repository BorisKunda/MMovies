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
        val articleUrl = dto.url?.takeIf { it.isNotBlank() } ?: return null
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
}
