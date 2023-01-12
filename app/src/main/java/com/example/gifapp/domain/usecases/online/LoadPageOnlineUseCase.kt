package com.example.gifapp.domain.usecases.online

import com.example.gifapp.domain.entities.Page
import com.example.gifapp.domain.reposities.LocalGifRepository
import com.example.gifapp.domain.reposities.OnlineGifRepository
import com.example.gifapp.domain.usecases.base.LoadPageUseCase
import javax.inject.Inject

class LoadPageOnlineUseCase @Inject constructor(
    private val onlineGifRepository: OnlineGifRepository,
) : LoadPageUseCase {
    override suspend fun invoke(pageNumber: Int, query: String): Result<Page> {
        return onlineGifRepository.loadPage(pageNumber, query)
    }
}