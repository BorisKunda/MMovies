package com.bk.mmovies.domain.model

// NewsAPI's country+category filter alone still lets through general
// entertainment/celebrity gossip with nothing to do with film/TV production -
// this keyword allowlist is the extra gate: a title survives if it contains
// at least one of these (case-insensitive), edited here directly when the
// list needs to change.
val NEWS_TITLE_REQUIRED_KEYWORDS: List<String> = listOf(
        "actor", "actress", "actors", "actresses",
        "cast", "casting",
        "filmmaker", "filmmakers",
        "director", "directors",
        "producer", "producers",
        "screenwriter", "screenwriters",
        "film", "films", "movie", "movies", "cinema",
        "television", "tv", "tv series",
        "episode", "episodes", "finale",
        "trailer", "trailers", "teaser", "teasers",
        "sequel", "sequels", "prequel", "prequels", "remake", "remakes",
        "streaming", "streamer", "streamers", "streaming service", "streaming services",
        "Netflix", "Disney", "Disney+", "Hulu", "Amazon Prime", "Prime Video",
        "HBO", "HBO Max", "Apple TV+", "Paramount", "Paramount+", "Peacock", "MGM+",
        "Warner Bros", "Universal Pictures", "Sony Pictures", "Columbia",
        "Lionsgate", "A24", "Miramax", "DreamWorks", "Pixar",
        "Hollywood",
        "Oscar", "Oscars", "Academy Awards", "Emmy", "Emmys", "Golden Globes", "BAFTA",
        "Cannes", "Sundance", "film festival", "film festivals",
        "new season", "new series", "new movie", "new film", "new show",
        "limited series", "miniseries", "original series", "original movie", "original film",
        "box office",
        "first look", "official trailer", "official teaser", "behind the scenes"
                                                        )

// Applied at the repository layer (see NewsRepositoryImpl) rather than in the
// ViewModel, since it's a source-data acceptance rule, not a UI concern -
// pagination math (totalPages/endReached) still runs off the unfiltered
// totalResults NewsAPI reports, so a filtered page can legitimately come back
// shorter than pageSize without meaning the feed has ended.
fun List<NewsItem>.filterByRequiredTitleKeywords(
        keywords: List<String> = NEWS_TITLE_REQUIRED_KEYWORDS
                                                 ): List<NewsItem> =
        filter { item -> keywords.any { keyword -> item.title.containsWholeWord(keyword) } }

// A plain String.contains would let a short keyword like "cast" match inside
// an unrelated word (NewsAPI often appends the source name straight into the
// title, e.g. "... - StyleCaster" - which contains "caster", which contains
// "cast"). Requiring a non-alphanumeric (or absent) character on both sides
// of the match keeps multi-word/symbol keywords like "Disney+" or "box
// office" working while rejecting that kind of embedded false match.
private fun String.containsWholeWord(keyword: String): Boolean {
    var searchStart = 0
    while (true) {
        val index = indexOf(keyword, searchStart, ignoreCase = true)
        if (index == -1) return false
        val before = index - 1
        val after = index + keyword.length
        val beforeIsBoundary = before < 0 || !this[before].isLetterOrDigit()
        val afterIsBoundary = after >= length || !this[after].isLetterOrDigit()
        if (beforeIsBoundary && afterIsBoundary) return true
        searchStart = index + 1
    }
}
