package com.example.gifapp.domain.usecases.base

import com.example.gifapp.domain.reposities.BaseGifRepository

open class SearchPageUseCase(private val gifRepository: BaseGifRepository) {
    suspend operator fun invoke(pageIndex: Int, name: String) =
        gifRepository.searchPage(pageIndex, name)
}