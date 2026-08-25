package com.bk.mmovies.ui.screen.catalog.screencomponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.bk.mmovies.R

// Matches CategorySelector's own start/end margins so this lines up with
// the chips below it.
private val searchBarHorizontalMargin = 28.dp
private val searchBarTopPadding = 8.dp
private val searchIconSize = 16.dp
private val searchIconEndPadding = 8.dp

private const val SEARCH_BAR_HINT_ALPHA = 0.6f
private const val SEARCH_BAR_DIVIDER_ALPHA = 0.18f

// A plain underlined row rather than a filled/bordered capsule, so it reads
// as a compact search affordance instead of a second chip-height band.
// Tapping it opens the full Search screen rather than doing inline search.
@Composable
fun CatalogSearchBar(onClick: () -> Unit) {
    val hint = stringResource(R.string.search_field_hint)

    Row(
            // No bottom padding: the divider sits directly under the text,
            // like an underlined field's indicator, so the gap it leaves
            // before CatalogHeaderBar is exactly that bar's own top margin —
            // matching tabContentTopSpacing below the header.
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                            start = searchBarHorizontalMargin,
                            end = searchBarHorizontalMargin,
                            top = searchBarTopPadding
                            )
                    .clickable(
                            role = Role.Button,
                            onClickLabel = stringResource(R.string.search_action)
                              ) { onClick() },
            verticalAlignment = Alignment.CenterVertically
       ) {
        Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = SEARCH_BAR_HINT_ALPHA),
                modifier = Modifier.size(searchIconSize)
            )
        Spacer(modifier = Modifier.width(searchIconEndPadding))
        Text(
                text = hint,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = SEARCH_BAR_HINT_ALPHA),
                style = MaterialTheme.typography.labelLarge
            )
    }
    HorizontalDivider(
            modifier = Modifier.padding(horizontal = searchBarHorizontalMargin),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = SEARCH_BAR_DIVIDER_ALPHA)
                      )
}
