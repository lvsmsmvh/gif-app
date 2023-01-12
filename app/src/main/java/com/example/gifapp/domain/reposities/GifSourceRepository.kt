package com.example.gifapp.domain.reposities

import com.example.gifapp.domain.entities.GifPicture

interface GifSourceRepository {
    suspend fun downloadAndGetLocalUrl(gifPicture: GifPicture): Result<String>
}