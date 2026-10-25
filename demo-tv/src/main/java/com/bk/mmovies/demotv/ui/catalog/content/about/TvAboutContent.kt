package com.bk.mmovies.demotv.ui.catalog.content.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bk.mmovies.demotv.R
import com.bk.mmovies.demotv.theme.tvContentTextStyle
import com.bk.mmovies.demotv.theme.tvHeaderTextStyle

@Composable
fun TvAboutContent() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
                text = stringResource(R.string.nav_about),
                color = MaterialTheme.colorScheme.onBackground,
                style = tvHeaderTextStyle,
                fontWeight = FontWeight.Bold
            )
        Text(
                text = stringResource(R.string.demo_about_description),
                color = MaterialTheme.colorScheme.onBackground,
                style = tvContentTextStyle
            )
    }
}
