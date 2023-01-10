package com.example.gifapp.domain.reposities

import com.example.gifapp.domain.entities.LoadFirstPageResult
import com.example.gifapp.domain.entities.Page

interface BaseGifRepository {

    suspend fun loadFirstPage(): Result<LoadFirstPageResult>
    suspend fun loadPage(pageIndex: Int): Result<Page>
    suspend fun search(name: String): Result<LoadFirstPageResult>
    suspend fun searchPage(pageIndex: Int, name: String): Result<Page>
}