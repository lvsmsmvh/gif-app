package com.example.gifapp.domain.usecases.local

import com.example.gifapp.domain.reposities.LocalGifRepository
import com.example.gifapp.domain.usecases.base.SearchPageUseCase
import javax.inject.Inject

class SearchPageLocalUseCase @Inject constructor(
    gifRepository: LocalGifRepository
) : SearchPageUseCase(gifRepository)