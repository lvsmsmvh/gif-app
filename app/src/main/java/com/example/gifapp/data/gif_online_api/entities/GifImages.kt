package com.example.gifapp.data.gif_online_api.entities

data class GifImages(
    val fixed_width: GifImagesFixedWidth,
    val fixed_width_small: GifImagesFixedWidthSmall,
    val downsized_medium: GifImagesDownsizedMedium,
    val original: GifImagesOriginal,
    val preview_gif: GifImagesPreviewGif,
)