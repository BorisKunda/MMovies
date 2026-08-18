package com.bk.mmovies.ui.screen.movies.screencomponents

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bk.mmovies.R
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.ui.theme.MMoviesTheme
import com.bk.mmovies.util.logDebug

private const val TAG = "CategoryListPopupView"

// Aligned with the spacing/shape scale the rest of the app uses: 16dp screen
// gutters (screenPadding / rowPaddingHorizontal), 12dp gaps and corners
// (cardCornerShape, spacerPadding), 24dp section breaks, 1dp borders.
private val sheetHorizontalPadding = 16.dp
private val headerBottomPadding = 16.dp
private val sheetBottomPadding = 24.dp
private val itemSpacing = 12.dp
private val itemHeight = 56.dp
private val itemHorizontalPadding = 16.dp
private val iconEndPadding = 12.dp
private val itemCornerShape = 12.dp
private val itemBorderWidth = 1.dp
private val itemIconSize = 20.dp
private val checkIconSize = 20.dp

private const val SELECTED_CONTAINER_ALPHA = 0.14f
private const val UNSELECTED_CONTAINER_ALPHA = 0.06f
private const val UNSELECTED_CONTENT_ALPHA = 0.75f

// A full-opacity border was far brighter than anything else in the app (every
// other border sits at 0.14-0.18 alpha). The check icon already carries the
// selected state, so the outline only needs to support it.
private const val SELECTED_BORDER_ALPHA = 0.45f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListPopupView(
        lastSelectedCategory: MovieCategory,
        onNewCategorySelected: (newSelectedCategory: MovieCategory) -> Unit,
        onDismiss: () -> Unit,
        state: SheetState
                         ) {
    ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = state,
            containerColor = MaterialTheme.colorScheme.surface
                    ) {
        Column(
                modifier = Modifier
                        .fillMaxWidth()
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
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(itemSpacing)
                  ) {
                movieCategoriesList.forEach { category ->
                    CategoryRow(
                            category = category,
                            isSelected = category == lastSelectedCategory,
                            onClick = { onNewCategorySelected(category) }
                               )
                }
            }
        }
    }

    DisposableEffect(Unit) {
        logDebug(TAG, "LAUNCHED")
        onDispose {
            logDebug(TAG, "DISPOSED")
        }
    }
}

@Composable
private fun CategoryRow(
        category: MovieCategory,
        isSelected: Boolean,
        onClick: () -> Unit
                       ) {
    val itemShape = RoundedCornerShape(itemCornerShape)
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    val containerColor by animateColorAsState(
            targetValue = onSurfaceColor.copy(
                    alpha = if (isSelected) SELECTED_CONTAINER_ALPHA
                    else UNSELECTED_CONTAINER_ALPHA
                                             ),
            label = "categoryContainerColor"
                                             )
    val borderColor by animateColorAsState(
            targetValue = if (isSelected) onSurfaceColor.copy(alpha = SELECTED_BORDER_ALPHA)
            else Color.Transparent,
            label = "categoryBorderColor"
                                          )
    val contentColor by animateColorAsState(
            targetValue = if (isSelected) onSurfaceColor
            else onSurfaceColor.copy(alpha = UNSELECTED_CONTENT_ALPHA),
            label = "categoryContentColor"
                                           )

    Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight)
                    .clip(itemShape)
                    .background(containerColor)
                    .border(
                            width = itemBorderWidth,
                            color = borderColor,
                            shape = itemShape
                           )
                    .selectable(
                            selected = isSelected,
                            role = Role.RadioButton,
                            onClick = onClick
                               )
                    .padding(horizontal = itemHorizontalPadding),
            verticalAlignment = Alignment.CenterVertically
       ) {
        Icon(
                painter = painterResource(category.drawableRes),
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(itemIconSize)
            )
        Spacer(modifier = Modifier.width(iconEndPadding))
        Text(
                text = stringResource(category.labelRes),
                color = contentColor,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                modifier = Modifier.weight(1f)
            )
        if (isSelected) {
            Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = onSurfaceColor,
                    modifier = Modifier.size(checkIconSize)
                )
        }
    }
}

private val movieCategoriesList = listOf(
        MovieCategory.PopularMovieCategory,
        MovieCategory.UpcomingMovieCategory,
        MovieCategory.NowPlayingMovieCategory,
        MovieCategory.TopRatedMovieCategory,
        MovieCategory.FavoritesMovieCategory
                                        )

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun CategoryListPopupViewPreview() {
    MMoviesTheme {
        CategoryListPopupView(
                lastSelectedCategory = MovieCategory.PopularMovieCategory,
                onNewCategorySelected = {},
                onDismiss = {},
                state = rememberModalBottomSheetState()
                             )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(locale = "iw", name = "Hebrew (RTL)")
@Composable
private fun CategoryListPopupViewPreviewHebrew() {
    MMoviesTheme {
        CategoryListPopupView(
                lastSelectedCategory = MovieCategory.TopRatedMovieCategory,
                onNewCategorySelected = {},
                onDismiss = {},
                state = rememberModalBottomSheetState()
                             )
    }
}

// Russian category names are the longest of the three languages
// ("Лучшие по рейтингу" against "Top Rated"), so this is where a row would
// wrap or clip first.
@OptIn(ExperimentalMaterial3Api::class)
@Preview(locale = "ru", name = "Russian (long text)")
@Composable
private fun CategoryListPopupViewPreviewRussian() {
    MMoviesTheme {
        CategoryListPopupView(
                lastSelectedCategory = MovieCategory.TopRatedMovieCategory,
                onNewCategorySelected = {},
                onDismiss = {},
                state = rememberModalBottomSheetState()
                             )
    }
}
