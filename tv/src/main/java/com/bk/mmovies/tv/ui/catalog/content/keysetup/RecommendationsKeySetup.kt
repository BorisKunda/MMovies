package com.bk.mmovies.tv.ui.catalog.content.keysetup

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bk.mmovies.tv.R
import com.bk.mmovies.tv.ui.catalog.content.recommendations.AiIcon
import com.bk.mmovies.tv.ui.component.ApiKeySetupSteps

@Composable
fun RecommendationsKeySetup(onApiKeySaved: (apiKey: String) -> Unit) {
    val context = LocalContext.current
    val savedMessage = stringResource(R.string.gemini_api_key_saved)

    Column(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
          ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AiIcon()
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                    text = stringResource(R.string.ai_recommendations_title),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall
                )
        }
        Text(
                text = stringResource(R.string.ai_recommendations_empty_message),
                modifier = Modifier
                        .padding(
                                top = 12.dp,
                                bottom = 24.dp
                                )
                        .fillMaxWidth(0.6f),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyLarge
            )

        ApiKeySetupSteps(
                onSaveClicked = { apiKey ->
                    onApiKeySaved(apiKey)
                    Toast.makeText(
                            context,
                            savedMessage,
                            Toast.LENGTH_SHORT
                                  )
                            .show()
                },
                modifier = Modifier.fillMaxWidth(0.6f)
                        )
    }
}
