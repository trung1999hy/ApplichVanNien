package com.example.universalcalendar.ui.adapter

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide


@BindingAdapter("loadImage")
fun ImageView.loadImage(url: String) {
     Glide.with(context).load(Uri.parse("file:///android_asset/image/$url")).into(this)
}