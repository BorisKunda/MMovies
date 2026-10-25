package com.bk.mmovies.demomobile.ui.screen.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bk.mmovies.demomobile.R
import com.bk.mmovies.demomobile.ui.screen.details.screencomponents.MockSeasonDetailsScreenContent
import com.bk.mmovies.ui.component.PoweredByTmdbFooter

@Composable
fun MockSeasonDetailsScreen(
        onBack: () -> Unit,
        viewModel: MockSeasonDetailsViewModel = viewModel()
                           ) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current

    LaunchedEffect(configuration) {
        viewModel.refreshDetails()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SeasonDetailsTopBar(onBack = onBack)

        Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
           ) {
            state.season?.let { season ->
                MockSeasonDetailsScreenContent(
                        season = season,
                        personBiographies = state.personBiographies,
                        modifier = Modifier.fillMaxSize()
                                              )
            }
        }

        PoweredByTmdbFooter(modifier = Modifier.alpha(0f), onClick = {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeasonDetailsTopBar(onBack: () -> Unit) {
    TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.details_back),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                                                      )
             )
}
