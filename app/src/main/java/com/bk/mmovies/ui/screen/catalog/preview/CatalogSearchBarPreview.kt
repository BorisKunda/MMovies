package com.bk.mmovies.ui.screen.catalog.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bk.mmovies.ui.screen.catalog.screencomponents.CatalogSearchBar
import com.bk.mmovies.ui.theme.MMoviesTheme
import com.bk.mmovies.ui.theme.PhoneSizePreviews

@PhoneSizePreviews
@Composable
private fun CatalogSearchBarPreview() {
    MMoviesTheme {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                CatalogSearchBar(onClick = {})
            }
        }
    }
}
