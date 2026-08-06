package com.bk.mmovies.ui.component

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.ui.theme.MMoviesTheme
import com.bk.mmovies.util.logDebug

private const val TAG = "CategoryListPopupView"

private val sheetHorizontalPadding = 20.dp
private val headerBottomPadding = 20.dp
private val sheetBottomPadding = 28.dp
private val itemSpacing = 10.dp
private val itemHeight = 56.dp
private val itemHorizontalPadding = 16.dp
private val iconEndPadding = 14.dp
private val itemCornerShape = 14.dp
private val itemBorderWidth = 1.5.dp
private val itemIconSize = 22.dp
private val checkIconSize = 20.dp

private const val SELECTED_CONTAINER_ALPHA = 0.14f
private const val UNSELECTED_CONTAINER_ALPHA = 0.06f
private const val UNSELECTED_CONTENT_ALPHA = 0.75f

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
                text = "Categories",
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

    LaunchedEffect(Unit) {
        logDebug(TAG, "LAUNCHED")
    }
    DisposableEffect(Unit) {
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
    val accentColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    val containerColor by animateColorAsState(
        targetValue = if (isSelected) accentColor.copy(alpha = SELECTED_CONTAINER_ALPHA)
        else onSurfaceColor.copy(alpha = UNSELECTED_CONTAINER_ALPHA),
        label = "categoryContainerColor"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) accentColor else Color.Transparent,
        label = "categoryBorderColor"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) accentColor
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
            text = category.label,
            color = contentColor,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = accentColor,
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
    MMoviesTheme{
        CategoryListPopupView(
                lastSelectedCategory = MovieCategory.PopularMovieCategory,
                onNewCategorySelected = {},
                onDismiss = {},
                state = rememberModalBottomSheetState())
    }
}
