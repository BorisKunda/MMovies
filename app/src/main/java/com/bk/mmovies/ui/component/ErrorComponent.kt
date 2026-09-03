package com.bk.mmovies.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bk.mmovies.R
import com.bk.mmovies.util.logDebug


private val errorViewPaddingStart = 28.dp
private val errorViewPaddingTop = 64.dp
private val errorViewEmbeddedPaddingTop = 24.dp
private val errorViewPaddingEnd = 28.dp
private val errorViewPaddingBottom = 32.dp
private val errorViewVerticalSpacing = 48.dp
private val errorImageHeight = 88.dp
private val errorTitlePaddingTop = 14.dp
private val errorMessagePaddingTop: Dp = 20.dp
private val noInternetTryAgainSpacing = 12.dp

@Composable fun ErrorView(
        modifier: Modifier,
        @DrawableRes errorImageResId: Int,
        errorTitle: String,
        errorMessage: String,
        actions: (@Composable ColumnScope.() -> Unit)? = null,
        useSpaceBetween: Boolean = false,
        tag: String = "ErrorView",
        // Full-screen hosts (the splash flow) own their insets. When this view
        // is embedded below an app bar or a category chip the NavHost has
        // already applied them, and re-applying pushes the content far down.
        isEmbedded: Boolean = false
                         ) {
    LazyColumn(
            modifier = modifier
                    .background(MaterialTheme.colorScheme.background)
                    .then(
                            if (isEmbedded) Modifier
                            else Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
                         )
                    .imePadding(),
            contentPadding = PaddingValues(
                    start = errorViewPaddingStart,
                    top = if (isEmbedded) errorViewEmbeddedPaddingTop else errorViewPaddingTop,
                    end = errorViewPaddingEnd,
                    bottom = errorViewPaddingBottom
                                          ),
            verticalArrangement = if (useSpaceBetween) Arrangement.SpaceBetween else Arrangement.spacedBy(
                    errorViewVerticalSpacing
                                                                                                         ),
            horizontalAlignment = Alignment.CenterHorizontally
              ) {
        item {
            ErrorTitleMessage(
                    errorImageResId,
                    errorTitle,
                    errorMessage
                             )
        }
        if (actions != null) {
            item {
                ErrorActions(actions)
            }
        }
    }

    LaunchedEffect(Unit) {
        logDebug(
                tag,
                "LAUNCHED"
                )
    }

    DisposableEffect(Unit) {
        onDispose {
            logDebug(
                    tag,
                    "DISPOSED"
                    )
        }
    }
}


@Composable
fun NoInternetView(
        // Placed before onSettingsButtonClicked (and defaulted) so every
        // existing trailing-lambda call site - which binds to the *last*
        // parameter - keeps targeting the settings action unchanged.
        onTryAgainClicked: (() -> Unit)? = null,
        onSettingsButtonClicked: () -> Unit
                  ) {
    ErrorView(
            modifier = Modifier.fillMaxSize(),
            errorImageResId = R.drawable.ic_no_internet,
            errorTitle = stringResource(
                    R.string.error_no_internet_connection_title
                                       ),
            errorMessage = stringResource(
                    R.string.error_no_internet_connection_message
                                         ),
            actions = {
                PrimaryButton(
                        imageResId = R.drawable.ic_settings,
                        label = stringResource(
                                R.string.error_no_internet_action
                                              ),
                        {
                            onSettingsButtonClicked()
                        },
                        true
                             )
                // Automatic retry depends on the OS reporting a fresh
                // connectivity transition (see InternetMonitor/retryIfOffline),
                // which never fires if the request failed while the network
                // never actually went down (e.g. a stale connection right
                // after coming back from the background). This manual escape
                // hatch covers that gap.
                if (onTryAgainClicked != null) {
                    Spacer(modifier = Modifier.height(noInternetTryAgainSpacing))
                    PrimaryButton(
                            imageResId = null,
                            label = stringResource(R.string.try_again),
                            onTryAgainClicked,
                            true
                                 )
                }
            },
            useSpaceBetween = true,
            tag = "NoInternetView"
             )
}

@Composable
private fun ErrorActions(actions: @Composable (ColumnScope.() -> Unit)) {
    Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = actions
          )
}

@Composable
private fun ErrorTitleMessage(
        @DrawableRes errorImageResId: Int,
        errorTitle: String,
        errorMessage: String
                             ) {
    Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
          ) {
        Image(
                painter = painterResource(errorImageResId),
                contentDescription = null,
                modifier = Modifier
                        .height(errorImageHeight)
                        .aspectRatio(1f),
                contentScale = ContentScale.Fit
             )
        Text(
                modifier = Modifier.padding(top = errorTitlePaddingTop),
                text = errorTitle,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

        Text(
                modifier = Modifier.padding(top = errorMessagePaddingTop),
                text = errorMessage,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
    }
}

@Composable
fun GenericErrorScreen(errorMessageText: String, onTryAgainClicked: () -> Unit) {
    ErrorView(
            modifier = Modifier.fillMaxSize(),
            errorImageResId = R.drawable.ic_error,
            errorTitle = stringResource(R.string.generic_error_title),
            errorMessage = errorMessageText,
            {
                PrimaryButton(
                        imageResId = null,
                        label = stringResource(R.string.try_again),
                        {
                            onTryAgainClicked()
                        },
                        true
                             )
            },
            useSpaceBetween = true,
            tag = "GenericErrorView",
            // Both callers render this inside an already-inset NavHost screen.
            isEmbedded = true
             )
}

