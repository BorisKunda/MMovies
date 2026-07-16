package com.bk.mmovies.ui.screen.mock

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel


@Composable
@Preview
fun MockScreen() {
    val context = LocalContext.current
    val viewModel: MockViewModel = hiltViewModel()
    Column(Modifier.fillMaxSize(), content = {
        Text("Mock Screen")
    })
}

