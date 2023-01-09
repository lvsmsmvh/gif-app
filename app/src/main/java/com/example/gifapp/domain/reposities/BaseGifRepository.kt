package com.example.gifapp.domain.reposities

import com.example.gifapp.domain.entities.GifPicturesPage

interface BaseGifRepository {
    suspend fun getGifs(pageIndex: Int): Result<GifPicturesPage>
    suspend fun search(pageIndex: Int, name: String): Result<GifPicturesPage>
}