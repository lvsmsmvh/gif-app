package com.example.gifapp.domain.usecases

import com.example.gifapp.domain.entities.GifPicturesPage
import com.example.gifapp.domain.reposities.LocalGifRepository
import javax.inject.Inject

class GetPageLocalUseCase @Inject constructor(
    private val localGifRepository: LocalGifRepository,
) {
    suspend operator fun invoke(pageIndex: Int): Result<GifPicturesPage> {
        return localGifRepository.getGifs(pageIndex)
    }
}