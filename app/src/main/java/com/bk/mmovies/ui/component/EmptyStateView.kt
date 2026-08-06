package com.bk.mmovies.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bk.mmovies.R
import com.bk.mmovies.ui.theme.MMoviesTheme

private val contentHorizontalPadding = 28.dp
private val contentBottomPadding = 32.dp
private val imageHeight = 88.dp
private val titlePaddingTop = 14.dp
private val messagePaddingTop = 12.dp

private const val MESSAGE_ALPHA = 0.7f

/**
 * Shown when a load succeeds but there is genuinely nothing to display.
 * Deliberately quieter than [GenericErrorScreen]: nothing has gone wrong, so
 * there is no error illustration and no retry button.
 */
@Composable
fun EmptyStateView(
        @DrawableRes imageResId: Int,
        title: String,
        message: String,
        modifier: Modifier = Modifier
                  ) {
    Column(
            modifier = modifier
                    .fillMaxSize()
                    .padding(
                            start = contentHorizontalPadding,
                            end = contentHorizontalPadding,
                            bottom = contentBottomPadding
                            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
        Image(
                painter = painterResource(imageResId),
                contentDescription = null,
                modifier = Modifier
                        .height(imageHeight)
                        .aspectRatio(1f),
                contentScale = ContentScale.Fit
             )
        Text(
                modifier = Modifier.padding(top = titlePaddingTop),
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        Text(
                modifier = Modifier.padding(top = messagePaddingTop),
                text = message,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = MESSAGE_ALPHA),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyStateViewPreview() {
    MMoviesTheme {
        EmptyStateView(
                imageResId = R.drawable.ic_star_filled_large,
                title = "No favorites yet",
                message = "Movies you mark as a favorite will show up here."
                      )
    }
}
