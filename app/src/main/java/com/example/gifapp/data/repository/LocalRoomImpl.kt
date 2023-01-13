package com.example.gifapp.data.repository

import com.example.gifapp.data.gif_db_room.dao.GifDao
import com.example.gifapp.data.mappers.toGifDBEntity
import com.example.gifapp.data.mappers.toGifPicture
import com.example.gifapp.domain.common.GIF_ITEMS_ON_PAGE
import com.example.gifapp.domain.entities.GifPicture
import com.example.gifapp.domain.entities.Page
import com.example.gifapp.domain.exceptions.NothingFoundException
import com.example.gifapp.domain.reposities.LocalGifRepository
import com.example.gifapp.utils.logDebug
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.min

class LocalRoomImpl @Inject constructor(
    private val gifDao: GifDao,
) : LocalGifRepository {

    override fun remove(gifPictureId: String) {
        gifDao.delete(gifPictureId)
    }

    override fun getLocalUrl(gifPicture: GifPicture): String? {
        return gifDao.getById(gifPicture.id)?.localUrl
    }

    override fun saveLocalUrl(gifPicture: GifPicture, url: String) {
        gifDao.insert(gifPicture.toGifDBEntity(url))
    }

    override suspend fun loadPage(pageIndex: Int, query: String): Result<Page> {
        val offset = (pageIndex - 1) * GIF_ITEMS_ON_PAGE
        val limit = GIF_ITEMS_ON_PAGE
        var totalItemsAvailable = 0
        val entities = when (query.isBlank()) {
            true -> {
                totalItemsAvailable = gifDao.amountGifs()
                gifDao.getGifEntities(limit, offset)
            }
            false -> {
                val allResults = gifDao.search(query)
                totalItemsAvailable = allResults.size
                val safeLimit = min(totalItemsAvailable - offset, limit)
                allResults.subList(offset, offset + safeLimit)
            }
        }

        // (a -1) / b + 1 to get pages
        val pagesAmount = (totalItemsAvailable - 1) / GIF_ITEMS_ON_PAGE + 1

        val gifPictures = entities.map { it.toGifPicture() }
        if (gifPictures.isEmpty()) {
            return Result.failure(NothingFoundException())
        }

        logDebug("load local page, totalItemsAvailable = $totalItemsAvailable, pages = $pagesAmount")
        val page = Page(pagesAmount, pageIndex, query, gifPictures)
        return Result.success(page)
    }
}