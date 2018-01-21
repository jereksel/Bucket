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

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.activities.detailed.DetailedContract.Presenter
import com.jereksel.libresubstratum.data.Type1ExtensionToString
import com.jereksel.libresubstratum.data.Type2ExtensionToString
import com.jereksel.libresubstratum.extensions.list
import com.jereksel.libresubstratum.views.TypeView
import kotterknife.bindView

class ThemePackAdapter(
        val presenter: Presenter
) : RecyclerView.Adapter<ThemePackAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        presenter.setAdapterView(position, holder)

        holder.card.setOnClickListener {
            holder.checkbox.toggle()
        }

        holder.card.setOnLongClickListener {
            presenter.compileAndRun(holder.adapterPosition)
            true
        }

        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            presenter.setCheckbox(holder.adapterPosition, isChecked)
        }

//        holder.type1aView.onPositionChange { presenter.setType1a(holder.adapterPosition, it) }
//
//        holder.type1bView.onPositionChange { presenter.setType1b(holder.adapterPosition, it) }
//
//        holder.type1cView.onPositionChange { presenter.setType1c(holder.adapterPosition, it) }

        holder.type2Spinner.selectListener { spinnerPosition ->
            presenter.setType2(holder.adapterPosition, spinnerPosition)
        }

        holder.appIcon.setOnLongClickListener { presenter.openInSplit(holder.adapterPosition); true }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_detailed, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = presenter.getNumberOfThemes()

//    override fun getItemCount() = themePack.themes.size

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view), ThemePackAdapterView {

        val card: CardView by bindView(R.id.card_view)
        val checkbox: CheckBox by bindView(R.id.checkbox)

        val appName: TextView by bindView(R.id.appName)
        val appId: TextView by bindView(R.id.appId)
        val appIcon: ImageView by bindView(R.id.imageView)

        val upToDate: TextView by bindView(R.id.uptodate)

//        val type1aView: TypeView by bindView(R.id.type1aview)
//        val type1bView: TypeView by bindView(R.id.type1bview)
//        val type1cView: TypeView by bindView(R.id.type1cview)

        val type2Spinner: Spinner by bindView(R.id.spinner_2)

        val overlay: RelativeLayout by bindView(R.id.overlay)
//        init {
//            (type1Spinners + type2Spinner).forEach { it.visibility = View.GONE }
//        }

        override fun setAppId(id: String) {
            appId.text = id
        }

        override fun setAppName(name: String) {
            appName.setTextColor(Color.BLACK)
            appName.text = name
        }

        override fun setAppIcon(icon: Drawable?) {
            appIcon.setImageDrawable(icon)
        }

        override fun setCheckbox(checked: Boolean) {
//            checkbox.setOnCheckedChangeListener(null)
            if (checkbox.isChecked != checked) {
                checkbox.isChecked = checked
            }
        }

        override fun type1aSpinner(list: List<Type1ExtensionToString>, position: Int) {
//            if (list.isEmpty()) {
//                type1aView.visibility = GONE
//            } else {
//                type1aView.visibility = VISIBLE
//                type1aView.setType1(list)
//                type1aView.setSelection(position)
//            }
        }

        override fun type1bSpinner(list: List<Type1ExtensionToString>, position: Int) {
//            if (list.isEmpty()) {
//                type1bView.visibility = GONE
//            } else {
//                type1bView.visibility = VISIBLE
//                type1bView.setType1(list)
//                type1bView.setSelection(position)
//            }
        }


        override fun type1cSpinner(list: List<Type1ExtensionToString>, position: Int) {
//            if (list.isEmpty()) {
//                type1cView.visibility = GONE
//            } else {
//                type1cView.visibility = VISIBLE
//                type1cView.setType1(list)
//                type1cView.setSelection(position)
//            }
        }

        override fun type2Spinner(list: List<Type2ExtensionToString>, position: Int) {
//            type2Spinner.onItemSelectedListener = null
            if (list.isEmpty()) {
                type2Spinner.visibility = GONE
            } else {
                type2Spinner.visibility = VISIBLE
                type2Spinner.list = list
                type2Spinner.setSelection(position)
            }
        }

        override fun setEnabled(enabled: Boolean) {
            appName.setTextColor(if (enabled) Color.GREEN else Color.RED)
        }

        override fun setCompiling(compiling: Boolean) {
            overlay.visibility = if (compiling) VISIBLE else GONE
        }

        override fun setInstalled(version1: String?, version2: String?) {
            if (version1 == null && version2 == null) {
                upToDate.setTextColor(Color.GREEN)
                upToDate.text = "Up to date"
            } else {
                upToDate.setTextColor(Color.RED)
                upToDate.text = "Update available: $version1 -> $version2"
            }
        }

        override fun reset() {
            upToDate.text = null
        }

    }

    private fun Spinner.selectListener(fn: (Int) -> Unit) {

        var user = false

        this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit

            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (user) {
                    fn(position)
                } else {
                    user = true
                }
            }
        }
    }
}
