package com.example.gifapp.data.repository

import com.example.gifapp.data.gif_online_api.GifApi
import com.example.gifapp.data.gif_online_api.entities.GifResponse
import com.example.gifapp.data.mappers.toGifPictures
import com.example.gifapp.domain.entities.GifPicturesPage
import com.example.gifapp.domain.exceptions.GifLoadPageOnlineException
import com.example.gifapp.domain.reposities.OnlineGifRepository
import javax.inject.Inject

class OnlineGifImpl @Inject constructor(
    private val gifApi: GifApi,
) : OnlineGifRepository {

    companion object {
        private const val API_KEY = "joROvSscQyfLRIpTj5JFKSk6FIJ2obMa"
        private const val ITEMS_ON_PAGE = 25
        private const val STATUS_OK = 200
    }

    override suspend fun getGifs(pageIndex: Int): Result<GifPicturesPage> {
        val gifResponse = gifApi.getGifs(
            api_key = API_KEY,
            limit = ITEMS_ON_PAGE,
            offset = pageIndex * ITEMS_ON_PAGE,
        )

        return parseResult(gifResponse, pageIndex)
    }

    override suspend fun search(pageIndex: Int, name: String): Result<GifPicturesPage> {
        val gifResponse = gifApi.searchGifs(
            api_key = API_KEY,
            search_string = name,
            limit = ITEMS_ON_PAGE,
            offset = pageIndex * ITEMS_ON_PAGE,
        )

        return parseResult(gifResponse, pageIndex)
    }

    private fun parseResult(gifResponse: GifResponse, pageIndex: Int): Result<GifPicturesPage> {
        return when (gifResponse.meta.status) {
            STATUS_OK -> {
                Result.success(GifPicturesPage(pageIndex, gifResponse.data.toGifPictures()))
            }
            else -> {
                Result.failure(GifLoadPageOnlineException(gifResponse.meta.msg))
            }
        }
    }
}