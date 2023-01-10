package com.example.gifapp.data.repository

import com.example.gifapp.data.gif_online_api.GifApi
import com.example.gifapp.data.gif_online_api.entities.GifResponse
import com.example.gifapp.data.mappers.toGifPictures
import com.example.gifapp.domain.entities.GifPicture
import com.example.gifapp.domain.entities.LoadFirstPageResult
import com.example.gifapp.domain.entities.Page
import com.example.gifapp.domain.exceptions.LoadException
import com.example.gifapp.domain.exceptions.NothingFoundException
import com.example.gifapp.domain.reposities.OnlineGifRepository
import com.example.gifapp.domain.reposities.RemovedGifsRepository
import retrofit2.Call
import javax.inject.Inject

class OnlineGiphyImpl @Inject constructor(
    private val gifApi: GifApi,
    private val removedGifsStore: RemovedGifsRepository,
) : OnlineGifRepository {

    companion object {
        private const val API_KEY = "joROvSscQyfLRIpTj5JFKSk6FIJ2obMa"
        private const val ITEMS_ON_PAGE = 25
        private const val STATUS_OK = 200
    }

    override suspend fun loadFirstPage(): Result<LoadFirstPageResult> {
        gifApi.getGifs(
            api_key = API_KEY,
            limit = ITEMS_ON_PAGE,
            offset = 0,
        )
            .toResult()
            .onFailure { return Result.failure(it) }
            .onSuccess { return Result.success(it.toLoadFirstPageResult()) }

        throw Exception("Unreachable code")
    }

    override suspend fun loadPage(pageIndex: Int): Result<Page> {
        gifApi.getGifs(
            api_key = API_KEY,
            limit = ITEMS_ON_PAGE,
            offset = (pageIndex - 1) * ITEMS_ON_PAGE,
        )
            .toResult()
            .onFailure { return Result.failure(it) }
            .onSuccess { return Result.success(it.toPage(pageIndex)) }

        throw Exception("Unreachable code")
    }

    override suspend fun search(name: String): Result<LoadFirstPageResult> {
        gifApi.searchGifs(
            api_key = API_KEY,
            search_string = name,
            limit = ITEMS_ON_PAGE,
            offset = 0,
        )
            .toResult()
            .onFailure { return Result.failure(it) }
            .onSuccess { return Result.success(it.toLoadFirstPageResult()) }

        throw Exception("Unreachable code")
    }

    override suspend fun searchPage(pageIndex: Int, name: String): Result<Page> {
        gifApi.searchGifs(
            api_key = API_KEY,
            search_string = name,
            limit = ITEMS_ON_PAGE,
            offset = (pageIndex - 1) * ITEMS_ON_PAGE,
        )
            .toResult()
            .onFailure { return Result.failure(it) }
            .onSuccess { return Result.success(it.toPage(pageIndex)) }

        throw Exception("Unreachable code")
    }

    private fun Call<GifResponse>.toResult(): Result<GifResponse> {
        val response = execute()
        if (!response.isSuccessful) {
            return Result.failure(LoadException(response.errorBody()?.string().toString()))
        }

        val gifResponse = response.body() ?: return Result.failure(LoadException())

        if (gifResponse.meta.status != STATUS_OK) {
            return Result.failure(LoadException(gifResponse.meta.msg))
        }

        if (gifResponse.data.isEmpty()) {
            return Result.failure(NothingFoundException())
        }

        return Result.success(gifResponse)
    }

    private fun GifResponse.toLoadFirstPageResult(): LoadFirstPageResult {
        val page = Page(1, data.toGifPictures().excludePreviouslyRemovedGifs())
        val totalPages = pagination.total_count / ITEMS_ON_PAGE
        return LoadFirstPageResult(page, totalPages)
    }

    private fun GifResponse.toPage(pageNumber: Int): Page {
        return Page(pageNumber, data.toGifPictures().excludePreviouslyRemovedGifs())
    }

    private fun List<GifPicture>.excludePreviouslyRemovedGifs(): List<GifPicture> {
        val previouslyRemovedIds = removedGifsStore.getAllRemovedGifIds().getOrNull() ?: emptyList()
        return filter { !previouslyRemovedIds.contains(it.id) }
    }
}