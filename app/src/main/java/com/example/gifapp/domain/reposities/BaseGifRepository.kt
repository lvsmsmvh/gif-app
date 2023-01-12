package com.example.gifapp.domain.reposities

import com.example.gifapp.domain.entities.Page

interface BaseGifRepository {
    suspend fun loadPage(pageIndex: Int, query: String): Result<Page>
}