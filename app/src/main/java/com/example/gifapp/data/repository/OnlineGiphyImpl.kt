package com.example.gifapp.data.repository

import com.example.gifapp.data.gif_online_api.GifApi
import com.example.gifapp.data.mappers.toGifPictures
import com.example.gifapp.domain.common.GIF_ITEMS_ON_PAGE
import com.example.gifapp.domain.entities.GifPicture
import com.example.gifapp.domain.entities.Page
import com.example.gifapp.domain.exceptions.LoadException
import com.example.gifapp.domain.exceptions.NothingFoundException
import com.example.gifapp.domain.reposities.OnlineGifRepository
import com.example.gifapp.domain.reposities.RemovedGifsRepository
import java.io.IOException
import javax.inject.Inject

class OnlineGiphyImpl @Inject constructor(
    private val gifApi: GifApi,
    private val removedGifsStore: RemovedGifsRepository,
) : OnlineGifRepository {

    companion object {
        private const val API_KEY = "joROvSscQyfLRIpTj5JFKSk6FIJ2obMa"
        private const val STATUS_OK = 200
    }

    override suspend fun loadPage(pageIndex: Int, query: String): Result<Page> {
        val gifResponse = try {
            when (query.isBlank()) {
                true -> gifApi.getGifs(
                    api_key = API_KEY,
                    limit = GIF_ITEMS_ON_PAGE,
                    offset = (pageIndex - 1) * GIF_ITEMS_ON_PAGE,
                )
                false -> gifApi.searchGifs(
                    api_key = API_KEY,
                    search_string = query,
                    limit = GIF_ITEMS_ON_PAGE,
                    offset = (pageIndex - 1) * GIF_ITEMS_ON_PAGE,
                )
            }
        } catch (e: IOException) {
            return Result.failure(LoadException(e.message.toString()))
        } catch (e: RuntimeException) {
            return Result.failure(LoadException(e.message.toString()))
        }

        if (gifResponse.meta.status != STATUS_OK) {
            return Result.failure(LoadException(gifResponse.meta.msg))
        }

        if (gifResponse.data.isEmpty()) {
            return Result.failure(NothingFoundException())
        }

        val totalPages = gifResponse.pagination.total_count / GIF_ITEMS_ON_PAGE
        val gifPictures = gifResponse.data.toGifPictures().excludePreviouslyRemovedGifs()
        val page = Page(totalPages, pageIndex, query, gifPictures)
        return Result.success(page)
    }

    private fun List<GifPicture>.excludePreviouslyRemovedGifs(): List<GifPicture> {
        val previouslyRemovedIds = removedGifsStore.getAllRemovedGifIds().getOrNull() ?: emptyList()
        return filter { !previouslyRemovedIds.contains(it.id) }
    }
}