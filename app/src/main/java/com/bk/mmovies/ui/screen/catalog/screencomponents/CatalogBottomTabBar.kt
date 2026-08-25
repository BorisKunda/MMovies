package com.bk.mmovies.ui.screen.catalog.screencomponents

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.bk.mmovies.R

@Composable
fun CatalogBottomTabBar(
        selectedTab: CatalogBottomTab,
        onTabSelected: (CatalogBottomTab) -> Unit
                    ) {
    NavigationBar {
        CatalogBottomTab.entries.forEach { tab ->
            NavigationBarItem(
                    selected = selectedTab == tab,
                    onClick = { onTabSelected(tab) },
                    icon = { Icon(imageVector = tab.icon, contentDescription = null) },
                    label = { Text(stringResource(tab.labelResId)) }
                              )
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
