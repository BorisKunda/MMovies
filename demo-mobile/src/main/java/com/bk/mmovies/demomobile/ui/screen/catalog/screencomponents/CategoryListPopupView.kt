package com.bk.mmovies.demomobile.ui.screen.catalog.screencomponents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bk.mmovies.demomobile.R
import com.bk.mmovies.domain.model.Category
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.domain.model.TvSeriesCategory

private val sheetHorizontalPadding = 16.dp
private val headerBottomPadding = 16.dp
private val sheetBottomPadding = 24.dp
private val itemSpacing = 12.dp
private val itemHeight = 56.dp
private val itemHorizontalPadding = 16.dp
private val iconEndPadding = 12.dp
private val itemCornerShape = 12.dp
private val itemIconSize = 20.dp

private const val UNAVAILABLE_CONTAINER_ALPHA = 0.06f
private const val UNAVAILABLE_CONTENT_ALPHA = 0.75f
private const val UNAVAILABLE_LIST_ALPHA = 0.38f

enum class CategoryType {
    MOVIE,
    TV
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListPopupView(
        categoryType: CategoryType,
        onAnyInteraction: () -> Unit,
        state: SheetState
                         ) {
    ModalBottomSheet(
            onDismissRequest = onAnyInteraction,
            sheetState = state,
            containerColor = MaterialTheme.colorScheme.surface
                    ) {
        Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onAnyInteraction
                                  )
                        .padding(
                                start = sheetHorizontalPadding,
                                end = sheetHorizontalPadding,
                                bottom = sheetBottomPadding
                                )
              ) {
            Text(
                    text = stringResource(R.string.categories_title),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = headerBottomPadding)
                )

            Column(
                    modifier = Modifier
                            .fillMaxWidth()
                            .alpha(UNAVAILABLE_LIST_ALPHA),
                    verticalArrangement = Arrangement.spacedBy(itemSpacing)
                  ) {
                val categories = when (categoryType) {
                    CategoryType.MOVIE -> movieCategoriesList
                    CategoryType.TV    -> tvCategoriesList
                }

                categories.forEach { category ->
                    CategoryRow(category = category)
                }
            }
        }
    }
}

@Composable
private fun CategoryRow(category: Category) {
    val itemShape = RoundedCornerShape(itemCornerShape)
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = itemHeight)
                    .clip(itemShape)
                    .background(onSurfaceColor.copy(alpha = UNAVAILABLE_CONTAINER_ALPHA))
                    .padding(horizontal = itemHorizontalPadding),
            verticalAlignment = Alignment.CenterVertically
       ) {
        Icon(
                painter = painterResource(category.drawableRes),
                contentDescription = null,
                tint = onSurfaceColor.copy(alpha = UNAVAILABLE_CONTENT_ALPHA),
                modifier = Modifier.size(itemIconSize)
            )
        Spacer(modifier = Modifier.width(iconEndPadding))
        Text(
                text = stringResource(category.labelRes),
                color = onSurfaceColor.copy(alpha = UNAVAILABLE_CONTENT_ALPHA),
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
    }
}

private val movieCategoriesList = listOf(
        MovieCategory.PopularMovieCategory,
        MovieCategory.UpcomingMovieCategory,
        MovieCategory.NowPlayingMovieCategory,
        MovieCategory.TopRatedMovieCategory,
        MovieCategory.FavoritesMovieCategory
                                        )
private val tvCategoriesList = listOf(
        TvSeriesCategory.PopularTvSeriesCategory,
        TvSeriesCategory.AiringTodayTvSeriesCategory,
        TvSeriesCategory.OnTVTvSeriesCategory,
        TvSeriesCategory.TopRatedTvSeriesCategory,
        TvSeriesCategory.UpcomingTvSeriesCategory,
        TvSeriesCategory.FavoritesTvSeriesCategory
                                     )
