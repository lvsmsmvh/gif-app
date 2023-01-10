package com.example.gifapp.domain.usecases.base

import com.example.gifapp.domain.reposities.BaseGifRepository

open class SearchUseCase(private val gifRepository: BaseGifRepository) {
    suspend operator fun invoke(name: String) = gifRepository.search(name)
}