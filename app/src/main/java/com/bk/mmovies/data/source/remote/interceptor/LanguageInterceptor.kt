package com.bk.mmovies.data.source.remote.interceptor

import com.bk.mmovies.data.source.remote.QUERY_PARAM_LANGUAGE
import com.bk.mmovies.locale.LocaleMonitor
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageInterceptor @Inject constructor(
        private val localeMonitor: LocaleMonitor
                                              ) : Interceptor {

    override fun intercept(
            chain: Interceptor.Chain
                          ): Response {
        val originalRequest: Request = chain.request()

        val originalRequestUrl: HttpUrl = originalRequest.url

        val urlWithLanguage: HttpUrl = originalRequestUrl.newBuilder()
                .setQueryParameter(
                        QUERY_PARAM_LANGUAGE,
                        localeMonitor.currentLanguage.value.tmdbLanguageTag
                                  )
                .build()

        val requestWithLanguage: Request = originalRequest
                .newBuilder()
                .url(urlWithLanguage)
                .build()

        return chain.proceed(requestWithLanguage)
    }
}
