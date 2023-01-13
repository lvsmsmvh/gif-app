package com.example.gifapp.domain.reposities

import com.example.gifapp.domain.entities.GifPicture

interface LocalGifRepository : BaseGifRepository {
    fun remove(gifPictureId: String)
    fun saveLocalUrl(gifPicture: GifPicture, url: String)
    fun getLocalUrl(gifPicture: GifPicture): String?
}