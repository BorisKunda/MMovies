package com.bk.mmovies.model

data class MovieModel(
    val id: Int,
    val title: String,
    val desc: String,
    val imageUrl: String,
    val releaseDate: String,
    val isFavorite: Boolean = false
)//todo add backdrop