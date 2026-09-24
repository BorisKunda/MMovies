package com.bk.mmovies.tv.ui.component

import android.content.ActivityNotFoundException
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bk.mmovies.tv.R

// Same soft red as the phone app's error_soft_red.
private val offlineIconTint = Color(0xFFFFB4AB)

/**
 * The TV counterpart of the phone app's NoInternetView: shown in place of a
 * catalog tab's content when every request behind it failed for lack of a
 * connection. Unlike the phone there is no InternetMonitor here to retry by
 * itself, so [onRetryClicked] is the way back, and [navRailFocusRequester]
 * lets D-pad Left leave for the nav rail (spatial focus search would pick a
 * random rail item instead - see TvRow).
 */
@Composable
fun TvOfflineView(
        onRetryClicked: () -> Unit,
        modifier: Modifier = Modifier,
        navRailFocusRequester: FocusRequester? = null
                 ) {
    val context = LocalContext.current

    Column(
            modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
        Icon(
                painter = painterResource(R.drawable.ic_tv_no_internet),
                contentDescription = null,
                tint = offlineIconTint,
                modifier = Modifier.size(96.dp)
            )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
                text = stringResource(R.string.offline_title),
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
        Text(
                text = stringResource(R.string.offline_message),
                modifier = Modifier
                        .padding(top = 12.dp, bottom = 32.dp)
                        .fillMaxWidth(0.6f),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            TvDetailsActionButton(
                    text = stringResource(R.string.try_again),
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
            TvDetailsActionButton(
                    text = stringResource(R.string.open_network_settings_action),
                    onClick = { openNetworkSettings(context) }
                                 )
        }
    }
}

// Wi-Fi settings first (what a TV owner needs when the box lost its network),
// falling back to the general settings screen on a device without it.
private fun openNetworkSettings(context: android.content.Context) {
    val flags = Intent.FLAG_ACTIVITY_NEW_TASK
    try {
        context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS).addFlags(flags))
    } catch (e: ActivityNotFoundException) {
        runCatching { context.startActivity(Intent(Settings.ACTION_SETTINGS).addFlags(flags)) }
    }
}
