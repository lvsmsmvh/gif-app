package com.example.gifapp.data.gif_db_room.dao

import androidx.room.*
import com.example.gifapp.data.gif_db_room.entities.GifDBEntity
import com.example.gifapp.data.gif_db_room.entities.RemovedGifDBEntity

@Dao
interface RemovedGifsDao {
    @Query("SELECT * FROM removed_gifs")
    fun getAll(): List<RemovedGifDBEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(removedGifDBEntity: RemovedGifDBEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(removedGifDBEntities: List<RemovedGifDBEntity>)
}