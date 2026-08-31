package com.bk.mmovies.ui.screen.catalog.screencomponents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.bk.mmovies.R

// Matches CatalogListView's cardPaddingHorizontal so the search bar's edges
// line up exactly with each catalog card's edges below it, making it read
// as the same width as a row's card.
private val searchBarHorizontalMargin = 12.dp
private val searchBarTopPadding = 8.dp
private val searchBarCornerShape = 24.dp
private val searchBarContentHorizontalPadding = 16.dp
private val searchBarContentVerticalPadding = 12.dp
private val searchIconSize = 18.dp
private val searchIconEndPadding = 10.dp

private const val SEARCH_BAR_HINT_ALPHA = 0.6f

// A solid filled capsule (surfaceVariant, no border) so it reads as a
// prominent tappable search field rather than blending into the chip row
// below it. Tapping it opens the full Search screen rather than doing
// inline search.
@Composable
fun CatalogSearchBar(onClick: () -> Unit) {
    val hint = stringResource(R.string.search_field_hint)
    val shape = RoundedCornerShape(searchBarCornerShape)

    Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                            start = searchBarHorizontalMargin,
                            end = searchBarHorizontalMargin,
                            top = searchBarTopPadding
                            )
                    .clip(shape)
                    .background(MaterialTheme.colorScheme.surfaceVariant, shape)
                    .clickable(
                            role = Role.Button,
                            onClickLabel = stringResource(R.string.search_action)
                              ) { onClick() }
                    .padding(
                            horizontal = searchBarContentHorizontalPadding,
                            vertical = searchBarContentVerticalPadding
                            ),
            verticalAlignment = Alignment.CenterVertically
       ) {
        Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = SEARCH_BAR_HINT_ALPHA),
                modifier = Modifier.size(searchIconSize)
            )
        Spacer(modifier = Modifier.width(searchIconEndPadding))
        Text(
                text = hint,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = SEARCH_BAR_HINT_ALPHA),
                style = MaterialTheme.typography.labelLarge
            )
    }
}
