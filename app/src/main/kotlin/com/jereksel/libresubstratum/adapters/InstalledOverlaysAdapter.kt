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
import android.support.v7.util.DiffUtil
import android.support.v7.util.SortedList
import android.support.v7.util.SortedList.INVALID_POSITION
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotterknife.bindView
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.activities.installed.InstalledContract.Presenter
import com.jereksel.libresubstratum.adapters.InstalledOverlaysAdapter.ViewHolder
import com.jereksel.libresubstratum.data.InstalledOverlay
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async

class InstalledOverlaysAdapter(
        apps: List<InstalledOverlay>,
        val presenter: Presenter
): RecyclerView.Adapter<ViewHolder>() {

    val comparator = compareBy<InstalledOverlay>({ it.sourceThemeName.toLowerCase() }, { it.targetName.toLowerCase() }, { it.type1a },
            { it.type1b }, { it.type1c }, { it.type2 }, { it.type3 })

    val apps = apps.toMutableList()

    override fun getItemCount() = apps.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (destroyed) return

        val overlay = apps[position]

        val info = presenter.getOverlayInfo(overlay.overlayId)
        val overlayId = overlay.overlayId

        holder.targetIcon.setImageDrawable(overlay.targetDrawable)
        holder.themeIcon.setImageDrawable(overlay.sourceThemeDrawable)
        holder.targetName.text = "${overlay.targetName} - ${overlay.sourceThemeName}"
        val color = if(info?.enabled == true) Color.GREEN else Color.RED
        holder.targetName.setTextColor(color)

        holder.checkbox.setOnCheckedChangeListener(null)
        holder.checkbox.isChecked = presenter.getState(overlayId)
        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            presenter.setState(overlayId, isChecked)
        }

        holder.view.setOnClickListener {
            holder.checkbox.toggle()
        }

        holder.view.setOnLongClickListener {
            if (info != null) {
                async(UI) {
                    presenter.toggleOverlay(overlay.overlayId, !info.enabled)
                }
            }
            notifyItemChanged(position)
            true
        }

        listOf(holder.targetIcon, holder.themeIcon).forEach { it.setOnLongClickListener {
            if(!presenter.openActivity(overlay.targetId)) {
                Toast.makeText(it.context, "App cannot be opened", Toast.LENGTH_SHORT).show()
            }
            true
        }}

        listOf(
                Triple(overlay.type1a, holder.type1a, R.string.theme_type1a_list),
                Triple(overlay.type1b, holder.type1b, R.string.theme_type1b_list),
                Triple(overlay.type1c, holder.type1c, R.string.theme_type1c_list),
                Triple(overlay.type2, holder.type2, R.string.theme_type2_list),
                Triple(overlay.type3, holder.type3, R.string.theme_type3_list)
        ).forEach { (name, view, stringId) ->
            if (!name.isNullOrEmpty()) {
                val text = view.context.getString(stringId)
                view.text = Html.fromHtml("<b>$text:</b> $name")
                view.visibility = View.VISIBLE
            } else {
                view.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_installed, parent, false)
        return ViewHolder(v)
    }

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        val card: RelativeLayout by bindView(R.id.card)
        val targetIcon: ImageView by bindView(R.id.target_icon)
        val themeIcon: ImageView by bindView(R.id.theme_icon)
        val targetName: TextView by bindView(R.id.target_name)
        val checkbox: CheckBox by bindView(R.id.checkbox)
        val type1a: TextView by bindView(R.id.theme_type1a)
        val type1b: TextView by bindView(R.id.theme_type1b)
        val type1c: TextView by bindView(R.id.theme_type1c)
        val type2: TextView by bindView(R.id.theme_type2)
        val type3: TextView by bindView(R.id.theme_type3)
    }

    var destroyed = false

    fun destroy() {
        destroyed = true
    }

    class InstalledOverlayDiffCallback(
            val originalList: List<InstalledOverlay>,
            val newList: List<InstalledOverlay>
    ): DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return originalList[oldItemPosition].overlayId == newList[newItemPosition].overlayId
        }

        override fun getOldListSize() = originalList.size

        override fun getNewListSize() = newList.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return originalList[oldItemPosition] == newList[newItemPosition]
        }

    }

    fun updateOverlays(overlays: List<InstalledOverlay>) {

        val diff = DiffUtil.calculateDiff(InstalledOverlayDiffCallback(apps, overlays))

        apps.clear()
        apps.addAll(overlays)

        diff.dispatchUpdatesTo(this)

    }

}

