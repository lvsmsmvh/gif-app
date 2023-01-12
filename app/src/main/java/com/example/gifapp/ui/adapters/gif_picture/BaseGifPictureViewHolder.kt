package com.example.gifapp.ui.adapters.gif_picture

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.gifapp.domain.entities.GifPicture


abstract class BaseGifPictureViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(item: GifPicture, onClicked: (GifPicture) -> Unit): ImageView
}