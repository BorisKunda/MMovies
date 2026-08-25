package com.bk.mmovies.domain.model

data class CatalogItem( val id: Int,
                        val title: String,
                        val imageUrl: String,
                        val releaseDate: String,
                        val rating: Int = 0,
                        val isFavorite: Boolean = false)

