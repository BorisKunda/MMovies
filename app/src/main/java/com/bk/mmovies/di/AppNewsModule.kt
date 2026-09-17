package com.bk.mmovies.di

import android.content.Context
import com.bk.mmovies.data.mapper.NewsMapper
import com.bk.mmovies.data.repositoryimpl.NewsRepositoryImpl
import com.bk.mmovies.data.source.remote.NetworkManager
import com.bk.mmovies.data.source.remote.api.NewsApi
import com.bk.mmovies.domain.repository.NewsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// :app's own NewsRepository binding - NewsApi-backed (NewsAPI), unlike :tv's
// TvNewsModule which binds the same interface to a Guardian-backed
// implementation. Kept out of :core's (shared) NewsNetworkModule specifically
// so :app and :tv can each bind NewsRepository differently without Hilt
// seeing two unqualified bindings in the same graph.
@Module
@InstallIn(SingletonComponent::class)
object AppNewsModule {

    @Provides
    @Singleton
    fun provideNewsRepository(
            api: NewsApi,
            networkManager: NetworkManager,
            newsMapper: NewsMapper,
            @ApplicationContext context: Context
                             ): NewsRepository =
            NewsRepositoryImpl(api, networkManager, newsMapper, context)
}
