package com.example.gifapp.domain.usecases.local

import com.example.gifapp.domain.entities.GifPicture
import com.example.gifapp.domain.reposities.LocalGifRepository
import com.example.gifapp.domain.reposities.RemovedGifsRepository
import com.example.gifapp.utils.MediaSaverUtil
import javax.inject.Inject

class RemoveGifLocalUseCase @Inject constructor(
    private val localGifRepository: LocalGifRepository,
    private val removedGifsStore: RemovedGifsRepository,
) {
    operator fun invoke(gif: GifPicture) {
        localGifRepository.getLocalUrl(gif)?.let { MediaSaverUtil.removeGif(it) }
        localGifRepository.remove(gif.id)
        removedGifsStore.markRemoved(gif.id)
    }
}