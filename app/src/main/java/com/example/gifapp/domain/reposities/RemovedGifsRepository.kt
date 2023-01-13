package com.example.gifapp.domain.reposities

interface RemovedGifsRepository {
    fun markRemoved(gifId: String)
    fun getAllRemovedGifIds(): Result<List<String>>
}