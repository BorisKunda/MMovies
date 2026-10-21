package com.bk.mmovies.tv.ui.catalog.content.keysetup

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bk.mmovies.tv.R
import com.bk.mmovies.tv.theme.tvContentTextStyle
import com.bk.mmovies.tv.theme.tvHeaderTextStyle
import com.bk.mmovies.tv.ui.catalog.TvCatalogDestination
import com.bk.mmovies.tv.ui.component.ApiKeySetupSteps

@Composable
fun NewsKeySetup(onApiKeySaved: (apiKey: String) -> Unit) {
    val context = LocalContext.current
    val savedMessage = stringResource(R.string.news_api_key_saved)

    Column(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
          ) {
        Text(
                text = stringResource(TvCatalogDestination.News.labelResId),
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                style = tvHeaderTextStyle
            )
        Text(
                text = stringResource(R.string.news_api_key_setup_message),
                modifier = Modifier
                        .padding(
                                top = 12.dp,
                                bottom = 24.dp
                                )
                        .fillMaxWidth(0.6f),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                style = tvContentTextStyle
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
