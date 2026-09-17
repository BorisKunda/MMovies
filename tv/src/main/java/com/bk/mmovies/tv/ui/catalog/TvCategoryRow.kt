package com.bk.mmovies.tv.ui.catalog

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bk.mmovies.domain.model.CatalogItem
import com.bk.mmovies.domain.model.Category
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.domain.model.TvSeriesCategory
import com.bk.mmovies.tv.ui.component.TvRow

@Composable
fun CategoryRow(
        category: Category,
        items: List<CatalogItem>,
        onItemPressed: (item: CatalogItem) -> Unit,
        onItemFocused: (item: CatalogItem) -> Unit = {},
        onEndReached: () -> Unit = {}
               ) {
    Column() {
        Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
            Icon(
                    painter = painterResource(category.drawableRes),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(32.dp)
                )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                    text = stringResource(category.labelRes),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall
                )
        }
        Spacer(modifier = Modifier.height(16.dp))
        TvRow(
                rowId = "category_${category.categoryId}",
                list = items,
                onItemPressed = onItemPressed,
                onItemFocused = onItemFocused,
                onEndReached = onEndReached,
                isUpcoming = category == MovieCategory.UpcomingMovieCategory || category == TvSeriesCategory.UpcomingTvSeriesCategory
             )
    }
}

