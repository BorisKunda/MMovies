package com.bk.mmovies.tv.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bk.mmovies.core.R
import com.bk.mmovies.tv.R as TvR
import com.bk.mmovies.tv.ui.component.ApiKeySetupSteps
import com.bk.mmovies.ui.theme.MMoviesTheme

private val errorIconSize = 96.dp

// No discovery link out to TMDB here (see ApiKeySetupSteps' doc, and the
// identical change made for News/Gemini) - tmdb_api_key_setup_instructions
// carries the "how to get a key" steps as plain text instead of a button.
@Composable
fun ApiKeySetupTvScreen(onSaveClicked: (apiKey: String) -> Unit = {}) {
    Box(
            modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(54.dp)
       ) {
        Row(
                modifier = Modifier.fillMaxSize()
           ) {
            Column(
                    modifier = Modifier
                            .weight(0.4f)
                            .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                  ) {
                Icon(
                        painter = painterResource(R.drawable.ic_invalid_api_key),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(errorIconSize)
                    )

                Text(
                        text = stringResource(R.string.error_missing_api_key_title),
                        modifier = Modifier.padding(top = 24.dp),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                Text(
                        text = stringResource(TvR.string.tmdb_api_key_setup_instructions),
                        modifier = Modifier.padding(top = 8.dp),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
            }

            Spacer(modifier = Modifier.weight(0.18f))

            ApiKeySetupSteps(
                    onSaveClicked = onSaveClicked,
                    modifier = Modifier
                            .weight(0.42f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                             )
        }
    }
}

@Preview(device = "id:tv_1080p")
@Composable
private fun ApiKeySetupTvScreenPreview() {
    MMoviesTheme {
        Scaffold(
                containerColor = MaterialTheme.colorScheme.background
                ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                ApiKeySetupTvScreen()
            }
        }
    }
}
