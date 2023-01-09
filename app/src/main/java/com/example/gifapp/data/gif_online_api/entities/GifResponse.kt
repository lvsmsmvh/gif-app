package com.example.gifapp.data.gif_online_api.entities

data class GifResponse(
    val data: List<GifObject>,
    val meta: MetaObject,
)