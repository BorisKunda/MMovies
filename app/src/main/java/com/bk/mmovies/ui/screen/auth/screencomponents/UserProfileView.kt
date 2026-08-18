package com.bk.mmovies.ui.screen.auth.screencomponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.bk.mmovies.R
import com.bk.mmovies.ui.theme.MMoviesTheme
import com.bk.mmovies.ui.theme.logoutIconSize
import com.bk.mmovies.ui.theme.profileAvatarSize

private val profileNamePaddingTop = 5.dp
private val profileNamePaddingBottom = 5.dp

@Composable
fun UserProfileView(
        name: String,
        imageUrl: String,
        isGuest: Boolean,
        onLogout: () -> Unit,
        modifier: Modifier = Modifier
                   ) {
    Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
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
        } else {
            AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .crossfade(true)
                            .build(),
                    placeholder = painterResource(R.drawable.ic_guest),
                    error = painterResource(R.drawable.ic_guest),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                            .size(profileAvatarSize)
                            .clip(CircleShape)
                      )
        }

        Text(
                text = if (isGuest) stringResource(R.string.guest_label) else name,
                modifier = Modifier,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 10.sp),
                fontWeight = FontWeight.SemiBold
            )

        Row(
                modifier = Modifier.clickable(onClick = onLogout),
                verticalAlignment = Alignment.CenterVertically
           ) {
            Text(
                    text = stringResource(if (isGuest) R.string.log_in_action else R.string.log_out),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelSmall
                )
            Icon(
                    painter = painterResource(
                            if (isGuest) R.drawable.ic_login else R.drawable.ic_logout
                                              ),
                    // The label to the left already announces this to TalkBack.
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(logoutIconSize)
                )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UserProfileViewPreview() {
    MMoviesTheme {
        Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
            Box(
                    modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    contentAlignment = Alignment.Center
               ) {
                UserProfileView(
                        name = "Boris Kunda",
                        imageUrl = "",
                        isGuest = false,
                        onLogout = {}
                                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UserProfileViewGuestPreview() {
    MMoviesTheme {
        Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
            Box(
                    modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    contentAlignment = Alignment.Center
               ) {
                UserProfileView(
                        name = "Guest",
                        imageUrl = "",
                        isGuest = true,
                        onLogout = {}
                                )
            }
        }
    }
}

