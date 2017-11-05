package com.jereksel.libresubstratum.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.data.Type1ExtensionToString
import org.jetbrains.anko.find
import org.jetbrains.anko.layoutInflater

class Type1SpinnerArrayAdapter(
        context: Context,
        val objects: List<Type1ExtensionToString>
) : ArrayAdapter<Type1ExtensionToString>(context, 0, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view = context.layoutInflater.inflate(R.layout.item_type1spinner, null)
        val type1Extension = objects[position]


        view.find<TextView>(R.id.textView).text = type1Extension.toString()

        val colorDrawable = if (type1Extension.type1.color.isNotEmpty()) {
            ColorDrawable(Color.parseColor(type1Extension.type1.color))
        } else {
            ColorDrawable(Color.TRANSPARENT)
        }

        view.find<ImageView>(R.id.imageView).setImageDrawable(colorDrawable)
        return view

//        return super.getView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }
}

