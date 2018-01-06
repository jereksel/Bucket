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
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.Html
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.activities.prioritiesdetail.PrioritiesDetailContract.Presenter
import com.jereksel.libresubstratum.data.InstalledOverlay
import com.jereksel.libresubstratum.extensions.getLogger
import com.jereksel.libresubstratum.utils.SimpleDiffCallback
import kotterknife.bindView
import org.jetbrains.anko.sdk25.coroutines.onLongClick
import java.util.*
import kotlin.collections.ArrayList

class PrioritiesDetailAdapter(
        overlays: List<InstalledOverlay>,
        val presenter: Presenter
): RecyclerView.Adapter<PrioritiesDetailAdapter.ViewHolder>() {

    val log = getLogger()

    val overlays = ArrayList(overlays)

    lateinit var itemTouchListener: ItemTouchHelper

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val overlay = overlays[position]

        val isEnabled = presenter.isEnabled(overlay.overlayId)

        val color = if(isEnabled) Color.GREEN else Color.RED
        holder.targetName.setTextColor(color)

        holder.targetIcon.setImageDrawable(overlay.targetDrawable)
        holder.themeIcon.setImageDrawable(overlay.sourceThemeDrawable)
        holder.targetName.text = "${overlay.targetName} - ${overlay.sourceThemeName}"

        val listener = View.OnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                itemTouchListener.startDrag(holder)
            }
            false
        }

        holder.card.setOnClickListener {

            @Suppress("NAME_SHADOWING")
            val position = holder.adapterPosition

            val oldOverlays = overlays.toMutableList()

            val o = overlays.removeAt(position)
            overlays.add(0, o)
            presenter.updateOverlays(overlays)

            val newOverlays = overlays.toMutableList()

            DiffUtil.calculateDiff(SimpleDiffCallback(oldOverlays, newOverlays)).dispatchUpdatesTo(this)
        }

        holder.card.onLongClick(returnValue = true) {
            presenter.toggleOverlay(overlay.overlayId)
            notifyItemChanged(holder.adapterPosition)
        }

        holder.reorder.setOnTouchListener(listener)

        holder.themeIcon.setOnLongClickListener {
            presenter.openAppInSplit(overlay.targetId)
            true
        }

        holder.targetIcon.setOnLongClickListener {
            presenter.openAppInSplit(overlay.targetId)
            true
        }

        listOf(
                Triple(overlay.type1a, holder.type1a, R.string.theme_type1a_list),
                Triple(overlay.type1b, holder.type1b, R.string.theme_type1b_list),
                Triple(overlay.type1c, holder.type1c, R.string.theme_type1c_list),
                Triple(overlay.type2, holder.type2, R.string.theme_type2_list),
                Triple(overlay.type3, holder.type3, R.string.theme_type3_list)
        ).forEach { (name, view, stringId) ->
            if (!name.isNullOrEmpty()) {
                val text = view.context.getString(stringId)
                view.text = Html.fromHtml("<b>$text:</b> ${name?.replace("_", " ")}")
                view.visibility = View.VISIBLE
            } else {
                view.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_priorities_detail, parent, false)
        return ViewHolder(v)
    }

    fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(overlays, fromPosition, toPosition)
        presenter.updateOverlays(overlays)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun getItemCount() = overlays.size

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        val card: RelativeLayout by bindView(R.id.card)
        val targetIcon: ImageView by bindView(R.id.target_icon)
        val themeIcon: ImageView by bindView(R.id.theme_icon)
        val targetName: TextView by bindView(R.id.target_name)
        val reorder: ImageView by bindView(R.id.reorder)
        val type1a: TextView by bindView(R.id.theme_type1a)
        val type1b: TextView by bindView(R.id.theme_type1b)
        val type1c: TextView by bindView(R.id.theme_type1c)
        val type2: TextView by bindView(R.id.theme_type2)
        val type3: TextView by bindView(R.id.theme_type3)
    }

}