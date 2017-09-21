package com.jereksel.libresubstratum.domain

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.v4.util.LruCache
import com.jereksel.libresubstratum.data.MainViewTheme


class BitmapLruCache(private val apps: List<MainViewTheme>): LruCache<Int, Bitmap>(((Runtime.getRuntime().maxMemory() / 1024) / 2).toInt()) {
    override fun create(key: Int): Bitmap? {
//        return super.create(key)
        val drawable = apps[key].heroImage()
        if (drawable != null) {
            return drawableToBitmap(drawable)
        } else {
            return null
        }
    }

    override fun sizeOf(key: Int, bitmap: Bitmap) = bitmap.byteCount / 1024

    //https://stackoverflow.com/questions/3035692/how-to-convert-a-drawable-to-a-bitmap
    fun drawableToBitmap(drawable: Drawable): Bitmap {

        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }

        val bitmap: Bitmap

        if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_4444) // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        }

        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

}