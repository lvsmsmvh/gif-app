package com.example.gifapp.data.repository

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.example.gifapp.data.gif_db_room.dao.GifDao
import com.example.gifapp.data.gif_db_room.entities.GifDBEntity
import com.example.gifapp.data.mappers.changeUrl
import com.example.gifapp.data.mappers.toGifDBEntity
import com.example.gifapp.data.mappers.toGifPicture
import com.example.gifapp.domain.entities.GifPicture
import com.example.gifapp.domain.entities.LoadFirstPageResult
import com.example.gifapp.domain.entities.Page
import com.example.gifapp.domain.exceptions.NothingFoundException
import com.example.gifapp.domain.reposities.LocalGifRepository
import com.example.gifapp.domain.reposities.RemovedGifsRepository
import java.io.IOException
import javax.inject.Inject

class LocalRoomToMediaStoreImpl @Inject constructor(
    private val context: Context,
    private val gifDao: GifDao,
    private val removedGifsStore: RemovedGifsRepository,
) : LocalGifRepository {

    companion object {
        private const val ITEMS_ON_PAGE = 25
    }

    override fun remove(gifPictures: List<GifPicture>): Result<Boolean> {
        removedGifsStore.markRemoved(gifPictures.map { it.id })
        val entities = gifPictures.map { it.toGifDBEntity() }
        gifDao.delete(entities)
        return Result.success(true)
    }

    override fun save(gifPicture: GifPicture, bitmap: Bitmap): Result<Boolean> {
        val uri = saveBitmap(context)
        gifDao.insert(gifPicture.changeUrl(uri).toGifDBEntity())
        return Result.success(true)
    }

    override suspend fun loadFirstPage(): Result<LoadFirstPageResult> {
        return prepareFirstPageResult(
            entities = gifDao.getGifEntities(0, ITEMS_ON_PAGE),
            totalItemsAvailable = gifDao.amountGifs() / ITEMS_ON_PAGE
        )
    }

    override suspend fun loadPage(pageIndex: Int): Result<Page> {
        val from = (pageIndex - 1) * ITEMS_ON_PAGE
        val to = from + ITEMS_ON_PAGE
        return preparePageResult(
            entities = gifDao.getGifEntities(from, to),
            pageIndex = pageIndex
        )
    }


    override suspend fun search(name: String): Result<LoadFirstPageResult> {
        val entities = gifDao.search(name)
        return prepareFirstPageResult(
            entities = entities.take(ITEMS_ON_PAGE),
            totalItemsAvailable = entities.size / ITEMS_ON_PAGE
        )
    }

    override suspend fun searchPage(pageIndex: Int, name: String): Result<Page> {
        val from = (pageIndex - 1) * ITEMS_ON_PAGE
        val to = from + ITEMS_ON_PAGE
        return preparePageResult(
            entities = gifDao.search(name).subList(from, to),
            pageIndex = pageIndex
        )
    }

    private fun preparePageResult(entities: List<GifDBEntity>, pageIndex: Int): Result<Page> {
        val gifPictures = entities.map { it.toGifPicture() }
        if (gifPictures.isEmpty()) {
            return Result.failure(NothingFoundException())
        }
        val page = Page(pageIndex, gifPictures)
        return Result.success(page)
    }

    private fun prepareFirstPageResult(
        entities: List<GifDBEntity>,
        totalItemsAvailable: Int,
    ): Result<LoadFirstPageResult> {
        val totalPages = totalItemsAvailable / ITEMS_ON_PAGE
        val firstPageEntities = entities.take(ITEMS_ON_PAGE)
        val gifPictures = firstPageEntities.map { it.toGifPicture() }
        if (gifPictures.isEmpty()) {
            return Result.failure(NothingFoundException())
        }
        val page = Page(1, gifPictures)
        val loadFirstPageResult = LoadFirstPageResult(page, pagesAmount = totalPages)
        return Result.success(loadFirstPageResult)
    }

    private fun saveBitmap(
        context: Context, bitmap: Bitmap, format: Bitmap.CompressFormat,
        mimeType: String, displayName: String
    ): String {
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
        }

        val resolver = context.contentResolver
        var uri: Uri? = null

        try {
            uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: throw IOException("Failed to create new MediaStore record.")

            resolver.openOutputStream(uri)?.use {
                if (!bitmap.compress(format, 95, it))
                    throw IOException("Failed to save bitmap.")
            } ?: throw IOException("Failed to open output stream.")
        } catch (e: IOException) {
            uri?.let { orphanUri ->
                resolver.delete(orphanUri, null, null)
            }

            e.printStackTrace()
        }

        return "uri"
    }
}