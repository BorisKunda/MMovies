package com.bk.mmovies.data

import com.bk.mmovies.data.source.local.DbManager
import com.bk.mmovies.data.source.local.MovieDb
import com.bk.mmovies.data.source.remote.NetworkManager
import com.bk.mmovies.data.source.remote.TmdbApi
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val api: TmdbApi,
    private val db: MovieDb,
    private val networkManager: NetworkManager,
    private val dbManager: DbManager
) : MovieRepository {
}