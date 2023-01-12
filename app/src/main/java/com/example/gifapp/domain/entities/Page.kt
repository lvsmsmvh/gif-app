package com.example.gifapp.domain.entities

data class Page(
    val pagesAmount: Int,
    val pageNumber: Int,
    val query: String,
    val gifPictures: List<GifPicture>,
)