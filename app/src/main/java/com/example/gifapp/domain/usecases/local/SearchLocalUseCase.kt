package com.example.gifapp.domain.usecases.local

import com.example.gifapp.domain.reposities.LocalGifRepository
import com.example.gifapp.domain.usecases.base.SearchUseCase
import javax.inject.Inject

class SearchLocalUseCase @Inject constructor(
    gifRepository: LocalGifRepository
) : SearchUseCase(gifRepository)