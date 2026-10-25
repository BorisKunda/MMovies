package com.bk.mmovies.demomobile.ui.screen.details

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bk.mmovies.demomobile.R
import com.bk.mmovies.demomobile.ui.screen.details.screencomponents.MockItemDetailsScreenContent
import com.bk.mmovies.ui.component.PoweredByTmdbFooter

@Composable
fun MockItemDetailsScreen(
        onBack: () -> Unit,
        viewModel: MockItemDetailsViewModel = viewModel()
                         ) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    LaunchedEffect(configuration) {
        viewModel.refreshDetails()
    }

    LaunchedEffect(Unit) {
        viewModel.messageEvent.collect { messageRes ->
            Toast.makeText(context, messageRes, Toast.LENGTH_SHORT).show()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        DetailsTopBar(onBack = onBack)

        Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
           ) {
            state.movieDetails?.let { movieDetails ->
                MockItemDetailsScreenContent(
                        movieDetails = movieDetails,
                        personBiographies = state.personBiographies,
                        onTrailerClicked = viewModel::onTrailerClicked,
                        onFavoriteClicked = viewModel::onFavoriteClicked,
                        modifier = Modifier.fillMaxSize()
                                            )
            }
        }

        PoweredByTmdbFooter(modifier = Modifier.alpha(0f), onClick = {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailsTopBar(onBack: () -> Unit) {
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
