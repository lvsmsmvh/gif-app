package com.example.gifapp.domain.usecases.other

import com.example.gifapp.domain.entities.GifPicture
import com.example.gifapp.domain.reposities.GifSourceRepository
import javax.inject.Inject

class DownloadAndGetLocalUrlUseCase @Inject constructor(
    private val gifSourceRepository: GifSourceRepository,
) {
    suspend operator fun invoke(gifPicture: GifPicture): Result<String> {
        return gifSourceRepository.downloadAndGetLocalUrl(gifPicture)
    }
}