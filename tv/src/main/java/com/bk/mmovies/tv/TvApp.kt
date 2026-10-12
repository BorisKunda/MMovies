package com.bk.mmovies.tv

import android.app.Application
import android.content.Context
import coil3.ImageLoader
import coil3.SingletonImageLoader
import com.bk.mmovies.app.CoilImageLoaderFactory
import com.bk.mmovies.data.source.remote.POSTER_PATH_SIZE_SEGMENT_LIST_ITEM
import dagger.hilt.android.HiltAndroidApp

private const val TV_POSTER_PATH_SIZE_SEGMENT_LIST_ITEM = "w185"

@HiltAndroidApp
class TvApp : Application(), SingletonImageLoader.Factory {

    override fun onCreate() {
        super.onCreate()
        POSTER_PATH_SIZE_SEGMENT_LIST_ITEM = TV_POSTER_PATH_SIZE_SEGMENT_LIST_ITEM
    }

    override fun newImageLoader(context: Context): ImageLoader {
        return CoilImageLoaderFactory.create(context)
    }
}
