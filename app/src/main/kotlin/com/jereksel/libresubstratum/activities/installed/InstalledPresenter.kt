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

package com.jereksel.libresubstratum.activities.installed

import com.github.kittinunf.result.Result
import com.jereksel.libresubstratum.activities.installed.InstalledContract.Presenter
import com.jereksel.libresubstratum.data.InstalledOverlay
import com.jereksel.libresubstratum.domain.ActivityProxy
import com.jereksel.libresubstratum.domain.PackageManager
import com.jereksel.libresubstratum.domain.Metrics
import com.jereksel.libresubstratum.domain.OverlayService
import com.jereksel.libresubstratum.extensions.getLogger
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.toObservable
import io.reactivex.rxkotlin.toSingle
import io.reactivex.schedulers.Schedulers

class InstalledPresenter(
        val packageManager: PackageManager,
        val overlayService: OverlayService,
        val activityProxy: ActivityProxy,
        val metrics: Metrics
) : Presenter() {

    val log = getLogger()

    private var subscription: Disposable? = null
    private var overlays: MutableList<InstalledOverlay>? = null
    private var filter = ""

    private var state: MutableMap<String, Boolean>? = null

    override fun getInstalledOverlays() {

        val o = overlays

        if (o != null) {
            view.get()?.addOverlays(o)
            return
        }

        subscription = Observable.fromCallable { packageManager.getInstalledOverlays() }
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .map {
                    it.sortedWith(compareBy({ it.sourceThemeName.toLowerCase() }, { it.targetName.toLowerCase() }, { it.type1a },
                            { it.type1b }, { it.type1c }, { it.type2 }, { it.type3 }))
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    overlays = it.toMutableList()
                    state = it.map { Pair(it.overlayId, false) }.toMap().toMutableMap()
                    view.get()?.addOverlays(getFilteredApps())
                }
    }

    override fun toggleOverlay(overlayId: String, enabled: Boolean) {

        val single = {
            if (enabled) {
                overlayService.enableOverlay(overlayId)
            } else {
                overlayService.disableOverlay(overlayId)
            }
        }.toSingle()

        single
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())
                .subscribe { _ ->
                    view.get()?.refreshRecyclerView()
                    if (overlayId.startsWith("com.android.systemui")) {
                        view.get()?.showSnackBar("This change requires SystemUI restart", "Restart SystemUI", { overlayService.restartSystemUI() })
                    }
                }

    }

    private fun selectedOverlays() = (overlays ?: emptyList<InstalledOverlay>()).filter { overlay -> state?.get(overlay.overlayId) == true }

    override fun uninstallSelected() {

        val toUninstall = selectedOverlays()

        val disp = toUninstall.toObservable()
                .observeOn(Schedulers.computation())
                .subscribeOn(Schedulers.computation())
                .map {
                    overlays?.remove(it)
                    overlayService.uninstallApk(it.overlayId)
                    Result.of(overlays)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { overlaysResult ->
                    val overlays = overlaysResult.component1()
                    if (overlays != null) {
                        this.view.get()?.addOverlays(overlays)
                    }
                }

        compositeDisposable.add(disp)
    }

    override fun enableSelected() {

        val toEnable = selectedOverlays()
                .map { it.overlayId }


        val disp = toEnable.toObservable()
                .observeOn(Schedulers.computation())
                .subscribeOn(Schedulers.computation())
                .filter { overlayService.getOverlayInfo(it)?.enabled == false }
                .toList()
                .map {
                    it.forEach { overlayService.enableOverlay(it) }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { _ ->
                    deselectAll()
                    view.get()?.refreshRecyclerView()
                }

        compositeDisposable.add(disp)
    }

    override fun disableSelected() {

        val toDisable = selectedOverlays()
                .map { it.overlayId }

        val disp = toDisable.toObservable()
                .observeOn(Schedulers.computation())
                .subscribeOn(Schedulers.computation())
                .filter { overlayService.getOverlayInfo(it)?.enabled == true }
                .toList()
                .map {
                    it.forEach { overlayService.disableOverlay(it) }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { _ ->
                    deselectAll()
                    view.get()?.refreshRecyclerView()
                }

        compositeDisposable.add(disp)

    }

    override fun selectAll() {
        getFilteredApps().forEach { state?.set(it.overlayId, true) }
        view.get()?.refreshRecyclerView()
    }

    override fun deselectAll() {
        getFilteredApps().forEach { state?.set(it.overlayId, false) }
        view.get()?.refreshRecyclerView()
    }

    override fun getOverlayInfo(overlayId: String) = overlayService.getOverlayInfo(overlayId)

    override fun openActivity(appId: String) = activityProxy.openActivityInSplit(appId)

    override fun getState(overlayId: String) = state!![overlayId]!!

    override fun setState(overlayId: String, isEnabled: Boolean) {
        state?.set(overlayId, isEnabled)
    }

    override fun restartSystemUI() {
        compositeDisposable += Schedulers.io()
                .scheduleDirect {
                    overlayService.restartSystemUI()
                }
    }

    override fun setFilter(filter: String) {
        this.filter = filter
        view.get()?.updateOverlays(getFilteredApps())
    }

    fun getFilteredApps(): List<InstalledOverlay> {
        val overlays = overlays
        if (overlays == null) {
            return listOf()
        } else if (filter.isEmpty()) {
            return overlays
        } else {
            return overlays.filter {
                it.targetName.contains(filter, true) ||
                        it.sourceThemeName.contains(filter, true)
            }
        }
    }
}