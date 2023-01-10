package com.example.gifapp.domain.usecases.local

import com.example.gifapp.domain.reposities.LocalGifRepository
import com.example.gifapp.domain.usecases.base.LoadPageUseCase
import javax.inject.Inject

class LoadPageLocalUseCase @Inject constructor(
    gifRepository: LocalGifRepository
) : LoadPageUseCase(gifRepository)