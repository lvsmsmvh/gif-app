package com.example.gifapp.domain.usecases.local

import com.example.gifapp.domain.reposities.LocalGifRepository
import com.example.gifapp.domain.usecases.base.LoadFirstPageUseCase
import javax.inject.Inject

class LoadFirstPageLocalUseCase @Inject constructor(
    gifRepository: LocalGifRepository
) : LoadFirstPageUseCase(gifRepository)