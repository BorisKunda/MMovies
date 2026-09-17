package com.bk.mmovies.tv

import android.app.Application
import android.content.Context
import coil3.ImageLoader
import coil3.SingletonImageLoader
import com.bk.mmovies.app.CoilImageLoaderFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TvApp : Application(), SingletonImageLoader.Factory {

    override fun newImageLoader(context: Context): ImageLoader {
        return CoilImageLoaderFactory.create(context)
    }
}
