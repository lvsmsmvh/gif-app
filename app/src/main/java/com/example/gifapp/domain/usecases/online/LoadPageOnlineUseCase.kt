package com.example.gifapp.domain.usecases.online

import com.example.gifapp.domain.reposities.OnlineGifRepository
import com.example.gifapp.domain.usecases.base.LoadPageUseCase
import javax.inject.Inject

class LoadPageOnlineUseCase @Inject constructor(
    gifRepository: OnlineGifRepository
) : LoadPageUseCase(gifRepository)