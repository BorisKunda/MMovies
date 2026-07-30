package com.bk.mmovies.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.bk.mmovies.R
import com.bk.mmovies.ui.theme.MMoviesTheme
import com.bk.mmovies.ui.theme.primaryButtonContentColor
import com.bk.mmovies.ui.theme.primaryButtonCornerSize
import com.bk.mmovies.ui.theme.primaryButtonDisabledContainerColor
import com.bk.mmovies.ui.theme.primaryButtonDisabledContentColor
import com.bk.mmovies.ui.theme.primaryButtonHeight
import com.bk.mmovies.ui.theme.primaryButtonIconPadding
import com.bk.mmovies.ui.theme.primaryButtonIconSize
import com.bk.mmovies.util.logDebug


@Composable
fun PrimaryButton(
        @DrawableRes imageResId: Int?,
        label: String,
        onClick: () -> Unit,
        isEnabled: Boolean
                 ) {
    Button(
            onClick = onClick,
            modifier = Modifier
                    .fillMaxWidth()
                    .height(
                            primaryButtonHeight
                           ),
            colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(primaryButtonContentColor),
                    disabledContainerColor = primaryButtonDisabledContainerColor,
                    disabledContentColor = primaryButtonDisabledContentColor
                                                ),
            enabled = isEnabled,
            shape = RoundedCornerShape(primaryButtonCornerSize),
          ) {
        imageResId?.let {
            Icon(
                    painter = painterResource(
                            imageResId
                                             ),
                    contentDescription = null,
                    modifier = Modifier
                            .size(primaryButtonIconSize)
                            .padding(horizontal = primaryButtonIconPadding)
                )
        }
        Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
    }
}

@Composable
@Preview(showBackground = true)
fun PrimaryButtonPreview() {
    MMoviesTheme {
        Box(
                modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center,
                content = {
                    PrimaryButton(
                            imageResId = R.drawable.ic_settings,
                            label = "Open network settings",
                            {
                                logDebug(
                                        "PrimaryButtonPreview",
                                        "Open network settings clicked"
                                        )
                            },
                            true
                                 )
                }
           )
    }
}