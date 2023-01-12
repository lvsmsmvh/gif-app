package com.example.gifapp.domain.reposities

import com.example.gifapp.domain.entities.GifPicture

interface LocalGifRepository : BaseGifRepository {
    fun remove(gifPictures: List<GifPicture>): Result<Boolean>
    fun remove(gifPicture: GifPicture): Result<Boolean>
    fun saveLocalUrl(gifPicture: GifPicture, url: String)
    fun getLocalUrl(gifPicture: GifPicture): String?
}