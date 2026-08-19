package com.bk.mmovies.ui.screen.movies.screencomponents

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.bk.mmovies.ui.screen.movies.MoviesBottomTab

@Composable
fun MoviesBottomBar(
        selectedTab: MoviesBottomTab,
        onTabSelected: (MoviesBottomTab) -> Unit
                   ) {
    NavigationBar {
        MoviesBottomTab.entries.forEach { tab ->
            NavigationBarItem(
                    selected = selectedTab == tab,
                    onClick = { onTabSelected(tab) },
                    icon = { Icon(imageVector = tab.icon, contentDescription = null) },
                    label = { Text(stringResource(tab.labelResId)) }
                              )
        }
    }
}
