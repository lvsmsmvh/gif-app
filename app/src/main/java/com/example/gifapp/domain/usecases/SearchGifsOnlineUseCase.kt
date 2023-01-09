package com.example.gifapp.domain.usecases

import com.example.gifapp.domain.entities.GifPicturesPage
import com.example.gifapp.domain.reposities.OnlineGifRepository
import javax.inject.Inject

class SearchGifsOnlineUseCase @Inject constructor(
    private val onlineGifRepository: OnlineGifRepository,
) {
    suspend operator fun invoke(pageIndex: Int, name: String): Result<GifPicturesPage> {
        return onlineGifRepository.search(pageIndex, name)
    }
}