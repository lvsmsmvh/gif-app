package com.example.gifapp.data.mappers

import com.example.gifapp.data.gif_db_room.entities.GifDBEntity
import com.example.gifapp.data.gif_online_api.entities.GifObject
import com.example.gifapp.domain.entities.GifPicture

fun List<GifObject>.toGifPictures(): List<GifPicture> {
    return map {
        GifPicture(
            id = it.id,
            url = it.images.fixed_width_small.url ?: it.images.fixed_width.url,
            title = it.title,
        )
    }
}

fun GifPicture.toGifDBEntity(localUrl: String): GifDBEntity {
    return GifDBEntity(
        id = id,
        uri = url,
        title = title,
        localUrl = localUrl
    )
}

fun GifPicture.changeUrl(newUrl: String): GifPicture {
    return GifPicture(
        id = id,
        url = newUrl,
        title = title,
    )
}

fun GifDBEntity.toGifPicture(): GifPicture {
    return GifPicture(
        id = id,
        url = uri,
        title = title,
    )
}