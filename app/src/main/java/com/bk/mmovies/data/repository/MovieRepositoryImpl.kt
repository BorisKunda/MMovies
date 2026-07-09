package com.bk.mmovies.data.repository

import com.bk.mmovies.data.source.local.DbManager
import com.bk.mmovies.data.source.local.db.MovieDb
import com.bk.mmovies.data.source.remote.NetworkManager
import com.bk.mmovies.data.source.remote.api.TmdbApi
import com.bk.mmovies.domain.repository.MovieRepository
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val api: TmdbApi,
    private val db: MovieDb,
    private val networkManager: NetworkManager,
    private val dbManager: DbManager
) : MovieRepository {
}