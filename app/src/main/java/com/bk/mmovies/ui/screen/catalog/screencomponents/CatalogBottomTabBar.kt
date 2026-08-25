package com.bk.mmovies.ui.screen.catalog.screencomponents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.bk.mmovies.R

// NavigationBar enforces an 80dp minimum height internally, so shrinking our
// own padding just left Compose's auto-centering fill the rest of that fixed
// height — the visible gap barely moved. Surface has no such minimum, so the
// bar's height (and therefore the icon+label block's visible margin) is
// driven entirely by this padding.
private val tabVerticalPadding = 6.dp
private val tabIconLabelSpacing = 4.dp
private val tabDividerVerticalPadding = 10.dp
private const val TAB_DIVIDER_ALPHA = 0.18f

@Composable
fun CatalogBottomTabBar(
        selectedTab: CatalogBottomTab,
        onTabSelected: (CatalogBottomTab) -> Unit
                    ) {
    Surface(
            color = MaterialTheme.colorScheme.surfaceContainer,
            tonalElevation = NavigationBarDefaults.Elevation
           ) {
        // VerticalDivider defaults to fillMaxHeight(); without this the Row
        // (and the Surface around it) had no bounded height to fill against
        // and stretched to consume the rest of the screen.
        Row(modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .selectableGroup()) {
            CatalogBottomTab.entries.forEachIndexed { index, tab ->
                if (index != 0) {
                    VerticalDivider(
                            modifier = Modifier.padding(vertical = tabDividerVerticalPadding),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = TAB_DIVIDER_ALPHA)
                                    )
                }
                val isSelected = selectedTab == tab
                val contentColor = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }

                Column(
                        modifier = Modifier
                                .weight(1f)
                                .selectable(
                                        selected = isSelected,
                                        role = Role.Tab,
                                        onClick = { onTabSelected(tab) }
                                           )
                                .padding(vertical = tabVerticalPadding),
                        horizontalAlignment = Alignment.CenterHorizontally
                      ) {
                    Icon(
                            imageVector = tab.icon,
                            contentDescription = null,
                            tint = contentColor
                        )
                    Spacer(modifier = Modifier.height(tabIconLabelSpacing))
                    Text(
                            text = stringResource(tab.labelResId),
                            color = contentColor,
                            style = MaterialTheme.typography.labelMedium
                        )
                }
            }
        }
    }
}

enum class CatalogBottomTab(
        val labelResId: Int,
        val icon: ImageVector
                           ) {
    Movies(R.string.tab_movies, Icons.Filled.Movie),
    TvSeries(R.string.tab_tv_series, Icons.Filled.Tv)
}
