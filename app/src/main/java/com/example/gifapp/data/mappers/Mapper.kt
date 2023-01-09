package com.example.gifapp.data.mappers

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