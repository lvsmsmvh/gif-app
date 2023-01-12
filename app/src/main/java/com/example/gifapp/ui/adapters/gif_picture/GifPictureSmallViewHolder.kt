package com.example.gifapp.ui.adapters.gif_picture

import android.view.View
import android.widget.ImageView
import com.example.gifapp.R
import com.example.gifapp.domain.entities.GifPicture


class GifPictureSmallViewHolder(private val view: View) : BaseGifPictureViewHolder(view) {

    override fun bind(item: GifPicture, onClicked: (GifPicture) -> Unit): ImageView {
        val imageView = view.findViewById<ImageView>(R.id.item_gif_picture_image)
        imageView.setOnClickListener { onClicked(item) }
        return imageView
    }
}