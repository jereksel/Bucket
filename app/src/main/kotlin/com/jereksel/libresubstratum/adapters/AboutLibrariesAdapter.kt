package com.jereksel.libresubstratum.adapters

import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.data.libraries.Library
import kellinwood.security.zipsigner.HexDumpEncoder
import kotterknife.bindView
import org.jetbrains.anko.find

class AboutLibrariesAdapter(
        private val libraries: List<Library>
) : Adapter<ViewHolder>() {

    private val TYPE_HEADER = 0
    private val TYPE_ITEM = 1

    override fun getItemCount() = libraries.size

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return TYPE_HEADER
        } else {
            return TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View

        @Suppress("LiftReturnOrAssignment")
        if (viewType == TYPE_HEADER) {
            v = LayoutInflater.from(parent.context).inflate(R.layout.item_aboutlibrary_header, parent, false)
            return HeaderHolder(v)
        } else {
            v = LayoutInflater.from(parent.context).inflate(R.layout.item_aboutlibrary, parent, false)
            return ViewHolder(v)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is HeaderHolder) {
            holder.textView.setText(R.string.libraries)
        } else if (holder is ViewHolder) {

            val lib = getItem(position)
            holder.textView.text = "${lib.name} by ${lib.authors.joinToString(separator = " ") { it.name }}".trim()
            holder.itemView.setOnClickListener {

                val context = it.context

                val inflater = LayoutInflater.from(context)
                val view = inflater.inflate(R.layout.dialog_librarylicense, null)

                val textview = view.find<TextView>(R.id.license)
                textview.text = context.getString(lib.license.stringId)
                val alertDialog = AlertDialog.Builder(context)
                alertDialog.setTitle(R.string.license)
                alertDialog.setView(view)
                val alert = alertDialog.create()
                alert.show()

            }
        }
    }

    fun getItem(i: Int) = libraries[i-1]

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView by bindView(R.id.textView)
    }

    class HeaderHolder(view: View): RecyclerView.ViewHolder(view) {
        val textView: TextView by bindView(R.id.textView)
    }

}