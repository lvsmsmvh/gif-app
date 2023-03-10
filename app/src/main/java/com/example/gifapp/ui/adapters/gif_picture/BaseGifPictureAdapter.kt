package com.example.gifapp.ui.adapters.gif_picture

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.gifapp.R
import com.example.gifapp.domain.entities.GifPicture
import com.example.gifapp.ui.adapters.base.BaseRvAdapter
import com.example.gifapp.ui.viewmodels.LoadingState
import com.example.gifapp.ui.viewmodels.asFailed
import com.example.gifapp.ui.viewmodels.asLoaded

abstract class BaseGifPictureAdapter<VH : BaseGifPictureViewHolder>(
    private val onClicked: (GifPicture) -> Unit,
) : BaseRvAdapter<GifPicture, VH>() {

    protected open val overrideWidth = -1
    protected open val overrideHeight = -1

    enum class ScaleStrategy { CENTER_CROP, CENTER_INSIDE }

    abstract val scaleStrategy: ScaleStrategy

    private data class GifPictureData(
        var imageView: ImageView?,
        var localUrl: LoadingState<String>,
        var isSet: Boolean,
    )
    private val gifPicturesData = mutableMapOf<GifPicture, GifPictureData>()

    override fun set(newItems: List<GifPicture>) {
        super.set(newItems)
        gifPicturesData.clear()
        gifPicturesData.putAll(newItems.associateWith {
            GifPictureData(null, LoadingState.Loading, false)
        })
    }

    fun clear() {
        gifPicturesData.clear()
        super.set(emptyList())
    }

    fun addUrl(gifPicture: GifPicture, loadingState: LoadingState<String>) {
        val gifPictureData = gifPicturesData[gifPicture]
        gifPictureData?.localUrl = loadingState
        tryToSetImage(gifPicture)
    }

    private fun tryToSetImage(gifPicture: GifPicture) {
        val gifPictureData = gifPicturesData[gifPicture]
        val image = gifPictureData?.imageView ?: return
        if (gifPictureData.isSet) return

        gifPictureData.localUrl.asLoaded()?.result?.let { localUrl ->
            Glide.with(image.context)
                .asGif()
                .load(localUrl)
                .run {
                    when (scaleStrategy) {
                        ScaleStrategy.CENTER_CROP -> centerCrop()
                        ScaleStrategy.CENTER_INSIDE -> centerInside()
                    }
                }
                .override(overrideWidth, overrideHeight)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(image)
            gifPictureData.isSet = true
        }
        gifPictureData.localUrl.asFailed()?.run {
            Glide.with(image.context)
                .load(R.drawable.ic_error)
                .run {
                    when (scaleStrategy) {
                        ScaleStrategy.CENTER_CROP -> centerCrop()
                        ScaleStrategy.CENTER_INSIDE -> centerInside()
                    }
                }
                .override(overrideWidth, overrideHeight)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(image)
            gifPictureData.isSet = true
        }
    }

    override fun onBind(holder: VH, item: GifPicture) {
        val imageView = holder.bind(item, onClicked)
        val gifPictureData = gifPicturesData[item]
        gifPictureData?.imageView = imageView
        tryToSetImage(item)
    }
}