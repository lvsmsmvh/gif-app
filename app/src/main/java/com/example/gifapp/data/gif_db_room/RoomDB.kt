package com.example.gifapp.data.gif_db_room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.gifapp.data.gif_db_room.dao.GifDao
import com.example.gifapp.data.gif_db_room.dao.RemovedGifsDao
import com.example.gifapp.data.gif_db_room.entities.GifDBEntity
import com.example.gifapp.data.gif_db_room.entities.RemovedGifDBEntity

const val DATABASE_NAME = "room_local_db"
private const val CURRENT_DB_VERSION = 1

@Database(
    entities = [
        GifDBEntity::class,
        RemovedGifDBEntity::class,
    ],
    version = CURRENT_DB_VERSION
)
abstract class RoomDB : RoomDatabase() {
    abstract fun gifDao(): GifDao
    abstract fun removedGifsDao(): RemovedGifsDao
}