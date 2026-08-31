package com.bk.mmovies.ui.component

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.bk.mmovies.R
import com.bk.mmovies.data.source.remote.CAST_PROFILE_PATH_SIZE_SEGMENT
import com.bk.mmovies.data.source.remote.CAST_PROFILE_PATH_SIZE_SEGMENT_ZOOM
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
fun ActorDetailsDialog(
        castMember: CastMemberModel,
        onDismiss: () -> Unit
                       ) {
    val actorDetailsViewModel = hiltViewModel<ActorDetailsViewModel>()
    val state by actorDetailsViewModel.personDetailsState.collectAsStateWithLifecycle()

    LaunchedEffect(castMember.id) {
        actorDetailsViewModel.loadPersonDetails(castMember.id)
    }

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
                    // The cast list only ever needed a w185 thumbnail, but that
                    // upscales to a visibly blurry mess at this dialog's size —
                    // swap in the larger TMDB size just for this display.
                    val largeProfileUrl = castMember.profileUrl.replace(
                            CAST_PROFILE_PATH_SIZE_SEGMENT,
                            CAST_PROFILE_PATH_SIZE_SEGMENT_ZOOM
                                                                       )
                    AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                    .data(largeProfileUrl)
                                    .crossfade(true)
                                    .build(),
                            placeholder = painterResource(R.drawable.placeholder),
                            error = painterResource(R.drawable.placeholder),
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

                    when (val currentState = state) {
                        is PersonDetailsUiState.Loading -> {
                            LoadingIndicatorRow()
                        }

                        is PersonDetailsUiState.Content -> {
                            PersonDetailsBody(
                                    birthday = currentState.personDetails.birthday,
                                    placeOfBirth = currentState.personDetails.placeOfBirth,
                                    biography = currentState.personDetails.biography
                                              )
                        }

                        is PersonDetailsUiState.Error -> {
                            Text(
                                    text = currentState.errorMessage,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = SECONDARY_TEXT_ALPHA),
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(top = characterToBodySpacing)
                                )
                            // PrimaryButton owns its own width/height, so the
                            // spacing above it has to come from a wrapper.
                            Box(modifier = Modifier.padding(top = characterToBodySpacing)) {
                                PrimaryButton(
                                        imageResId = null,
                                        label = stringResource(R.string.try_again),
                                        onClick = { actorDetailsViewModel.retry() },
                                        isEnabled = true
                                             )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingIndicatorRow() {
    Box(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = characterToBodySpacing),
            contentAlignment = Alignment.Center
       ) {
        LoaderView()
    }
}

@Composable
private fun PersonDetailsBody(
        birthday: String,
        placeOfBirth: String,
        biography: String
                              ) {
    val bornText = listOfNotNull(
            birthday.takeIf { it.isNotBlank() }?.let { stringResource(R.string.details_born_on, it) },
            placeOfBirth.takeIf { it.isNotBlank() }
                                ).joinToString(" • ")
    if (bornText.isNotBlank()) {
        Text(
                text = bornText,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = SECONDARY_TEXT_ALPHA),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = characterToBodySpacing)
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
            text = biography.ifBlank { stringResource(R.string.details_biography_unavailable) },
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = SECONDARY_TEXT_ALPHA),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = bioTitleTopSpacing)
        )
}
