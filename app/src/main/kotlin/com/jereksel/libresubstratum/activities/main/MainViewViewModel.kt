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
import io.reactivex.rxkotlin.toSingle
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Named

class MainViewViewModel @Inject constructor(
        val packageManager: IPackageManager,
        @Named("logged") val overlayService: OverlayService,
        val keyFinder: IKeyFinder
): ViewModel() {

    val apps: ObservableList<MainViewModel> = ObservableArrayList()

    val swipeToRefresh = ObservableBoolean(true)

    val executor = Executors.newSingleThreadExecutor()

    @Volatile
    var initialized = false

    val compositeDisposable = CompositeDisposable()

    fun init() {
        if (initialized) {
            return
        }

        initialized = true

        compositeDisposable += Schedulers.io().scheduleDirect {

            val themes = packageManager.getInstalledThemes().sortedBy { it.name }

            val models = themes.map { MainViewModel(it.appId, it.name) }

            apps.addAll(models)

            swipeToRefresh.set(false)

            themes.forEachIndexed {index, installedTheme ->

                Schedulers.io().scheduleDirect {

                    installedTheme.heroImage.run()

                    val image = installedTheme.heroImage.get()!!

                    executor.execute {

                        val theme = apps[index]

                        val newTheme = theme.copy(heroImage = image.absolutePath)

                        apps[index] = newTheme

                    }

                }


                Schedulers.io().scheduleDirect {

                    val key = keyFinder.getKey(installedTheme.appId)

                    executor.execute {

                        val theme = apps[index]

                        val newTheme = theme.copy(keyAvailable = key != null)

                        apps[index] = newTheme

                    }

                }


            }

        }



/*
        compositeDisposable += { packageManager.getInstalledThemes() }.toSingle()
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .flattenAsObservable { it }
                .map {
                    MainViewModel(it.appId, it.name)
                }
                .toList()
                .subscribe { models ->
                    swipeToRefresh.set(false)
                    apps.clear()
                    apps.addAll(models)
                }*/

    }

    override fun onCleared() {
        compositeDisposable.clear()
        executor.shutdownNow()
    }
}