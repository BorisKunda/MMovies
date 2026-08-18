package com.bk.mmovies.ui.screen.movies.screencomponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.bk.mmovies.R
import com.bk.mmovies.ui.theme.logoutIconSize
import com.bk.mmovies.ui.theme.profileAvatarSize

// Mirrors CategorySelector's chip in MoviesScreen (corner shape, background/
// border alpha, content padding) so the two top-bar controls read as one UI.
private val userSelectorChipCornerShape = 18.dp
private val userSelectorChipBorderWidth = 1.dp
private val userSelectorChipContentHorizontalPadding = 12.dp
private val userSelectorChipContentVerticalPadding = 6.dp
private val userSelectorNameStartPadding = 6.dp
private val userSelectorIconStartPadding = 6.dp

private const val USER_SELECTOR_CHIP_CONTAINER_ALPHA = 0.06f
private const val USER_SELECTOR_CHIP_BORDER_ALPHA = 0.18f

@Composable
fun UserSelector(
        name: String,
        imageUrl: String,
        isGuest: Boolean,
        onLogout: () -> Unit,
        modifier: Modifier = Modifier
                 ) {
    val chipShape = RoundedCornerShape(userSelectorChipCornerShape)
    val displayName = if (isGuest) stringResource(R.string.guest_label) else name
    val actionLabel = stringResource(if (isGuest) R.string.log_in_action else R.string.log_out)

    Row(
            modifier = modifier
                    // Keeps the chip visually compact while giving it the same
                    // 48dp touch target as CategorySelector, so both chips end
                    // up exactly the same height.
                    .minimumInteractiveComponentSize()
                    .clip(chipShape)
                    .background(
                            MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = USER_SELECTOR_CHIP_CONTAINER_ALPHA
                                                                    )
                               )
                    .border(
                            width = userSelectorChipBorderWidth,
                            color = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = USER_SELECTOR_CHIP_BORDER_ALPHA
                                                                            ),
                            shape = chipShape
                           )
                    .clickable(
                            role = Role.Button,
                            onClickLabel = actionLabel
                              ) { onLogout() }
                    .padding(
                            horizontal = userSelectorChipContentHorizontalPadding,
                            vertical = userSelectorChipContentVerticalPadding
                            )
                    // Merged into one node so TalkBack reads the whole chip as
                    // a single control rather than avatar / name / icon.
                    .semantics(mergeDescendants = true) {
                        contentDescription = "$displayName. $actionLabel"
                    },
            verticalAlignment = Alignment.CenterVertically
       ) {
        if (isGuest) {
            Image(
                    painter = painterResource(R.drawable.ic_guest),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                            .size(profileAvatarSize)
                            .clip(CircleShape)
                 )
        } else if (imageUrl.isBlank()) {
            InitialsAvatar(name = name)
        } else {
            SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .crossfade(true)
                            .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                            .size(profileAvatarSize)
                            .clip(CircleShape),
                    loading = { InitialsAvatar(name = name) },
                    // A path from the API doesn't guarantee the image still
                    // exists (TMDB avatar removed, network blip, etc).
                    error = { InitialsAvatar(name = name) }
                                 )
        }

        Text(
                text = displayName,
                modifier = Modifier.padding(start = userSelectorNameStartPadding),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )

        Icon(
                painter = painterResource(
                        if (isGuest) R.drawable.ic_login else R.drawable.ic_logout
                                          ),
                // The chip's merged content description already announces this.
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                        .padding(start = userSelectorIconStartPadding)
                        .size(logoutIconSize)
                        // ic_login and ic_logout's arrows both point the same
                        // way (only the door-frame side differs), which is too
                        // subtle at this size. Mirror login so the two states
                        // are distinguishable at a glance.
                        .scale(scaleX = if (isGuest) -1f else 1f, scaleY = 1f)
            )
    }
}

// Matches CategorySelector's chip background/border in MoviesScreen so the
// avatar reads as part of the same UI.
private const val INITIALS_AVATAR_BACKGROUND_ALPHA = 0.06f
private const val INITIALS_AVATAR_BORDER_ALPHA = 0.18f
private val initialsAvatarBorderWidth = 1.dp

@Composable
private fun InitialsAvatar(name: String, modifier: Modifier = Modifier) {
    val initial = name.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    Box(
            modifier = modifier
                    .size(profileAvatarSize)
                    .clip(CircleShape)
                    .background(
                            MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = INITIALS_AVATAR_BACKGROUND_ALPHA
                                                                    )
                               )
                    .border(
                            width = initialsAvatarBorderWidth,
                            color = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = INITIALS_AVATAR_BORDER_ALPHA
                                                                            ),
                            shape = CircleShape
                           ),
            contentAlignment = Alignment.Center
       ) {
        Text(
                text = initial,
                color = Color.White,
                // lineHeight must match fontSize too — titleMedium's default
                // 24sp line height otherwise makes Compose center on a box far
                // taller than the glyph, throwing off the visual center.
                style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 10.sp,
                        lineHeight = 10.sp
                                                                  ),
                fontWeight = FontWeight.Bold
            )
    }
}
