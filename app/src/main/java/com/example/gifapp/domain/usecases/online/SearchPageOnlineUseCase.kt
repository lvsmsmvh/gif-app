package com.example.gifapp.domain.usecases.online

import com.example.gifapp.domain.reposities.OnlineGifRepository
import com.example.gifapp.domain.usecases.base.SearchPageUseCase
import javax.inject.Inject

class SearchPageOnlineUseCase @Inject constructor(
    gifRepository: OnlineGifRepository
) : SearchPageUseCase(gifRepository)