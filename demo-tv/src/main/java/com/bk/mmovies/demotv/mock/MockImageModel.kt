package com.bk.mmovies.demotv.mock

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri

private const val RESOURCE_URI_SCHEME = "android.resource"

@Composable
fun rememberMockImageModel(imageUrl: String?): Any? {
    val context = LocalContext.current
    return remember(imageUrl) {
        if (imageUrl == null) return@remember null
        val uri = imageUrl.toUri()
        if (uri.scheme == RESOURCE_URI_SCHEME) {
            context.resources.getIdentifier(uri.lastPathSegment, "drawable", context.packageName)
        } else {
            imageUrl
        }
    }
}
