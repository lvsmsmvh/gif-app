package com.example.gifapp.domain.usecases.base

import com.example.gifapp.domain.reposities.BaseGifRepository

open class LoadPageUseCase(private val gifRepository: BaseGifRepository) {
    suspend operator fun invoke(pageNumber: Int) = gifRepository.loadPage(pageNumber)
}