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

package com.jereksel.libresubstratum.activities.themelist

import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.databinding.ObservableList
import com.jakewharton.rxrelay2.ReplayRelay
import com.jereksel.libresubstratum.data.SingleLiveEvent
import com.jereksel.libresubstratum.domain.IKeyFinder
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.OverlayService
import com.jereksel.libresubstratum.domain.usecases.ICleanUnusedOverlays
import com.jereksel.libresubstratum.extensions.getLogger
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.experimental.cancelAndJoin
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject
import javax.inject.Named

interface ThemeListViewViewModelProvider {
    fun getThemeListViewViewModel(): IThemeListViewViewModel
}

class ThemeListViewViewModel @Inject constructor(
        val packageManager: IPackageManager,
        @Named("logged") val overlayService: OverlayService,
        val keyFinder: IKeyFinder,
        val cleanUnusedOverlays: ICleanUnusedOverlays
): IThemeListViewViewModel() {

    sealed class Change {
        data class Key(val id: Int, val keyAvailable: Boolean) : Change()
        data class Image(val id: Int, val location: String) : Change()
    }

    val log = getLogger()

    private val apps: ObservableList<ThemeListViewModel> = ObservableArrayList()
    override fun getAppsObservable() = apps

    private val swipeToRefresh = ObservableBoolean(true)
    override fun getSwipeToRefreshObservable() = swipeToRefresh

    private val _appToOpen = SingleLiveEvent<String>()
    override fun getAppToOpen() = _appToOpen

    @Volatile
    var initialized = false

    var compositeDisposable = CompositeDisposable()

    override fun init() {
        if (initialized) {
            return
        }

        cleanUnusedOverlays.clean()

        initialized = true

        val subject = ReplayRelay.create<Change>()

        compositeDisposable += subject
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe { change ->

                    log.debug("Received change: {}", change)

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

            val themes = packageManager.getInstalledThemes().sortedBy { it.name.toLowerCase() }

            val models = themes.map { ThemeListViewModel(it.appId, it.name) }

            apps.addAll(models)

            swipeToRefresh.set(false)

            themes.forEachIndexed { index, installedTheme ->

                compositeDisposable += Schedulers.io().scheduleDirect {
                    installedTheme.heroImage.run()
                    try {
                        val image = installedTheme.heroImage.get()!!
                        subject.accept(Change.Image(index, image.absolutePath))
                    } catch (ignored: InterruptedException) {
                    }
                }

                compositeDisposable += Schedulers.io().scheduleDirect {
                    val key = keyFinder.getKey(installedTheme.appId)
                    log.debug("Key for {}: {}", installedTheme.appId, key)
                    subject.accept(Change.Key(index, key != null))
                }

            }

        }

    }

    override fun reset() {
        compositeDisposable.clear()
        compositeDisposable = CompositeDisposable()
        initialized = false
        swipeToRefresh.set(true)
        apps.clear()
        init()
    }

    override fun onCleared() {
        compositeDisposable.clear()
    }

}