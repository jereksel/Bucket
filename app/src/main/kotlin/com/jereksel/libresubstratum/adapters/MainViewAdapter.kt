package com.jereksel.libresubstratum.adapters

import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.activities.main.MainContract
import com.jereksel.libresubstratum.activities.main.MainContract.Presenter
import com.jereksel.libresubstratum.data.InstalledTheme
import com.jereksel.libresubstratum.data.KeyPair
import com.jereksel.libresubstratum.domain.KeyFinder
import com.jereksel.libresubstratum.extensions.getLogger
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotterknife.bindView
import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast
import java.security.KeyFactory

class MainViewAdapter(
        val apps: List<InstalledTheme>,
        val presenter: Presenter
) : RecyclerView.Adapter<MainViewAdapter.ViewHolder>() {

    val keys = mutableMapOf<Int, KeyPair?>()

    val log = getLogger()

    init {

        apps.mapIndexed { index, mainViewTheme -> Pair(index, mainViewTheme.heroImage) }.toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
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
                    //FIXME
                    try {
                        notifyItemChanged(index)
                    } catch (ignored: IllegalStateException) {

                    }
                }

        Observable.fromArray(*apps.mapIndexed { index, _ -> index }.toTypedArray())
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
//                .map { it.appId }
//                .flatMap {
//                    Observable.just(it)
//                            .observeOn(Schedulers.io())
//                            .subscribeOn(Schedulers.io())
                .map {
                    Pair(it, presenter.getKeyPair(apps[it].appId))
                }
//                            .retryWhen(RetryWithDelay(Int.MAX_VALUE, 1000))
//                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    keys[it.first] = it.second
                    try {
                        notifyItemChanged(it.first)
                    } catch (ignored: IllegalStateException) {

                    }
                }

    }

    private val onClickSubject = PublishSubject.create<InstalledTheme>()!!

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = apps[position]
        val keyAvailable = keys.contains(position)
        val key = keys[position]

        holder.progressBar.visibility = GONE
        holder.lock.visibility = GONE

        holder.appName.text = app.name
        val heroImage = if (app.heroImage.isDone) { app.heroImage.get() } else { null }
        if (heroImage != null) {
            Picasso.with(holder.view.context).load(heroImage).noFade().fit().centerCrop().into(holder.heroImage)
        } else {
            holder.heroImage.setImageDrawable(ColorDrawable(android.R.color.black))
        }

        if (!keyAvailable) {
            holder.progressBar.visibility = VISIBLE
        } else {
            holder.progressBar.visibility = GONE

            if (key == null) {
                holder.lock.visibility = VISIBLE
            } else {
                holder.lock.visibility = GONE
            }

        }

        holder.view.setOnClickListener { onClickSubject.onNext(app) }
//        holder.lock.visibility = if (app.isEncrypted) View.VISIBLE else View.GONE
        holder.lock.setOnClickListener {
//            onClickSubject.onNext(app)
//            val keys = KeyFinder(holder.lock.context).getKey(app.appId)
//            it.context.toast(keys.toString())
            it.context.longToast("Theme is encrypted. Ask themer to also include unencrypted files.")
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
        val progressBar: ProgressBar by bindView(R.id.progressBar)
    }

    fun getClickObservable() = onClickSubject

}
