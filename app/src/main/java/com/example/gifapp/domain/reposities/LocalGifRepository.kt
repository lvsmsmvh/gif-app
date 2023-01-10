package com.example.gifapp.domain.reposities

import android.graphics.Bitmap
import com.example.gifapp.domain.entities.GifPicture

interface LocalGifRepository : BaseGifRepository {
    fun remove(gifPictures: List<GifPicture>): Result<Boolean>
    fun save(gifPicture: GifPicture, bitmap: Bitmap): Result<Boolean>
}