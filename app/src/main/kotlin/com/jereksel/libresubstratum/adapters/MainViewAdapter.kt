package com.jereksel.libresubstratum.adapters

import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.data.MainViewTheme
import com.jereksel.libresubstratum.extensions.getLogger
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotterknife.bindView

class MainViewAdapter(val apps: List<MainViewTheme>) : RecyclerView.Adapter<MainViewAdapter.ViewHolder>() {

    val log = getLogger()

    init {

        apps.mapIndexed { index, mainViewTheme -> Pair(index, mainViewTheme.heroImage) }.toObservable()
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .map {
//                    log.debug("Image downloading: {}", it)
                    it
                }
                .flatMap {
                    val index = it.first
                    val future = it.second
                    Observable.fromCallable { future.run() }
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.io())
                            .map { index }
                 }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { index ->
                    notifyItemChanged(index)
                }

    }

    private val onClickSubject = PublishSubject.create<MainViewTheme>()!!

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = apps[position]
        holder.appName.text = app.name
        val heroImage = if (app.heroImage.isDone) { app.heroImage.get() } else { null }
        if (heroImage != null) {
            Picasso.with(holder.view.context).load(heroImage).noFade().fit().centerCrop().into(holder.heroImage)
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

    fun getClickObservable() = onClickSubject

}
