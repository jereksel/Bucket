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

import com.jereksel.libresubstratum.activities.installed.InstalledContract.Presenter
import com.jereksel.libresubstratum.data.InstalledOverlay
import com.jereksel.libresubstratum.domain.IActivityProxy
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.Metrics
import com.jereksel.libresubstratum.domain.OverlayService
import com.jereksel.libresubstratum.extensions.getLogger
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.experimental.guava.await

class InstalledPresenter(
        val packageManager: IPackageManager,
        val overlayService: OverlayService,
        val activityProxy: IActivityProxy,
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

    suspend override fun toggleOverlay(overlayId: String, enabled: Boolean) {

        if (enabled) {
            overlayService.enableOverlay(overlayId).await()
        } else {
            overlayService.disableOverlay(overlayId).await()
        }

        view.get()?.refreshRecyclerView()
        if (overlayId.startsWith("com.android.systemui")) {
            view.get()?.showSnackBar("This change requires SystemUI restart", "Restart SystemUI", { overlayService.restartSystemUI() })
        }
    }

    private fun selectedOverlays() = (overlays ?: emptyList<InstalledOverlay>()).filter { overlay -> state?.get(overlay.overlayId) == true }

    override suspend fun uninstallSelected() {

        val toUninstall = selectedOverlays()

        for (pckg in toUninstall) {
            overlays?.remove(pckg)
            overlayService.uninstallApk(pckg.overlayId).await()
            val overlays = overlays
            if (overlays != null) {
                view.get()?.addOverlays(overlays)
            }
        }

    }

    override suspend fun enableSelected() {

        selectedOverlays()
                .map { it.overlayId }
                .filter { overlayService.getOverlayInfo(it)?.enabled == false }
                .forEach { overlayService.enableOverlay(it).await() }

        deselectAll()
        view.get()?.refreshRecyclerView()

    }

    override suspend fun disableSelected() {

        selectedOverlays()
                .map { it.overlayId }
                .filter { overlayService.getOverlayInfo(it)?.enabled == true }
                .forEach { overlayService.disableOverlay(it).await() }

        deselectAll()
        view.get()?.refreshRecyclerView()

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
        overlayService.restartSystemUI()
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