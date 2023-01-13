package com.example.gifapp.data.repository

import com.example.gifapp.data.gif_db_room.dao.RemovedGifsDao
import com.example.gifapp.data.gif_db_room.entities.RemovedGifDBEntity
import com.example.gifapp.domain.reposities.RemovedGifsRepository
import javax.inject.Inject

class RemovedGifsRoomImpl @Inject constructor(
    private val removedGifsDao: RemovedGifsDao,
): RemovedGifsRepository {
    override fun markRemoved(gifId: String): Result<Boolean> {
        val entity = RemovedGifDBEntity(gifId)
        removedGifsDao.insert(entity)
        return Result.success(true)
    }

    override fun getAllRemovedGifIds(): Result<List<String>> {
        val entities = removedGifsDao.getAll()
        val ids = entities.map { it.id }
        return Result.success(ids)
    }
}