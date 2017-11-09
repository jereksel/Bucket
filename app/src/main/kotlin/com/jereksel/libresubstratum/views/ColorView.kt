package com.jereksel.libresubstratum.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar

class ColorView: View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    val paint = Paint()

    var colors = listOf<Int>()

    //For edit mode
    val rainbow = listOf(
            Color.RED,
            Color.parseColor("#FF7F00"),
            Color.YELLOW,
            Color.GREEN,
            Color.BLUE,
            Color.parseColor("#4B0082"),
            Color.parseColor("#8F00FF")
    )

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val colors = if (isInEditMode) {
            rainbow
        } else if(!colors.isEmpty()) {
            colors
        } else {
            listOf(Color.TRANSPARENT)
        }

        val blockSize = width/colors.size.toFloat()

        colors.forEachIndexed { index, color ->

            val start = index*blockSize
            val end = (index + 1)*blockSize

            paint.color = color

            canvas.drawRect(start, 0f, end, height.toFloat(), paint)

        }
//
//        SeekBar(context).draw(canvas)

    }


    override fun onTouchEvent(event: MotionEvent) = true

    override fun onDragEvent(event: DragEvent?) = true

}