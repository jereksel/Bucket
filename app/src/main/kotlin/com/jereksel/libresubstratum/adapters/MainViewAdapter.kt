package com.jereksel.libresubstratum.adapters

import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import kotterknife.bindView
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.data.MainViewTheme
import com.squareup.picasso.Picasso
import rx.subjects.PublishSubject

class MainViewAdapter(val apps: List<MainViewTheme>) : RecyclerView.Adapter<MainViewAdapter.ViewHolder>() {

    private val onClickSubject = PublishSubject.create<MainViewTheme>()!!

    val cache = BitmapLruCache(apps)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = apps[position]
        holder.appName.text = app.name
        if (app.heroImage != null) {
            Picasso.with(holder.view.context).load(app.heroImage).noFade().fit().centerCrop().into(holder.heroImage)
        } else {
            holder.heroImage.setImageDrawable(ColorDrawable(android.R.color.black))
        }
        holder.view.setOnClickListener { onClickSubject.onNext(app) }
        holder.lock.visibility = if (app.isEncrypted) View.VISIBLE else View.GONE
        holder.lock.setOnClickListener {
            Toast.makeText(it.context, "Theme is encrypted. Ask themer to also include unencrypted files.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_main, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = apps.size

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        val appName: TextView by bindView(R.id.textView)
        val heroImage: ImageView by bindView(R.id.heroimage)
        val lock: ImageView by bindView(R.id.lock)
    }

    fun getClickObservable() = onClickSubject.asObservable()!!

}
