package com.bk.mmovies.ui.screen.news

import android.content.Context
import android.content.Intent
import com.bk.mmovies.R
import com.bk.mmovies.domain.model.NewsItem

/**
 * Launches the system share sheet (WhatsApp, Messages, Gmail, etc. - whatever
 * the device has installed) with the article's title and link. ACTION_SEND
 * with an explicit chooser rather than targeting a single package (e.g.
 * WhatsApp's own package) so this keeps working regardless of which apps the
 * user actually has installed.
 */
fun shareNewsItem(context: Context, newsItem: NewsItem) {
    val shareText = if (newsItem.title.isNotBlank()) {
        "${newsItem.title}\n${newsItem.articleUrl}"
    } else {
        newsItem.articleUrl
    }
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    context.startActivity(
            Intent.createChooser(sendIntent, context.getString(R.string.news_share_chooser_title))
                          )
}
