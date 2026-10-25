package com.bk.mmovies.demomobile.ui.screen.search.screencomponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private val contentHorizontalPadding = 28.dp
private val contentBottomPadding = 32.dp
private val messagePaddingTop = 12.dp

private const val MESSAGE_ALPHA = 0.7f

@Composable
fun EmptyStateView(
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
        Text(
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
