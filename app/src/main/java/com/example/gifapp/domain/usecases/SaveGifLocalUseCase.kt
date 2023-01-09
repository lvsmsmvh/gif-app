package com.example.gifapp.domain.usecases

import com.example.gifapp.domain.entities.GifPicture
import com.example.gifapp.domain.reposities.LocalGifRepository
import javax.inject.Inject

class SaveGifLocalUseCase @Inject constructor(
    private val localGifRepository: LocalGifRepository,
) {
    suspend operator fun invoke(gifs: List<GifPicture>): Result<Boolean> {
        return localGifRepository.save(gifs)
    }
}