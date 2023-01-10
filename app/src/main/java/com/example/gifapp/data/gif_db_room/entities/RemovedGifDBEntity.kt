package com.example.gifapp.data.gif_db_room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "removed_gifs")
data class RemovedGifDBEntity(
    @PrimaryKey val id: String,
)