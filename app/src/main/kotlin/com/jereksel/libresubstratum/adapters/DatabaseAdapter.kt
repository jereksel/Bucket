package com.jereksel.libresubstratum.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.domain.SubstratumDatabaseTheme
import com.squareup.picasso.Picasso
import kotterknife.bindView

class DatabaseAdapter(
        val themes: List<SubstratumDatabaseTheme>
): RecyclerView.Adapter<DatabaseAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_database, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val theme = themes[position]
        holder.overlayName.text = theme.name
        holder.overlayAuthor.text = theme.author
        val banner = holder.banner
        val icon = holder.icon

//        Glide.with()

        Picasso.with(holder.itemView.context)
                .load(theme.backgroundImage)
                .fit()
                .centerCrop()
                .into(holder.banner)

        Picasso.with(holder.itemView.context)
                .load(theme.image)
                .fit()
                .into(icon)

    }

    override fun getItemCount() = themes.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView by bindView(R.id.icon)
        val overlayName: TextView by bindView(R.id.overlayName)
        val overlayAuthor: TextView by bindView(R.id.overlayAuthor)
        val banner: ImageView by bindView(R.id.banner)
    }

}