package com.bk.mmovies.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bk.mmovies.data.source.local.dbmodel.entity.MovieEntity

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies")
    suspend fun getAllMovies(): List<MovieEntity>

    @Query("SELECT * FROM movies WHERE categoryId = :categoryId")
    suspend fun getMoviesByCategory(categoryId: Int): List<MovieEntity>

    @Query("SELECT * FROM movies WHERE movieId = :movieId")
    suspend fun getMovieById(movieId: Int): MovieEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMovies(movies: List<MovieEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMovie(movie: MovieEntity)

    @Query("UPDATE movies SET isFavorite=:isFavorite WHERE movieId=:movieId")
    suspend fun updateMovieIsFavorite(
        movieId: Int,
        isFavorite: Boolean
    )

    @Query("DELETE FROM movies WHERE movieId = :movieId")
    suspend fun deleteMovieById(movieId: Int)

    @Query("DELETE FROM movies")
    suspend fun clearDb()
}
