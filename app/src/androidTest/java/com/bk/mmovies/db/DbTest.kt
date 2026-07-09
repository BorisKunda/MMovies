package com.bk.mmovies.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.bk.mmovies.data.source.local.db.MovieDb
import com.bk.mmovies.data.source.local.dao.MovieDao
import com.bk.mmovies.data.source.local.entity.MovieEntity
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DbTest {
    private lateinit var movieDb: MovieDb
    private lateinit var movieDao: MovieDao

    @Before
    fun setUp() {
        val context: Context =
            ApplicationProvider.getApplicationContext()
        movieDb =
            Room.inMemoryDatabaseBuilder(context, MovieDb::class.java).allowMainThreadQueries()
                .build()
        movieDao = movieDb.movieDao()
    }

    @Test
    fun testInsertMovieGetMovieById() {
        runTest {
            val mockMovie1 = MovieEntity(1, "title", "desc", "imageUrl", "releaseDate", 1, false)

            movieDao.insertMovie(mockMovie1)

            val result = movieDao.getMovieById(1)

            assertEquals(mockMovie1, result)
        }
    }

    @Test
    fun testRemoveMovieById() {
        runTest {
            val mockMovie1 = MovieEntity(1, "title1", "desc", "imageUrl", "releaseDate", 1, false)
            val mockMovie2 = MovieEntity(2, "title2", "desc", "imageUrl", "releaseDate", 1, false)
            val mockMovie3 = MovieEntity(3, "title3", "desc", "imageUrl", "releaseDate", 1, false)
            val mockMoviesList123 = listOf(mockMovie1, mockMovie2, mockMovie3)
            val mockMoviesList12 = listOf(mockMovie1, mockMovie2)
            movieDao.insertMovies(mockMoviesList123)
            movieDao.deleteMovieById(3)
            val moviesFromDb = movieDao.getAllMovies()
            assertEquals(mockMoviesList12, moviesFromDb)
        }
    }
}