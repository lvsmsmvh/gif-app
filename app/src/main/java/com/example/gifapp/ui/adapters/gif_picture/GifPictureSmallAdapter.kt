package com.example.gifapp.ui.adapters.gif_picture

import android.view.View
import com.example.gifapp.R
import com.example.gifapp.domain.entities.GifPicture

class GifPictureSmallAdapter(
    onClicked: (GifPicture) -> Unit
) : BaseGifPictureAdapter<GifPictureSmallViewHolder>(onClicked) {
    override val overrideWidth = 200
    override val overrideHeight = 200
    override fun createViewHolder(view: View) = GifPictureSmallViewHolder(view)
    override val layoutRes: Int = R.layout.item_gif_picture
    override val scaleStrategy: ScaleStrategy = ScaleStrategy.CENTER_CROP
}