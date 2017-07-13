package com.jereksel.libresubstratum.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.bindView
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratumlib.ThemePack
import com.jereksel.libresubstratum.domain.IPackageManager

class ThemePackAdapter(themePack : ThemePack, val packageManager: IPackageManager) : RecyclerView.Adapter<ThemePackAdapter.ViewHolder>() {

    val themePack: ThemePack = ThemePack(themePack.themes.sortedBy { packageManager.getAppName(it.application) }, themePack.type3)

//    val onClickSubject = PublishSubject.create<DetailedApplication>()!!

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appId = themePack.themes[position].application
        holder.appName.text = packageManager.getAppName(appId)
        holder.appIcon.setImageDrawable(packageManager.getAppIcon(appId))
//        val element = apps[position]
//        holder.view.setOnClickListener { onClickSubject.onNext(element) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_detailed, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = themePack.themes.size

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        val appName: TextView by bindView(R.id.textView)
        val appIcon: ImageView by bindView(R.id.imageView)
    }

//    fun getClickObservable() = onClickSubject.asObservable()!!

}
