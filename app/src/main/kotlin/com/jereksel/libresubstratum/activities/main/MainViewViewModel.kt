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

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.databinding.ObservableList
import com.jereksel.libresubstratum.domain.IKeyFinder
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.OverlayService
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class MainViewViewModel @Inject constructor(
        val packageManager: IPackageManager,
        @Named("logged") val overlayService: OverlayService,
        val keyFinder: IKeyFinder
): IMainViewViewModel() {

    sealed class Change {
        data class Key(val id: Int, val keyAvailable: Boolean) : Change()
        data class Image(val id: Int, val location: String) : Change()
    }

    val apps: ObservableList<MainViewModel> = ObservableArrayList()
    override fun getAppsObservable() = apps

    val swipeToRefresh = ObservableBoolean(true)
    override fun getSwipeToRefreshObservable() = swipeToRefresh

    val _dialogContent = MutableLiveData<String>()
    override fun getDialogContent() = _dialogContent

    @Volatile
    var initialized = false

    var compositeDisposable = CompositeDisposable()

    override fun init() {
        if (initialized) {
            return
        }

        initialized = true

        val subject = BehaviorSubject.create<Change>()

        compositeDisposable += subject
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe { change ->

                    when(change) {
                        is Change.Key -> {
                            val (id, keyAvailable) = change
                            val theme = apps[id]
                            val newTheme = theme.copy(keyAvailable = keyAvailable)
                            apps[id] = newTheme
                        }
                        is Change.Image -> {
                            val (id, image) = change
                            val theme = apps[id]
                            val newTheme = theme.copy(heroImage = image)
                            apps[id] = newTheme
                        }
                    }

                }

        compositeDisposable += Schedulers.io().scheduleDirect {

            val themes = packageManager.getInstalledThemes().sortedBy { it.name }

            val models = themes.map { MainViewModel(it.appId, it.name) }

            apps.addAll(models)

            swipeToRefresh.set(false)

            themes.forEachIndexed { index, installedTheme ->

                compositeDisposable += Schedulers.io().scheduleDirect {
                    installedTheme.heroImage.run()
                    try {
                        val image = installedTheme.heroImage.get()!!
                        subject.onNext(Change.Image(index, image.absolutePath))
                    } catch (ignored: InterruptedException) {
                    }
                }

                compositeDisposable += Schedulers.io().scheduleDirect {
                    val key = keyFinder.getKey(installedTheme.appId)
                    subject.onNext(Change.Key(index, key != null))
                }

            }

        }

    }

    override fun reset() {
        swipeToRefresh.set(true)
        apps.clear()
        compositeDisposable.clear()
        compositeDisposable = CompositeDisposable()
        initialized = false
        init()
    }

    override fun tickChecks() {

        Thread {

            while (true) {
                Thread.sleep(1000)
                _dialogContent.postValue(UUID.randomUUID().toString())
            }

        }.start()

    }

    override fun onCleared() {
        compositeDisposable.clear()
    }


}