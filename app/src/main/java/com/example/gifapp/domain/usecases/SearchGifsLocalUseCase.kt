package com.example.gifapp.domain.usecases

import com.example.gifapp.domain.entities.GifPicturesPage
import com.example.gifapp.domain.reposities.LocalGifRepository
import javax.inject.Inject

class SearchGifsLocalUseCase @Inject constructor(
    private val localGifRepository: LocalGifRepository,
) {
    suspend operator fun invoke(pageIndex: Int, name: String): Result<GifPicturesPage> {
        return localGifRepository.search(pageIndex, name)
    }
}