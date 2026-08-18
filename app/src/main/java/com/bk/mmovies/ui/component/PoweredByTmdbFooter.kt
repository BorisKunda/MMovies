package com.bk.mmovies.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bk.mmovies.R

private val poweredByTmdbLabelSpacing = 6.dp
private val poweredByTmdbLogoHeight = 16.dp
private val poweredByTmdbDisclaimerPaddingTop = 8.dp
private val poweredByTmdbDisclaimerPaddingHorizontal = 24.dp

@Composable
fun PoweredByTmdbFooter(modifier: Modifier = Modifier) {
    Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
        Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
           ) {
            Text(
                    text = stringResource(R.string.powered_by_tmdb),
                    modifier = Modifier.padding(end = poweredByTmdbLabelSpacing),
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall
                )
            Image(
                    painter = painterResource(R.drawable.ic_tmdb_logo),
                    contentDescription = null,
                    modifier = Modifier.height(poweredByTmdbLogoHeight)
                 )
        }
        Text(
                text = stringResource(R.string.tmdb_disclaimer),
                modifier = Modifier
                        .padding(
                                top = poweredByTmdbDisclaimerPaddingTop,
                                start = poweredByTmdbDisclaimerPaddingHorizontal,
                                end = poweredByTmdbDisclaimerPaddingHorizontal
                                ),
                color = Color.Gray,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.labelSmall
            )
    }
}
