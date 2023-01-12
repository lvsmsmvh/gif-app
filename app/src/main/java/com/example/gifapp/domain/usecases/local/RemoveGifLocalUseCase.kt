package com.example.gifapp.domain.usecases.local

import com.example.gifapp.domain.entities.GifPicture
import com.example.gifapp.domain.reposities.LocalGifRepository
import javax.inject.Inject

class RemoveGifLocalUseCase @Inject constructor(
    private val localGifRepository: LocalGifRepository,
) {
    suspend operator fun invoke(gifs: List<GifPicture>): Result<Boolean> {
        return localGifRepository.remove(gifs)
    }

    suspend operator fun invoke(gif: GifPicture): Result<Boolean> {
        return localGifRepository.remove(gif)
    }
}