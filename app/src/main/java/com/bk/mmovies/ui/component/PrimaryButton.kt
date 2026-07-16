package com.bk.mmovies.ui.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun PrimaryButton(
    imageVector: ImageVector?,
    label: String,
    onClick: () -> Unit
) {
    ElevatedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor =
                Color.White,
            contentColor = Color.Black
        )
    ) {
        imageVector?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(
                modifier = Modifier.width(8.dp)
            )
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
@Preview
fun PrimaryButtonPreview() {
    PrimaryButton(
        imageVector = Icons.Outlined.Settings,
        label = "Open network settings"
    ) { }
}