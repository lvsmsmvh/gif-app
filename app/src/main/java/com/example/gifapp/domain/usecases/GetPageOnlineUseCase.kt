package com.example.gifapp.domain.usecases

import com.example.gifapp.domain.entities.GifPicturesPage
import com.example.gifapp.domain.reposities.OnlineGifRepository
import javax.inject.Inject

class GetPageOnlineUseCase @Inject constructor(
    private val onlineGifRepository: OnlineGifRepository,
) {
    suspend operator fun invoke(pageIndex: Int): Result<GifPicturesPage> {
        return onlineGifRepository.getGifs(pageIndex)
    }
}