package com.jereksel.libresubstratum.adapters

import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.adapters.NavigationBarAdapter.ViewHolder
import com.jereksel.libresubstratum.data.NavigationBarOverlay
import kotterknife.bindView

class NavigationBarAdapter(
        val bars: List<NavigationBarOverlay>
): Adapter<ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bar = bars[position]
        holder.left.setImageBitmap(BitmapFactory.decodeByteArray(bar.left, 0, bar.left.size))
        holder.center.setImageBitmap(BitmapFactory.decodeByteArray(bar.center, 0, bar.center.size))
        holder.right.setImageBitmap(BitmapFactory.decodeByteArray(bar.right, 0, bar.right.size))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_navigationbar, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = bars.size

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        val left: ImageView by bindView(R.id.left)
        val center: ImageView by bindView(R.id.center)
        val right: ImageView by bindView(R.id.right)
    }
}