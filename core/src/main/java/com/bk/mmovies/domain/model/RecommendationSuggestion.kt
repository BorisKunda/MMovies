package com.bk.mmovies.domain.model

/**
 * One AI-suggested title. [query] is deliberately a search term, not a
 * trusted catalog result on its own — an LLM asked to name real titles will
 * still occasionally fabricate a plausible-sounding one, so the caller is
 * expected to resolve [query] against a real catalog lookup (e.g.
 * [com.bk.mmovies.domain.repository.SearchRepository]) before showing
 * anything to the user, rather than rendering this directly.
 */
data class RecommendationSuggestion(
        val query: String,
        val mediaTypeHint: CatalogMediaType?,
        val reason: String
                                    )
