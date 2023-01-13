package com.example.gifapp.data.repository

import com.example.gifapp.data.gif_db_room.dao.GifDao
import com.example.gifapp.data.mappers.toGifDBEntity
import com.example.gifapp.data.mappers.toGifPicture
import com.example.gifapp.domain.entities.GifPicture
import com.example.gifapp.domain.entities.Page
import com.example.gifapp.domain.exceptions.NothingFoundException
import com.example.gifapp.domain.reposities.LocalGifRepository
import com.example.gifapp.domain.reposities.RemovedGifsRepository
import com.example.gifapp.utils.logDebug
import javax.inject.Inject
import kotlin.math.min

class LocalRoomImpl @Inject constructor(
    private val gifDao: GifDao,
    private val removedGifsStore: RemovedGifsRepository,
) : LocalGifRepository {

    companion object {
        private const val ITEMS_ON_PAGE = 48
    }

    override fun remove(gifPictures: List<GifPicture>): Result<Boolean> {
        gifPictures.forEach { remove(it) }
        return Result.success(true)
    }

    override fun remove(gifPicture: GifPicture): Result<Boolean> {
        removedGifsStore.markRemoved(gifPicture.id)
        gifDao.delete(gifPicture.id)
        return Result.success(true)
    }

    override fun getLocalUrl(gifPicture: GifPicture): String? {
        return gifDao.getById(gifPicture.id)?.localUrl
    }

    override fun saveLocalUrl(gifPicture: GifPicture, url: String) {
        gifDao.insert(gifPicture.toGifDBEntity(url))
    }

    override suspend fun loadPage(pageIndex: Int, query: String): Result<Page> {
        val offset = (pageIndex - 1) * ITEMS_ON_PAGE
        val limit = ITEMS_ON_PAGE
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

        val pagesAmount = totalItemsAvailable / ITEMS_ON_PAGE

        val gifPictures = entities.map { it.toGifPicture() }
        if (gifPictures.isEmpty()) {
            return Result.failure(NothingFoundException())
        }
        val page = Page(pagesAmount, pageIndex, query, gifPictures)

        logDebug("DBRoom return page from $offset")
        logDebug("DBRoom return page to $limit")
        logDebug("DBRoom return page $pageIndex, total pages $pagesAmount, items ${gifPictures.size}")
        return Result.success(page)
    }

//    override fun replaceWithLocalUrls(page: Page): Result<Page> {
//        val onlineGifPictures = page.gifPictures
//        val localGifPictures = gifDao.getGifEntities().map { it.toGifPicture() }
//        logDebug("Local gif pictures found: ${localGifPictures.size}")
//        val localGifPictureIds = localGifPictures.map { it.id }
//        val newList = onlineGifPictures.map { onlineGif ->
//            onlineGif.takeUnless { localGifPictureIds.contains(onlineGif.id) }
//                ?: localGifPictures.first { onlineGif.id == it.id }
//        }
//        return Result.success(Page(page.pagesAmount, page.pageNumber, page.query, newList))
//    }
}