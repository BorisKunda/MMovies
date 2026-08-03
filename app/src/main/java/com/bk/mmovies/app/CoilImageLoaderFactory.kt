package com.bk.mmovies.app

import android.content.Context
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import okio.Path.Companion.toOkioPath

object CoilImageLoaderFactory {

    fun create(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
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