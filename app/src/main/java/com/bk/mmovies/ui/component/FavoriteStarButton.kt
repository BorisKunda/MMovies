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
import androidx.compose.ui.unit.dp
import com.bk.mmovies.R
import com.bk.mmovies.ui.theme.MMoviesTheme

private val touchTargetSize = 32.dp
private val starIconSize = 16.dp
private const val SCRIM_ALPHA = 0.35f

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
        modifier: Modifier = Modifier
                      ) {
    val contentDescription = stringResource(
            if (isFavorite) R.string.favorite_remove_action else R.string.favorite_add_action
                                           )

    Box(
            modifier = modifier
                    .size(touchTargetSize)
                    .clickable(
                            role = Role.Button,
                            onClickLabel = contentDescription
                              ) { onClick() },
            contentAlignment = Alignment.Center
       ) {
        Box(
                modifier = Modifier
                        .size(starIconSize)
                        .clip(CircleShape)
                        // Only the unfilled star needs the scrim, sized to just
                        // the icon: it's a thin outline that disappears against
                        // a light poster, whereas the filled star is solid and
                        // reads fine on its own.
                        .then(
                                if (isFavorite) {
                                    Modifier
                                } else {
                                    Modifier.background(Color.Gray.copy(alpha = SCRIM_ALPHA))
                                }
                             ),
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
