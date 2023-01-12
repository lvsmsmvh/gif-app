package com.example.gifapp.data.gif_db_room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gifs")
data class GifDBEntity(
    @PrimaryKey val id: String,
    val uri: String,
    val title: String,
    val localUrl: String,
)