package com.bk.mmovies.tv.ui.catalog.content.recommendations

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bk.mmovies.core.R as CoreR
import com.bk.mmovies.domain.model.CatalogItem
import com.bk.mmovies.tv.R
import com.bk.mmovies.tv.theme.tvHeaderTextStyle
import com.bk.mmovies.tv.ui.catalog.TvCatalogScreenState
import com.bk.mmovies.tv.ui.catalog.content.carousel.TvRecommendationsRow
import com.bk.mmovies.tv.ui.catalog.content.carousel.TvShimmerRow
import com.bk.mmovies.tv.ui.catalog.content.keysetup.RecommendationsKeySetup
import com.bk.mmovies.tv.ui.component.TvDetailsActionButton

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
        screenState.isLoading            -> TvShimmerRow()
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
            modifier = Modifier.fillMaxWidth()
          ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AiIcon()
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                    text = stringResource(R.string.ai_recommendations_title),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    style = tvHeaderTextStyle
                )
        }
        Text(
                text = message,
                modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth(0.6f),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                style = tvHeaderTextStyle
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
fun AiIcon() {
    Icon(
            painter = painterResource(CoreR.drawable.ic_ai),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(20.dp)
        )
}
