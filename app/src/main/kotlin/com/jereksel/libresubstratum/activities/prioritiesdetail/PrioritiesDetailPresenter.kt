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

package com.jereksel.libresubstratum.activities.prioritiesdetail

import com.jereksel.libresubstratum.activities.prioritiesdetail.PrioritiesDetailContract.Presenter
import com.jereksel.libresubstratum.data.InstalledOverlay
import com.jereksel.libresubstratum.domain.IActivityProxy
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.OverlayService
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers

class PrioritiesDetailPresenter(
        val overlayService: OverlayService,
        val packageManager: IPackageManager,
        val activityProxy: IActivityProxy
): Presenter() {

    lateinit var overlays: List<InstalledOverlay>

    var fabShown = false

    override fun getOverlays(targetId: String) {

        fabShown = false

        Schedulers.io().scheduleDirect {
            val installedOverlays = packageManager.getInstalledOverlays()
            val installedOverlaysMap = installedOverlays.map { it.overlayId to it }.toMap()
            val priorities = overlayService.getOverlaysPrioritiesForTarget(targetId)
            val mapped = priorities.map { installedOverlaysMap[it.overlayId]!! }
            overlays = mapped

            AndroidSchedulers.mainThread().scheduleDirect {
                view.get()?.setOverlays(mapped)
            }
        }

    }

    override fun updateOverlays(overlays: List<InstalledOverlay>) {

        if (overlays == this.overlays && fabShown) {
            fabShown = false
            view.get()?.hideFab()
        } else if (overlays != this.overlays && !fabShown) {
            fabShown = true
            view.get()?.showFab()
        }

    }

    override fun updatePriorities(overlays: List<InstalledOverlay>) {

        compositeDisposable += Single.fromCallable { overlayService.updatePriorities(overlays.map { it.overlayId }) }
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { _ ->
                    //Copy list
                    this.overlays = overlays.toMutableList()
                    fabShown = false
                    view.get()?.hideFab()
                    view.get()?.notifyPrioritiesChanged()
                }

    }

    override fun toggleOverlay(overlayId: String, callback: () -> Unit) {
        val overlay = overlayService.getOverlayInfo(overlayId)!!

        Schedulers.io().scheduleDirect {

            if (overlay.enabled) {
                overlayService.disableOverlay(overlayId)
            } else {
                overlayService.enableOverlay(overlayId)
            }

            AndroidSchedulers.mainThread().scheduleDirect {
                callback()
            }

        }

    }

    override fun isEnabled(overlayId: String) = overlayService.getOverlayInfo(overlayId)?.enabled == true

    override fun openAppInSplit(targetId: String) {
        activityProxy.openActivityInSplit(targetId)
    }

}