package com.example.gifapp.domain.entities

data class Page(
    val pageNumber: Int,
    val gifPictures: List<GifPicture>,
)