package com.example.gifapp.domain.usecases.local

import android.graphics.Bitmap
import com.example.gifapp.domain.entities.GifPicture
import com.example.gifapp.domain.reposities.LocalGifRepository
import javax.inject.Inject

class SaveGifLocalUseCase @Inject constructor(
    private val localGifRepository: LocalGifRepository,
) {
    suspend operator fun invoke(gifPicture: GifPicture, bitmap: Bitmap): Result<Boolean> {
        return localGifRepository.save(gifPicture, bitmap)
    }
}