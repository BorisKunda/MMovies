package com.bk.mmovies.domain.model

// Shared by CatalogViewModel and SearchViewModel, whose per-screen ui-state
// shapes differ too much to unify outright, but whose page-merge/guard rules
// are identical and easy to get subtly wrong twice.

// TMDB can drift an item onto more than one page while a list is being
// paged through (e.g. it reorders between requests), and a duplicate key
// crashes LazyColumn's keyed items() with "Key ... was already used" —
// drop the repeat. Page 1 always replaces rather than appends.
fun <T> mergePagedItems(existingItems: List<T>, newItems: List<T>, page: Int, keySelector: (T) -> Any): List<T> {
    return if (page > 1) (existingItems + newItems).distinctBy(keySelector) else newItems
}

fun isLastPage(page: Int, totalPages: Int): Boolean = page >= totalPages

fun canLoadNextPage(endReached: Boolean, isLoadingNextPage: Boolean): Boolean = !endReached && !isLoadingNextPage
