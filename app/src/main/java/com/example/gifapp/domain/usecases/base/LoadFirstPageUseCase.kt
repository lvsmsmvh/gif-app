package com.example.gifapp.domain.usecases.base

import com.example.gifapp.domain.reposities.BaseGifRepository

open class LoadFirstPageUseCase(private val gifRepository: BaseGifRepository) {
    suspend operator fun invoke() = gifRepository.loadFirstPage()
}