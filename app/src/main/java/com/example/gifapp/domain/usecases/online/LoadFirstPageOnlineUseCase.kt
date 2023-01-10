package com.example.gifapp.domain.usecases.online

import com.example.gifapp.domain.reposities.OnlineGifRepository
import com.example.gifapp.domain.usecases.base.LoadFirstPageUseCase
import javax.inject.Inject

class LoadFirstPageOnlineUseCase @Inject constructor(
    gifRepository: OnlineGifRepository
) : LoadFirstPageUseCase(gifRepository)