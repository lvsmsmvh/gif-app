package com.example.gifapp.ui.adapters.gif_picture

import android.view.View
import com.example.gifapp.R
import com.example.gifapp.domain.entities.GifPicture

class GifPictureFullAdapter(
    onClicked: (GifPicture) -> Unit
) : BaseGifPictureAdapter<GifPictureFullViewHolder>(onClicked) {
    override fun createViewHolder(view: View) = GifPictureFullViewHolder(view)
    override val layoutRes: Int = R.layout.item_gif_picture_full
    override val scaleStrategy: ScaleStrategy = ScaleStrategy.CENTER_INSIDE
}