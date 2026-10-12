package com.bk.mmovies.tv.ui.catalog.content.component.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.bk.mmovies.tv.R

@Composable
fun TvProfileDialog(
        isGuest: Boolean,
        name: String,
        onDismissRequest: () -> Unit,
        onLoginClicked: () -> Unit,
        onLogoutClicked: () -> Unit
                            ) {
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
                modifier = Modifier
                        .width(420.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(32.dp)
              ) {
            Text(
                    text = if (isGuest) stringResource(R.string.guest_label) else name,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            Text(
                    text = stringResource(
                            if (isGuest) R.string.profile_dialog_guest_message else R.string.profile_dialog_logout_message
                                         ),
                    modifier = Modifier.padding(top = 12.dp, bottom = 24.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyLarge
                )
            Button(
                    onClick = if (isGuest) onLoginClicked else onLogoutClicked,
                    modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                  ) {
                Text(
                        text = stringResource(if (isGuest) R.string.login_action else R.string.logout_action),
                        fontWeight = FontWeight.Bold
                    )
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                    onClick = onDismissRequest,
                    modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                          ) {
                Text(text = stringResource(R.string.cancel_action))
            }
        }
    }
}
