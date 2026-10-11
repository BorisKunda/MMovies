package com.bk.mmovies.tv.ui.catalog.content

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bk.mmovies.tv.ui.catalog.TvCatalogDestination
import com.bk.mmovies.tv.ui.catalog.TvNavIcon
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

private val navRailExpandedWidth = 260.dp
private val navRailCollapsedWidth = 96.dp
private val navItemCornerShape = 12.dp
private val navIconSize = 24.dp
private const val navRailFocusDebounce = 250L

@Composable
internal fun CatalogNavRail(
        selectedDestination: TvCatalogDestination,
        onDestinationSelected: (TvCatalogDestination) -> Unit,
        navItemFocusRequesters: Map<TvCatalogDestination, FocusRequester> = emptyMap(),
        profileFocusRequester: FocusRequester? = null,
        onMoviesItemFocusChanged: (isFocused: Boolean) -> Unit = {},
        aboutContentFocusRequester: FocusRequester? = null,
        isFocusedState: State<Boolean> = remember { mutableStateOf(false) },
        onFocusChanged: (Boolean) -> Unit = {}
                           ) {
    val isFocused by isFocusedState
    val railWidth = if (isFocused) navRailExpandedWidth else navRailCollapsedWidth

    Column(
            modifier = Modifier
                    .width(railWidth)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(
                            horizontal = 20.dp,
                            vertical = 32.dp
                            )
                    .focusGroup()
                    .onFocusChanged { onFocusChanged(it.hasFocus) },
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {

        TvCatalogDestination.entries.forEachIndexed { index, destination ->
            val focusRequester = navItemFocusRequesters[destination]
            var itemModifier =
                    if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier
            if (index == 0) {
                itemModifier =
                        itemModifier.onFocusChanged { onMoviesItemFocusChanged(it.isFocused) }
            }
            if (index == 0 && profileFocusRequester != null) {
                itemModifier = itemModifier.onPreviewKeyEvent { keyEvent ->
                    if (keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.DirectionUp) {
                        runCatching { profileFocusRequester.requestFocus() }
                        true
                    } else {
                        false
                    }
                }
            }

            if (destination == TvCatalogDestination.About && aboutContentFocusRequester != null) {
                itemModifier = itemModifier.onPreviewKeyEvent { keyEvent ->
                    if (keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.DirectionRight) {
                        runCatching { aboutContentFocusRequester.requestFocus() }
                        true
                    } else {
                        false
                    }
                }
            }
            CatalogNavRailItem(
                    label = stringResource(destination.labelResId),
                    icon = destination.icon,
                    isSelected = destination == selectedDestination && !isFocused,
                    isExpanded = isFocused,
                    onClick = { onDestinationSelected(destination) },
                    modifier = itemModifier
                              )
        }
    }
}

@Composable
private fun CatalogNavRailItem(
        label: String,
        icon: TvNavIcon,
        isSelected: Boolean,
        isExpanded: Boolean,
        onClick: () -> Unit,
        modifier: Modifier = Modifier
                              ) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    LaunchedEffect(isFocused) {
        if (isFocused) {
            delay(navRailFocusDebounce.milliseconds)
            onClick()
        }
    }
    val backgroundColor = when {
        isFocused  -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.18f)
        isSelected -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f)
        else       -> Color.Transparent
    }
    val contentColor = if (isSelected || isFocused) {
        MaterialTheme.colorScheme.onBackground
    } else {
        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
    }
    Row(
            modifier = modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(navItemCornerShape))
                    .background(backgroundColor)
                    .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = onClick
                              )
                    .padding(
                            horizontal = 16.dp,
                            vertical = 14.dp
                            ),


            horizontalArrangement = if (isExpanded) Arrangement.Start else Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
       ) {
        when (icon) {
            is TvNavIcon.Vector   -> Icon(
                    imageVector = icon.imageVector,
                    contentDescription = if (isExpanded) null else label,
                    tint = contentColor,
                    modifier = Modifier.size(navIconSize)
                                         )
            is TvNavIcon.Drawable -> Icon(
                    painter = painterResource(icon.resId),
                    contentDescription = if (isExpanded) null else label,
                    tint = contentColor,
                    modifier = Modifier.size(navIconSize)
                                         )
        }

        if (isExpanded) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                        text = label,
                        color = contentColor,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1
                    )
            }
        }
    }
}

@Composable
internal fun RailReservedSpaceSpacer(isRailFocused: State<Boolean>) {
    val focused by isRailFocused
    Spacer(modifier = Modifier.width(if (focused) navRailExpandedWidth else navRailCollapsedWidth))
}
