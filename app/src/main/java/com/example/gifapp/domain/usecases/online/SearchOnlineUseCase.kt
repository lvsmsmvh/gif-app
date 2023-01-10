package com.example.gifapp.domain.usecases.online

import com.example.gifapp.domain.reposities.OnlineGifRepository
import com.example.gifapp.domain.usecases.base.SearchUseCase
import javax.inject.Inject

class SearchOnlineUseCase @Inject constructor(
    gifRepository: OnlineGifRepository
) : SearchUseCase(gifRepository)