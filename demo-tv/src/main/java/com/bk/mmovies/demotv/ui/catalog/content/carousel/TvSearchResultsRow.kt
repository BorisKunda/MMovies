package com.bk.mmovies.demotv.ui.catalog.content.carousel

import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusRequester
import com.bk.mmovies.domain.model.CatalogItem

@Composable
fun TvSearchResultsRow(
        items: List<CatalogItem>,
        onItemPressed: (item: CatalogItem) -> Unit,
        navRailFocusRequester: FocusRequester? = null
                      ) {
    TvRow(
            rowId = "search",
            list = items,
            onItemPressed = onItemPressed,
            navRailFocusRequester = navRailFocusRequester,
            focusFirstOnEntry = true
         )
}
