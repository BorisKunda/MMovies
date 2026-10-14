package com.bk.mmovies.tv.ui.catalog.content.carousel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bk.mmovies.domain.model.CatalogItem
import com.bk.mmovies.tv.R
import com.bk.mmovies.tv.theme.tvHeaderTextStyle
import com.bk.mmovies.tv.ui.catalog.content.recommendations.AiIcon

@Composable
fun TvRecommendationsRow(
        items: List<CatalogItem>,
        onItemPressed: (item: CatalogItem) -> Unit,
        navRailFocusRequester: FocusRequester? = null
                        ) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AiIcon()
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                    text = stringResource(R.string.ai_recommendations_title),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    style = tvHeaderTextStyle
                )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TvRow(
                rowId = "recommendations",
                list = items,
                onItemPressed = onItemPressed,
                navRailFocusRequester = navRailFocusRequester
             )
    }
}
