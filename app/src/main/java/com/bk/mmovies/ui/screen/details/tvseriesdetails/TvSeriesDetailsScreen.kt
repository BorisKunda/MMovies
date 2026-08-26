package com.bk.mmovies.ui.screen.details.tvseriesdetails

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bk.mmovies.R
import com.bk.mmovies.ui.component.GenericErrorScreen
import com.bk.mmovies.ui.component.LoaderView
import com.bk.mmovies.ui.component.PoweredByTmdbFooter
import com.bk.mmovies.ui.screen.details.tvseriesdetails.screencomponents.TvSeriesDetailsScreenContent

private const val TAG = "TvSeriesDetailsScreen"

@Composable
fun TvSeriesDetailsScreen(
        seriesId: Int,
        onBack: () -> Unit,
        onNavigateToSeasonDetails: (seasonNumber: Int) -> Unit = {},
        onNavigateToTerms: () -> Unit = {}
                         ) {
    val context = LocalContext.current
    val tvSeriesDetailsViewModel = hiltViewModel<TvSeriesDetailsViewModel>()
    val state by tvSeriesDetailsViewModel.tvSeriesDetailsScreenState.collectAsStateWithLifecycle()

    LaunchedEffect(seriesId) {
        tvSeriesDetailsViewModel.loadTvSeriesDetails(seriesId)
    }

    LaunchedEffect(Unit) {
        tvSeriesDetailsViewModel.messageEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // The screen previously relied entirely on the system back gesture,
        // which leaves no visible way out for anyone not using gestures.
        TvSeriesDetailsTopBar(onBack = onBack)

        Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
           ) {
            when (val currentState = state) {
                is TvSeriesDetailsScreenState.Loading -> {
                    LoaderView()
                }

                is TvSeriesDetailsScreenState.Content -> {
                    TvSeriesDetailsScreenContent(
                            tvSeriesDetails = currentState.tvSeriesDetails,
                            showFavoriteStar = tvSeriesDetailsViewModel.canToggleFavorite,
                            onFavoriteClicked = { tvSeriesDetailsViewModel.onFavoriteClicked() },
                            onSeasonClicked = onNavigateToSeasonDetails,
                            modifier = Modifier.fillMaxSize()
                                                 )
                }

                is TvSeriesDetailsScreenState.Error -> {
                    GenericErrorScreen(
                            currentState.errorMessage,
                            onTryAgainClicked = { tvSeriesDetailsViewModel.retry() }
                                      )
                }
            }
        }

        PoweredByTmdbFooter(onClick = onNavigateToTerms)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TvSeriesDetailsTopBar(onBack: () -> Unit) {
    TopAppBar(
            // No title: the header below already shows it at full size, and a
            // second copy here would make TalkBack announce it twice more.
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
