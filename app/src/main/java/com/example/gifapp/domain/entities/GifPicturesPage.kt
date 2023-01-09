package com.example.gifapp.domain.entities

data class GifPicturesPage(
    val pageNumber: Int,
    val gifPictures: List<GifPicture>
)