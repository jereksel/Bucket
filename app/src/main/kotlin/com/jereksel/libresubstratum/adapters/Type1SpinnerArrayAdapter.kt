/*
 * Copyright (C) 2017 Andrzej Ressel (jereksel@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.jereksel.libresubstratum.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v4.graphics.ColorUtils
import android.support.v7.graphics.Palette
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import arrow.syntax.validated.valid
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.data.Type1ExtensionToString
import org.jetbrains.anko.backgroundColor
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

        val holder = if (convertView != null) {
            convertView.tag as ViewHolder
        } else {
            val view = context.layoutInflater.inflate(R.layout.item_type1spinner_dropdown, null)

            val holder = ViewHolder(
                    row = view,
                    textView = view.find(R.id.textView)
            )

            view.tag = holder
            holder
        }

        val type1Extension = objects[position]

        holder.textView.text = type1Extension.toString()

        if (type1Extension.type1.color.isNotEmpty()) {
            val type1Color = ColorUtils.setAlphaComponent(Color.parseColor(type1Extension.type1.color), 0xFF)
            val swatch = Palette.Swatch(type1Color, 1)
            holder.textView.textColor = swatch.titleTextColor
            holder.row.backgroundColor = type1Color
        } else {
            val type1Color = Color.WHITE
            holder.textView.textColor = Color.BLACK
            holder.row.backgroundColor = type1Color
        }

        return holder.row
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {

        val holder = if (convertView != null) {
            convertView.tag as ViewHolder
        } else {
            val view = context.layoutInflater.inflate(R.layout.item_type1spinner_dropdown, null)

            val holder = ViewHolder(
                    row = view,
                    textView = view.find(R.id.textView)
            )

            view.tag = holder
            holder
        }

        val type1Extension = objects[position]

        val textView = holder.textView
        val view = holder.row

        holder.textView.text = type1Extension.toString()

        if (type1Extension.type1.color.isNotEmpty()) {
            val type1Color = ColorUtils.setAlphaComponent(Color.parseColor(type1Extension.type1.color), 0xFF)
            val swatch = Palette.Swatch(type1Color, 1)
            textView.textColor = swatch.titleTextColor
            view.backgroundColor = type1Color
        } else {
            val type1Color = Color.WHITE
            textView.textColor = Color.BLACK
            view.backgroundColor = type1Color
        }

        return holder.row

    }

    data class ViewHolder(
            val textView: TextView,
            val row: View
    )

}

