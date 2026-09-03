package com.bk.mmovies.app

import android.content.Context
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.request.crossfade
import okio.Path.Companion.toOkioPath

object CoilImageLoaderFactory {

    fun create(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            // Every call site used to build its own ImageRequest just to set
            // this, allocating a fresh request object on every recomposition
            // for no benefit since the value never varied. Set once here so
            // callers can pass a plain URL as the model instead.
            .crossfade(true)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(
                        context.cacheDir
                            .resolve("image_cache")
                            .toOkioPath()
                    )
                    .maxSizePercent(0.02)
                    .build()
            }
            .build()
    }
}