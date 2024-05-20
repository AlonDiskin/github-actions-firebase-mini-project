package com.example.ui.util

import android.widget.ImageView
import com.bumptech.glide.Glide

object ImageLoader {

    fun loadIntoImageView(imageView: ImageView, url: String) {
        Glide
            .with(imageView.context)
            .load(url)
            .into(imageView)
    }
}