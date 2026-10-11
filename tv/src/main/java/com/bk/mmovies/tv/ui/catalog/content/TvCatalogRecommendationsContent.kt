package com.bk.mmovies.tv.ui.catalog.content

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bk.mmovies.core.R as CoreR
import com.bk.mmovies.domain.model.CatalogItem
import com.bk.mmovies.tv.R
import com.bk.mmovies.tv.ui.catalog.TvCatalogScreenState
import com.bk.mmovies.tv.ui.catalog.content.rows.TvRowShimmer
import com.bk.mmovies.tv.ui.component.ApiKeySetupSteps
import com.bk.mmovies.tv.ui.component.TvDetailsActionButton
import com.bk.mmovies.tv.ui.component.TvRow

@Composable
fun TvRecommendationsContent(
        screenState: TvCatalogScreenState.Recommendations,
        onApiKeySaved: (apiKey: String) -> Unit,
        onItemPressed: (item: CatalogItem) -> Unit,
        onRetryClicked: () -> Unit,
        navRailFocusRequester: FocusRequester?
                                  ) {
    when {
        screenState.loginRequired        -> RecommendationsMessage(stringResource(R.string.ai_recommendations_login_required))
        screenState.keySetupRequired     -> RecommendationsKeySetup(onApiKeySaved)
        screenState.isLoading            -> TvRowShimmer(modifier = Modifier.padding(top = 24.dp))
        screenState.items.isNotEmpty()   -> TvRecommendationsRow(
                items = screenState.items,
                onItemPressed = onItemPressed,
                navRailFocusRequester = navRailFocusRequester
                                                                )
        screenState.errorMessage != null -> RecommendationsMessage(
                message = screenState.errorMessage,
                onRetryClicked = onRetryClicked,
                navRailFocusRequester = navRailFocusRequester
                                                                  )
        else                             -> RecommendationsMessage(stringResource(R.string.ai_recommendations_no_signal_message))
    }
}

@Composable
private fun RecommendationsMessage(
        message: String,
        onRetryClicked: (() -> Unit)? = null,
        navRailFocusRequester: FocusRequester? = null
                                  ) {
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
                text = message,
                modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth(0.6f),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyLarge
            )
        if (onRetryClicked != null) {
            Spacer(modifier = Modifier.height(24.dp))
            TvDetailsActionButton(
                    text = stringResource(R.string.retry_action),
                    onClick = onRetryClicked,
                    modifier = Modifier.onPreviewKeyEvent { keyEvent ->
                        if (navRailFocusRequester != null &&
                            keyEvent.type == KeyEventType.KeyDown &&
                            keyEvent.key == Key.DirectionLeft
                        ) {
                            runCatching { navRailFocusRequester.requestFocus() }
                            true
                        } else {
                            false
                        }
                    }
                                 )
        }
    }
}

@Composable
private fun RecommendationsKeySetup(onApiKeySaved: (apiKey: String) -> Unit) {
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

@Composable
fun TvRecommendationsRow(
        items: List<CatalogItem>,
        onItemPressed: (item: CatalogItem) -> Unit,
        navRailFocusRequester: FocusRequester? = null
                        ) {
    Column {
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

        Spacer(modifier = Modifier.height(16.dp))

        TvRow(
                rowId = "recommendations",
                list = items,
                onItemPressed = onItemPressed,
                navRailFocusRequester = navRailFocusRequester
             )
    }
}

@Composable
fun AiIcon() {
    Icon(
            painter = painterResource(CoreR.drawable.ic_ai),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(28.dp)
        )
}
