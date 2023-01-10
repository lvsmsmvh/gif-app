package com.example.gifapp.data.gif_db_room.dao

import androidx.room.*
import com.example.gifapp.data.gif_db_room.entities.GifDBEntity

@Dao
interface GifDao {
    @Query("SELECT COUNT(*) FROM gifs WHERE id NOT IN (SELECT * FROM removed_gifs)")
    fun amountGifs(): Int

    @Query("SELECT * FROM gifs WHERE id NOT IN (SELECT * FROM removed_gifs) LIMIT :from, :to")
    fun getGifEntities(from: Int, to: Int): List<GifDBEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(gifDBEntity: GifDBEntity)

    @Delete
    fun delete(gifDBEntity: GifDBEntity)

    @Delete
    fun delete(gifDBEntities: List<GifDBEntity>)

    @Query("SELECT * FROM gifs WHERE title LIKE '%' + :name + '%'")
    fun search(name: String): List<GifDBEntity>
}