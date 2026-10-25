package com.bk.mmovies.demomobile.ui.screen.details.screencomponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import com.bk.mmovies.core.R as CoreR
import com.bk.mmovies.demomobile.R
import com.bk.mmovies.demomobile.ui.component.rememberMockImageModel
import com.bk.mmovies.domain.model.CastMemberModel

private val dialogWidthFraction = 0.92f
private val dialogHeightFraction = 0.88f
private val dialogCornerShape = 20.dp
private val photoHeight = 280.dp
private val contentPadding = 20.dp
private val nameToCharacterSpacing = 8.dp
private val characterToBodySpacing = 16.dp
private val bioTitleLetterSpacing = 1.sp
private val bioTitleTopSpacing = 4.dp

private const val SCRIM_ALPHA = 0.7f
private const val SECONDARY_TEXT_ALPHA = 0.85f

@Composable
fun MockActorDetailsDialog(
        castMember: CastMemberModel,
        biography: String,
        onDismiss: () -> Unit
                          ) {
    Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false)
          ) {
        Surface(
                modifier = Modifier
                        .fillMaxWidth(dialogWidthFraction)
                        .fillMaxHeight(dialogHeightFraction),
                shape = RoundedCornerShape(dialogCornerShape),
                color = MaterialTheme.colorScheme.surface
               ) {
            Column(
                    modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                  ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    AsyncImage(
                            model = rememberMockImageModel(castMember.profileUrl),
                            placeholder = painterResource(CoreR.drawable.person_placeholder),
                            error = painterResource(CoreR.drawable.person_placeholder),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                    .fillMaxWidth()
                                    .height(photoHeight)
                                    .clip(
                                            RoundedCornerShape(
                                                    topStart = dialogCornerShape,
                                                    topEnd = dialogCornerShape
                                                               )
                                         )
                              )
                    IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.align(Alignment.TopEnd)
                              ) {
                        Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(R.string.action_close),
                                tint = Color.White,
                                modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(Color.Black.copy(alpha = SCRIM_ALPHA))
                            )
                    }
                }
                Column(modifier = Modifier.padding(contentPadding)) {
                    Text(
                            text = castMember.name,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    if (castMember.character.isNotBlank()) {
                        Text(
                                text = castMember.character,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = SECONDARY_TEXT_ALPHA),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(top = nameToCharacterSpacing)
                            )
                    }
                    Text(
                            text = stringResource(R.string.details_biography_label),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = SECONDARY_TEXT_ALPHA),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = bioTitleLetterSpacing,
                            modifier = Modifier.padding(top = characterToBodySpacing)
                        )
                    Text(
                            text = biography,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = SECONDARY_TEXT_ALPHA),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = bioTitleTopSpacing)
                        )
                }
            }
        }
    }
}
