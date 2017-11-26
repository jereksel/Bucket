package com.jereksel.libresubstratum.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.activities.priorities.PrioritiesContract
import com.jereksel.libresubstratum.activities.priorities.PrioritiesContract.Presenter
import com.jereksel.libresubstratum.adapters.PrioritiesAdapter.ViewHolder
import com.jereksel.libresubstratum.data.InstalledTheme
import kotterknife.bindView

class PrioritiesAdapter(
        val presenter: Presenter,
        val apps: List<String>
): RecyclerView.Adapter<ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appId = apps[position]
        holder.imageView.setImageDrawable(presenter.getIcon(appId))
        holder.textView.text = appId
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_priorities, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = apps.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView by bindView(R.id.imageView)
        val textView: TextView by bindView(R.id.textView)
    }

}