package com.bk.mmovies.demotv.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.bk.mmovies.core.R as CoreR
import com.bk.mmovies.demotv.R
import com.bk.mmovies.demotv.mock.rememberMockImageModel

private const val THUMBNAIL_ASPECT_RATIO = 16f / 9f
private val thumbnailPlayIconSize = 64.dp
private val focusBorderWidth = 3.dp
private const val THUMBNAIL_SCRIM_ALPHA = 0.35f

@Composable
fun TvTrailerSection(
        posterUrl: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier
                    ) {
    var isFocused by remember { mutableStateOf(false) }

    Box(
            modifier = modifier
                    .fillMaxWidth()
                    .aspectRatio(THUMBNAIL_ASPECT_RATIO)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .then(
                            if (isFocused) {
                                Modifier.border(width = focusBorderWidth, color = Color.White)
                            } else {
                                Modifier
                            }
                         )
                    .onFocusChanged { focusState -> isFocused = focusState.isFocused }
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
                modifier = Modifier.height(thumbnailPlayIconSize)
            )
    }
}
