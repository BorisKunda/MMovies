package com.bk.mmovies.data.source.remote.interceptor

import com.bk.mmovies.data.source.local.preferences.ApiKeyStorage
import com.bk.mmovies.data.source.remote.QUERY_PARAM_API_KEY
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiKeyInterceptor @Inject constructor(
        private val apiKeyStorage: ApiKeyStorage
                                           ) : Interceptor {

    override fun intercept(
            chain: Interceptor.Chain
                          ): Response {
        val originalRequest: Request = chain.request()

        val storedApiKey = apiKeyStorage.getApiKey() ?: return chain.proceed(originalRequest)

        val originalRequestUrl: HttpUrl = originalRequest.url

        val urlWithApiKey: HttpUrl = originalRequestUrl.newBuilder()
                .setQueryParameter(
                        QUERY_PARAM_API_KEY,
                        storedApiKey
                                  )
                .build()


        val requestWithApiKey: Request = originalRequest
                .newBuilder()
                .url(urlWithApiKey)
                .build()

        return chain.proceed(requestWithApiKey)
    }
}