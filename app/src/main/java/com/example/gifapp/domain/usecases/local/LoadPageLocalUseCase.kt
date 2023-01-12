package com.example.gifapp.domain.usecases.local

import com.example.gifapp.domain.entities.Page
import com.example.gifapp.domain.reposities.LocalGifRepository
import com.example.gifapp.domain.usecases.base.LoadPageUseCase
import javax.inject.Inject

class LoadPageLocalUseCase @Inject constructor(
    private val localGifRepository: LocalGifRepository,
) : LoadPageUseCase {
    override suspend fun invoke(pageNumber: Int, query: String): Result<Page> {
        return localGifRepository.loadPage(pageNumber, query)
    }
}