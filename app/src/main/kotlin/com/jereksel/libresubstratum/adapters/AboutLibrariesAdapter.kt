package com.jereksel.libresubstratum.adapters

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.data.libraries.Library
import kotlinx.android.synthetic.main.item_aboutlibrary.*
import kotterknife.bindView
import kotlin.reflect.full.createInstance

class AboutLibrariesAdapter(
        private vararg val libraries: Library
) : Adapter<AboutLibrariesAdapter.ViewHolder>() {

    override fun getItemCount() = libraries.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_aboutlibrary, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView by bindView(R.id.textView)
    }

}