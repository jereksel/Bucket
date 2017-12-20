/*
 * Copyright (C) 2017 Andrzej Ressel (jereksel@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.jereksel.libresubstratum.activities.main

import android.graphics.drawable.ColorDrawable
import android.support.v7.util.DiffUtil
import android.support.v7.util.DiffUtil.DiffResult
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.activities.main.MainContract.Presenter
import com.jereksel.libresubstratum.data.DiffCallBack
import com.jereksel.libresubstratum.data.InstalledTheme
import com.jereksel.libresubstratum.data.KeyPair
import com.jereksel.libresubstratum.extensions.getLogger
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.android.MainThreadDisposable.verifyMainThread
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotterknife.bindView
import org.jetbrains.anko.longToast
import java.io.File

class MainViewAdapter(
        val presenter: Presenter
) : RecyclerView.Adapter<MainViewAdapter.ViewHolder>() {

    var apps: List<MainViewModel> = listOf()

    val log = getLogger()

    private val onClickSubject = PublishSubject.create<String>()!!

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = apps[position]

        val key = app.keyAvailable

        holder.appName.text = app.appName
        if (app.heroImage != null) {
            Picasso.with(holder.view.context).load(File(app.heroImage)).noFade().fit().centerCrop().into(holder.heroImage)
        } else {
            holder.heroImage.setImageDrawable(ColorDrawable(android.R.color.black))
        }

        if (key == null) {
            holder.progressBar.visibility = VISIBLE
        } else {
            holder.progressBar.visibility = GONE

            if (!key) {
                holder.lock.visibility = VISIBLE
            } else {
                holder.lock.visibility = GONE
            }

        }

        holder.view.setOnClickListener {
            onClickSubject.onNext(app.appId)
        }

        holder.lock.setOnClickListener {
            it.context.longToast(R.string.unsupported_template_toast)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_main, parent, false)
        return ViewHolder(v)
    }

    fun updateItems(apps: List<MainViewModel>) {
        verifyMainThread();

        val oldList = this.apps
        val newList = apps

        MainViewModelDiffCallBack(oldList, newList)
                .calculate()
                .dispatchUpdatesTo(this)

        this.apps = apps
    }

    override fun getItemCount() = apps.size

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        val appName: TextView by bindView(R.id.textView)
        val heroImage: ImageView by bindView(R.id.heroimage)
        val lock: ImageView by bindView(R.id.lock)
        val progressBar: ProgressBar by bindView(R.id.progressBar)
    }

    fun getClickObservable() = onClickSubject

    private fun <E> DiffCallBack<E>.calculate(): DiffResult {
        return DiffUtil.calculateDiff(this)
    }

}
