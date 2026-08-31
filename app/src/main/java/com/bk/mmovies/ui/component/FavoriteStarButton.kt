package com.bk.mmovies.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bk.mmovies.R
import com.bk.mmovies.ui.theme.MMoviesTheme

val favoriteStarDefaultIconSize = 32.dp

// Scrim/touch target grow past the icon by the same margins regardless of
// the caller's icon size, so the circle always reads as a background behind
// the star rather than an outline hugging its edges.
private val scrimSizeMargin = 12.dp
private val touchTargetSizeMargin = 24.dp
private const val SCRIM_ALPHA = 0.35f

// Color.Gray (0xFF888888) darkened by 25% (136 * 0.75 ≈ 102 = 0x66).
private val scrimColor = Color(0xFF666666)

/**
 * Filled/outline favorite toggle meant to sit over a poster's top-right
 * corner. Callers gate its visibility (guest sessions, the Upcoming
 * category) rather than this composable, since that decision depends on
 * context it doesn't have.
 */
@Composable
fun FavoriteStarButton(
        isFavorite: Boolean,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        starIconSize: Dp = favoriteStarDefaultIconSize
                      ) {
    val contentDescription = stringResource(
            if (isFavorite) R.string.favorite_remove_action else R.string.favorite_add_action
                                           )
    val scrimSize = starIconSize + scrimSizeMargin
    val touchTargetSize = starIconSize + touchTargetSizeMargin

    Box(
            modifier = modifier
                    .size(touchTargetSize)
                    .clickable(
                            role = Role.Button,
                            onClickLabel = contentDescription
                              ) { onClick() },
            // TopEnd, not Center: the touch target is padded larger than the
            // visible circle for tap comfort, but the circle itself should
            // sit flush against the poster's corner, not centered within
            // that extra invisible padding.
            contentAlignment = Alignment.TopEnd
       ) {
        Box(
                modifier = Modifier
                        .size(scrimSize)
                        .clip(CircleShape)
                        // Larger than the icon so the scrim reads as a
                        // background circle behind it, not just an outline
                        // hugging the star's edges. Shown for both states so
                        // the star stays legible against bright poster art
                        // even when filled.
                        .background(scrimColor.copy(alpha = SCRIM_ALPHA)),
                contentAlignment = Alignment.Center
           ) {
            Icon(
                    painter = painterResource(
                            if (isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star_unfilled
                                             ),
                    contentDescription = contentDescription,
                    tint = Color.White,
                    modifier = Modifier.size(starIconSize)
                )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoriteStarButtonPreview() {
    MMoviesTheme {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
            Box(
                    modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp)
                            .background(Color.DarkGray)
               ) {
                FavoriteStarButton(isFavorite = true, onClick = {})
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoriteStarButtonUnfilledPreview() {
    MMoviesTheme {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
            Box(
                    modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp)
                            .background(Color.DarkGray)
               ) {
                FavoriteStarButton(isFavorite = false, onClick = {})
            }
        }
    }
}
