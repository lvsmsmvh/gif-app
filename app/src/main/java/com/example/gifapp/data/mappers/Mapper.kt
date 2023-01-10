package com.example.gifapp.data.mappers

import com.example.gifapp.data.gif_db_room.entities.GifDBEntity
import com.example.gifapp.data.gif_db_room.entities.RemovedGifDBEntity
import com.example.gifapp.data.gif_online_api.entities.GifObject
import com.example.gifapp.domain.entities.GifPicture

fun List<GifObject>.toGifPictures(): List<GifPicture> {
    return map {
        GifPicture(
            id = it.id,
            url = it.url,
            title = it.title,
            description = it.alt_text,
        )
    }
}

fun GifPicture.toGifDBEntity(): GifDBEntity {
    return GifDBEntity(
        id = id,
        uri = url,
        title = title,
        description = description,
    )
}

fun GifPicture.changeUrl(newUrl: String): GifPicture {
    return GifPicture(
        id = id,
        url = newUrl,
        title = title,
        description = description,
    )
}

fun GifDBEntity.toGifPicture(): GifPicture {
    return GifPicture(
        id = id,
        url = uri,
        title = title,
        description = description,
    )
}