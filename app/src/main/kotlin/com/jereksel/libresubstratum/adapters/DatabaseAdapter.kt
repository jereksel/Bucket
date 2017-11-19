package com.jereksel.libresubstratum.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

class DatabaseAdapter(
        val themes: List<String>
): RecyclerView.Adapter<DatabaseAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount() = themes.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

}