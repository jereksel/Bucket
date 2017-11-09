package com.jereksel.libresubstratum.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.graphics.Palette
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.data.Type1ExtensionToString
import org.jetbrains.anko.find
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.textColor

class Type1SpinnerArrayAdapter(
        context: Context,
        val objects: List<Type1ExtensionToString>
) : ArrayAdapter<Type1ExtensionToString>(context, 0, objects) {

//    private val objects = objects.sortedWith(compareBy({ !it.type1.default }, {
//        val hsv = FloatArray(3)
//        val rgb = it.type1.color
//        if (rgb.isEmpty()) {
//            Float.MAX_VALUE
//        } else {
//            Color.colorToHSV(Color.parseColor(rgb), hsv)
//            hsv[0]
//        }
//    }))

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view = context.layoutInflater.inflate(R.layout.item_type1spinner, null)
        val type1Extension = objects[position]

        val textView = view.find<TextView>(R.id.textView)

        textView.text = type1Extension.toString()

        if (type1Extension.type1.color.isNotEmpty()) {
            val type1Color = Color.parseColor(type1Extension.type1.color)
            val swatch = Palette.Swatch(type1Color, 1)
            textView.textColor = swatch.titleTextColor
            view.background = ColorDrawable(type1Color)
        } else {
            val type1Color = Color.WHITE
            textView.textColor = Color.BLACK
            view.background = ColorDrawable(type1Color)
        }

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }
}

