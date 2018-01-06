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

package com.jereksel.libresubstratum.activities.priorities

import com.google.common.collect.ArrayListMultimap
import com.jereksel.libresubstratum.activities.priorities.PrioritiesContract.Presenter
import com.jereksel.libresubstratum.activities.priorities.PrioritiesContract.View
import com.jereksel.libresubstratum.data.InstalledOverlay
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.OverlayService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import java.lang.ref.WeakReference

class PrioritiesPresenter(
        val overlayService: OverlayService,
        val packageManager: IPackageManager
): Presenter() {

    override fun getApplication() {

        compositeDisposable += Observable.fromCallable { packageManager.getInstalledOverlays() }
                .observeOn(Schedulers.computation())
                .subscribeOn(Schedulers.computation())
                .flatMap {

                    val multiMap = ArrayListMultimap.create<String, InstalledOverlay>()

                    it.forEach {
                        multiMap.put(it.targetId, it)
                    }

                    multiMap.asMap().entries.toObservable()

                }
                .filter { it.value.size > 1 }
                .map { it.key to it.value.map { overlayService.getOverlayInfo(it.overlayId).get()?.enabled == true } }
                .map { it.first to it.second.filter { it } }
                .filter { it.second.size > 1 }
                .map { it.first }
//                .map { packageManager.getInstalledTheme(it) }
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { themes ->
                    view.get()?.addApplications(themes)
                }

    }

    override fun getIcon(appId: String) = packageManager.getAppIcon(appId)
}