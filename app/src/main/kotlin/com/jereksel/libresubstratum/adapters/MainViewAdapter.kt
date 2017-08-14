package com.jereksel.libresubstratum.adapters

import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.bindView
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.data.DetailedApplication
import com.jereksel.libresubstratum.data.InstalledTheme
import rx.subjects.PublishSubject

class MainViewAdapter(val apps: List<InstalledTheme>) : RecyclerView.Adapter<MainViewAdapter.ViewHolder>() {

    val onClickSubject = PublishSubject.create<InstalledTheme>()!!

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.appName.text = apps[position].name
        holder.heroImage.setImageDrawable(apps[position].heroImage ?: ColorDrawable(android.R.color.black))
        val element = apps[position]
        holder.view.setOnClickListener { onClickSubject.onNext(element) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_main, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = apps.size

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        val appName: TextView by bindView(R.id.textView)
        val heroImage: ImageView by bindView(R.id.heroimage)
    }

    fun getClickObservable() = onClickSubject.asObservable()!!

}
