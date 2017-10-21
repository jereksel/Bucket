package com.jereksel.libresubstratum.adapters

import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.data.libraries.Library
import kotterknife.bindView
import org.jetbrains.anko.find

class AboutLibrariesAdapter(
        private val libraries: List<Library>
) : Adapter<AboutLibrariesAdapter.ViewHolder>() {

    override fun getItemCount() = libraries.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_aboutlibrary, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lib = libraries[position]
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

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView by bindView(R.id.textView)
    }

}