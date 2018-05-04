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
import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.databinding.ObservableList
import com.jakewharton.rxrelay2.ReplayRelay
import com.jereksel.libresubstratum.data.SingleLiveEvent
import com.jereksel.libresubstratum.domain.IKeyFinder
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.OverlayService
import com.jereksel.libresubstratum.domain.usecases.CleanUnusedOverlays
import com.jereksel.libresubstratum.domain.usecases.ICleanUnusedOverlays
import com.jereksel.libresubstratum.extensions.getLogger
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.cancelAndJoin
import kotlinx.coroutines.experimental.guava.await
import kotlinx.coroutines.experimental.launch
import java.util.concurrent.ExecutorService
import javax.inject.Inject
import javax.inject.Named

class MainViewViewModel @Inject constructor(
        val packageManager: IPackageManager,
        @Named("logged") val overlayService: OverlayService,
        val keyFinder: IKeyFinder,
        val cleanUnusedOverlays: ICleanUnusedOverlays
): IMainViewViewModel() {

    val log = getLogger()

    private val apps: ObservableList<MainViewModel> = ObservableArrayList()
    override fun getAppsObservable() = apps

    private val swipeToRefresh = ObservableBoolean(true)
    override fun getSwipeToRefreshObservable() = swipeToRefresh

    private val _dialogContent = MutableLiveData<String>()
    override fun getDialogContent() = _dialogContent

    private val _permissionsToRequest = SingleLiveEvent<List<String>>()
    override fun getPermissions() = _permissionsToRequest

    private val _appToOpen = SingleLiveEvent<String>()
    override fun getAppToOpen() = _appToOpen

    @Volatile
    var initialized = false

    lateinit var job: Job

    override fun init() {
        if (initialized) {
            return
        }

        cleanUnusedOverlays.clean()

        initialized = true

        job = launch {

            val themes = packageManager.getInstalledThemes().sortedBy { it.name.toLowerCase() }

            val models = themes.map { MainViewModel(it.appId, it.name) }

            apps.addAll(models)

            swipeToRefresh.set(false)

            themes.forEachIndexed { index, installedTheme ->

                val key = keyFinder.getKey(installedTheme.appId)

                log.debug("Key for {}: {}", installedTheme.appId, key)

                val theme = apps[index]
                val newTheme = theme.copy(keyAvailable = key != null)
                apps[index] = newTheme

            }

            themes.forEachIndexed { index, _ ->

                val theme = apps[index]

                val image = packageManager.getHeroImage(theme.appId).await()

                val newTheme = theme.copy(heroImage = image?.absolutePath)
                apps[index] = newTheme


            }

        }
    }

    override fun reset() {
        initialized = false
        swipeToRefresh.set(true)
        apps.clear()
        job.cancel()
        init()
    }

    override suspend fun tickChecks() {
        val perms = overlayService.requiredPermissions()
        if (perms.isNotEmpty()) {
            _permissionsToRequest.postValue(perms)
            return
        }
        _dialogContent.postValue("Checking system...")
        val message = overlayService.additionalSteps().await()
        if (message != null) {
            _dialogContent.postValue(message)
            return
        }
        _dialogContent.postValue("")
    }

    override fun onCleared() {
        job.cancel()
    }

}