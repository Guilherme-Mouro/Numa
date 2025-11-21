package com.example.numa.util

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.widget.ImageView

class FixPixelArt(context: Context) {

    fun removeFilter(image: ImageView) {
        image.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        (image.drawable as BitmapDrawable).paint.isFilterBitmap = false
    }
}