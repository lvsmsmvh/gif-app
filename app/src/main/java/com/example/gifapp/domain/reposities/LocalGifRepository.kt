package com.example.gifapp.domain.reposities

import com.example.gifapp.domain.entities.GifPicture

interface LocalGifRepository : BaseGifRepository {
    fun remove(gifPictures: List<GifPicture>): Result<Boolean>
    fun save(gifPictures: List<GifPicture>): Result<Boolean>
}