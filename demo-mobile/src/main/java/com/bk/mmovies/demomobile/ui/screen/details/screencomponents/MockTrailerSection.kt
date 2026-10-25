package com.bk.mmovies.demomobile.ui.screen.details.screencomponents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.bk.mmovies.core.R as CoreR
import com.bk.mmovies.demomobile.R
import com.bk.mmovies.demomobile.ui.component.rememberMockImageModel

private val playerHeight = 220.dp
private val thumbnailPlayIconSize = 56.dp
private const val THUMBNAIL_SCRIM_ALPHA = 0.35f

@Composable
fun MockTrailerSection(
        posterUrl: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier
                      ) {
    Box(
            modifier = modifier
                    .fillMaxWidth()
                    .height(playerHeight)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
       ) {
        AsyncImage(
                model = rememberMockImageModel(posterUrl),
                placeholder = painterResource(CoreR.drawable.placeholder_wide),
                error = painterResource(CoreR.drawable.placeholder_wide),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
                  )
        Box(
                modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = THUMBNAIL_SCRIM_ALPHA))
           )
        Icon(
                imageVector = Icons.Filled.PlayCircle,
                contentDescription = stringResource(R.string.details_watch_trailer_action),
                tint = Color.White,
                modifier = Modifier.size(thumbnailPlayIconSize)
            )
    }
}
