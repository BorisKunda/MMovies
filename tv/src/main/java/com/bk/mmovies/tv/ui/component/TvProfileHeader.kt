package com.bk.mmovies.tv.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import com.bk.mmovies.core.R as CoreR
import com.bk.mmovies.tv.R

private val avatarSize = 40.dp
private const val AVATAR_BACKGROUND_ALPHA = 0.10f
private const val AVATAR_BORDER_ALPHA = 0.2f
private val avatarBorderWidth = 1.dp

/**
 * Shown above the catalog content, mirroring :app's UserProfileBar - the
 * signed-in user's name/avatar, or a "Guest" placeholder when no session is
 * logged in (matching :app's UserAvatarButton, which :tv can't reuse
 * directly since it only depends on :core). D-pad focusable/clickable so
 * pressing it opens the login screen, since a guest otherwise had no way to
 * reach it again after dismissing the initial Auth screen.
 */
@Composable
fun TvProfileHeader(
        name: String,
        imageUrl: String,
        isGuest: Boolean,
        modifier: Modifier = Modifier,
        focusRequester: FocusRequester? = null,
        // False makes the header invisible to D-pad focus search (and to
        // requestFocus) - the catalog only lets it be reached from the Movies
        // rail item, so everywhere else Up/Right never land on it.
        canBeFocused: Boolean = true,
        onFocusChanged: (isFocused: Boolean) -> Unit = {},
        // D-pad Down/Left on the header hands focus back to where it came from
        // (the nav rail's Movies item) instead of wherever spatial focus
        // search would land (a nearby content card for Down).
        onNavigateBack: (() -> Unit)? = null,
        onClick: () -> Unit = {}
                    ) {
    val displayName = if (isGuest) stringResource(R.string.guest_label) else name
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    // The clickable/focusable area is on this inner Row rather than the
    // outer fillMaxWidth() one, so the hit target and focus highlight stay
    // tight around the avatar+name instead of spanning the whole header
    // width that Arrangement.End uses to right-align them.
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Row(
                modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(if (isFocused) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.14f) else Color.Transparent)
                        // Both must precede the focus target (clickable below).
                        .focusProperties { canFocus = canBeFocused }
                        .onFocusChanged { onFocusChanged(it.isFocused) }
                        .onPreviewKeyEvent { keyEvent ->
                            if (onNavigateBack != null &&
                                keyEvent.type == KeyEventType.KeyDown &&
                                (keyEvent.key == Key.DirectionDown || keyEvent.key == Key.DirectionLeft)
                            ) {
                                onNavigateBack()
                                true
                            } else {
                                false
                            }
                        }
                        .let { if (focusRequester != null) it.focusRequester(focusRequester) else it }
                        .clickable(interactionSource = interactionSource, role = Role.Button, onClick = onClick)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
           ) {
            Avatar(name = name, imageUrl = imageUrl, isGuest = isGuest)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                    text = displayName,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
        }
    }
}

@Composable
private fun Avatar(name: String, imageUrl: String, isGuest: Boolean) {
    when {
        isGuest             -> Image(
                painter = painterResource(CoreR.drawable.ic_guest),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                        .size(avatarSize)
                        .clip(CircleShape)
                                    )
        imageUrl.isBlank()  -> InitialsAvatar(name = name)
        else                -> SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                        .size(avatarSize)
                        .clip(CircleShape),
                loading = { InitialsAvatar(name = name) },
                error = { InitialsAvatar(name = name) }
                                                     )
    }
}

@Composable
private fun InitialsAvatar(name: String) {
    val initial = name.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    Box(
            modifier = Modifier
                    .size(avatarSize)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = AVATAR_BACKGROUND_ALPHA))
                    .border(
                            width = avatarBorderWidth,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = AVATAR_BORDER_ALPHA),
                            shape = CircleShape
                           ),
            contentAlignment = Alignment.Center
       ) {
        Text(
                text = initial,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp, lineHeight = 18.sp),
                fontWeight = FontWeight.Bold
            )
    }
}
