package com.bk.mmovies.demotv.ui.catalog.content.carousel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bk.mmovies.domain.model.CatalogItem
import com.bk.mmovies.domain.model.Category
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.domain.model.TvSeriesCategory
import com.bk.mmovies.demotv.theme.tvContentTextStyle
import com.bk.mmovies.demotv.theme.tvHeaderIconSize
import com.bk.mmovies.demotv.theme.tvHeaderTextStyle

private val iconToHeaderSpacing = 5.dp

@Composable
fun TvCatalogRow(
        category: Category,
        items: List<CatalogItem>,
        onItemPressed: (item: CatalogItem) -> Unit,
        onItemFocused: (item: CatalogItem) -> Unit = {},
        onEndReached: () -> Unit = {},
        // Shown under the header in place of the (empty) row, e.g. for a
        // favorites row the user hasn't added anything to yet.
        emptyMessage: String? = null,
        headerLabelRes: Int = category.labelRes,
        navRailFocusRequester: FocusRequester? = null
                ) {
    Column() {
        Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
           ) {
            Icon(
                    painter = painterResource(category.drawableRes),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(tvHeaderIconSize)
                )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                    text = stringResource(headerLabelRes),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    style = tvHeaderTextStyle
                )
        }
        // Spacer(modifier = Modifier.height(16.dp))
        if (items.isEmpty() && emptyMessage != null) {
            Text(
                    text = emptyMessage,
                    modifier = Modifier.padding(
                            start = 25.dp,
                            bottom = 10.dp
                                               ),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    style = tvContentTextStyle
                )
            return@Column
        }
        TvRow(
                rowId = "category_${category.categoryId}",
                list = items,
                onItemPressed = onItemPressed,
                onItemFocused = onItemFocused,
                onEndReached = onEndReached,
                isUpcoming = category == MovieCategory.UpcomingMovieCategory || category == TvSeriesCategory.UpcomingTvSeriesCategory,
                navRailFocusRequester = navRailFocusRequester
             )
    }
}

