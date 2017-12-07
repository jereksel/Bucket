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

import com.jereksel.libresubstratum.activities.main.MainContract.Presenter
import com.jereksel.libresubstratum.data.KeyPair
import com.jereksel.libresubstratum.domain.*
import com.jereksel.libresubstratum.extensions.getLogger
import com.jereksel.libresubstratum.extensions.safeDispose
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers

class MainPresenter(
        val packageManager: IPackageManager,
        val themeReader: IThemeReader,
        val overlayService: OverlayService,
        val metrics: Metrics,
        val keyFinder: IKeyFinder
) : Presenter() {

    val log = getLogger()

    companion object {
        val SUBSTRATUM_LEGACY = "Substratum_Legacy"
        val SUBSTRATUM_NAME = "Substratum_Name"
        val SUBSTRATUM_AUTHOR = "Substratum_Author"
    }

    private val mainView: MainContract.View?
    get() = view.get()

    override fun getApplications() {

        compositeDisposable += Observable.fromCallable { packageManager.getInstalledThemes() }
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .flatMapIterable { it }
                .toList()
                .flattenAsObservable { it }
                .sorted { t1, t2 -> String.CASE_INSENSITIVE_ORDER.compare(t1.name, t2.name)}
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { list ->
                    mainView?.addApplications(list)
                }
    }

    override fun checkPermissions() {
        val perms = overlayService.requiredPermissions()
        if (perms.isNotEmpty()) {
            mainView?.requestPermissions(perms)
            return
        }
        mainView?.dismissDialog()
        val message = overlayService.additionalSteps()
        if (message != null) {
            mainView?.showUndismissableDialog(message)
            return
        }
    }

    override fun getKeyPair(appId: String) = keyFinder.getKey(appId)

    override fun openThemeScreen(appId: String) {
        log.debug("Opening theme {}", appId)
        metrics.userEnteredTheme(appId)
        mainView?.openThemeFragment(appId)
    }
}
