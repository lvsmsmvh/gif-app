package com.example.gifapp.domain.reposities

interface RemovedGifsRepository {
    fun markRemoved(gifId: String): Result<Boolean>
    fun markRemoved(gifIds: List<String>): Result<Boolean>
    fun getAllRemovedGifIds(): Result<List<String>>
}