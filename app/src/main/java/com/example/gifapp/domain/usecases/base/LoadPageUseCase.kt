package com.example.gifapp.domain.usecases.base

import com.example.gifapp.domain.entities.Page


interface LoadPageUseCase {
    suspend operator fun invoke(pageNumber: Int, query: String = ""): Result<Page>
}