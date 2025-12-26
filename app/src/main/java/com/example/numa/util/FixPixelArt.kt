package com.example.numa.util

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.widget.ImageView

class FixPixelArt(context: Context) {
companion object {
    fun removeFilter(image: ImageView) {
        image.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        (image.drawable as BitmapDrawable).paint.isFilterBitmap = false
    }

    fun removeAnimFilter(image: ImageView) {
        image.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        val background = image.background

        if (background is AnimationDrawable) {
            for (i in 0 until background.numberOfFrames) {
                val frame = background.getFrame(i)
                if (frame is BitmapDrawable) {
                    frame.paint.isFilterBitmap = false
                }
            }
        } else if (background is BitmapDrawable) {
            background.paint.isFilterBitmap = false
        }
    }
}
}