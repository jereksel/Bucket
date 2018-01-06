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

package com.jereksel.libresubstratum.activities.detailed

import com.github.kittinunf.result.Result
import com.google.common.collect.ArrayListMultimap
import com.jereksel.libresubstratum.activities.detailed.DetailedContract.Presenter
import com.jereksel.libresubstratum.activities.detailed.DetailedContract.View
import com.jereksel.libresubstratum.adapters.ThemePackAdapterView
import com.jereksel.libresubstratum.data.Type1ExtensionToString
import com.jereksel.libresubstratum.data.Type2ExtensionToString
import com.jereksel.libresubstratum.domain.*
import com.jereksel.libresubstratum.domain.usecases.ICompileThemeUseCase
import com.jereksel.libresubstratum.domain.usecases.IGetThemeInfoUseCase
import com.jereksel.libresubstratum.extensions.getLogger
import com.jereksel.libresubstratum.utils.ThemeNameUtils
import com.jereksel.libresubstratumlib.ThemePack
import com.jereksel.libresubstratumlib.Type3Extension
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.guava.await
import java.io.File

class DetailedPresenter(
        private val packageManager: IPackageManager,
        private val getThemeInfoUseCase: IGetThemeInfoUseCase,
        private val overlayService: OverlayService,
        private val activityProxy: IActivityProxy,
        private val compileThemeUseCase: ICompileThemeUseCase,
        private val clipboardManager: ClipboardManager,
        private val metrics: Metrics
) : Presenter() {

    val detailedView: View?
        get() = view.get()

    lateinit var themePackState: List<ThemePackAdapterState>
    lateinit var themePack: ThemePack
    lateinit var appId: String
    val log = getLogger()

    private var type3: Type3Extension? = null

    var init = false

    override fun readTheme(appId: String) {

        log.debug("Reading {}", appId)

        val extractLocation = File(packageManager.getCacheFolder(), appId)

        if (!extractLocation.exists()) {
            extractLocation.mkdirs()
        }

        if (init) {
            detailedView?.addThemes(themePack)
            return
        }
        init = true

        this.appId = appId

        Observable.fromCallable { getThemeInfoUseCase.getThemeInfo(appId) }
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .map {
                    //Remove apps that are not installed
                    val installedApps = it.themes.filter { packageManager.isPackageInstalled(it.application) }
                    ThemePack(installedApps, it.type3)
                }
                .map {
                    themePack = ThemePack(it.themes.sortedBy { packageManager.getAppName(it.application) }, it.type3)
                    themePackState = themePack.themes.map { ThemePackAdapterState() }
                    it
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { detailedView?.addThemes(it) }
    }

    override fun getNumberOfThemes() = themePack.themes.size

    //So we won't be stuck in refresh loop
    val seq = mutableSetOf<Int>()

    val adapterJobs = ArrayListMultimap.create<ThemePackAdapterView, Job>()

    override fun setAdapterView(position: Int, view: ThemePackAdapterView) {

        adapterJobs.get(view).forEach { it.cancel() }
        adapterJobs.removeAll(view)

        if (compiling) {
            return
        }

        val theme = themePack.themes[position]
        val state = themePackState[position]

        view.reset()

//        if (!seq.contains(position)) {

        view.setAppIcon(packageManager.getAppIcon(theme.application))
        view.setAppName(packageManager.getAppName(theme.application))
        view.setAppId(theme.application)
        view.setCheckbox(state.checked)

        view.type1aSpinner((theme.type1.find { it.suffix == "a" }?.extension ?: listOf()).map(::Type1ExtensionToString), state.type1a)
        view.type1bSpinner((theme.type1.find { it.suffix == "b" }?.extension ?: listOf()).map(::Type1ExtensionToString), state.type1b)
        view.type1cSpinner((theme.type1.find { it.suffix == "c" }?.extension ?: listOf()).map(::Type1ExtensionToString), state.type1c)
        view.type2Spinner((theme.type2?.extensions ?: listOf()).map(::Type2ExtensionToString), state.type2)

//        } else {
//            seq.remove(position)
//        }

        val overlay = getOverlayIdForTheme(position)
        val isOverlayInstalled = packageManager.isPackageInstalled(overlay)

        if (isOverlayInstalled) {
            val overlayVersionInfo = packageManager.getAppVersion(overlay)
            val themeVersionInfo = packageManager.getAppVersion(appId)

            if (overlayVersionInfo.first != themeVersionInfo.first) {
                //Overlay can be updated
                view.setInstalled(overlayVersionInfo.second, themeVersionInfo.second)
            } else {
                view.setInstalled(null, null)
            }


            val job = async(UI) {

                val overlayInfo = overlayService.getOverlayInfo(overlay).await()

                if (overlayInfo != null) {
                    view.setEnabled(overlayInfo.enabled)
                } else {
                    view.setEnabled(false)
                }

            }

            adapterJobs.put(view, job)
        }

        view.setCompiling(state.compiling)
    }

    //TODO: Change name
    fun areVersionsTheSame(app1: String, app2: String): Boolean {

        log.debug("areVersionTheSame", "$app1 vs $app2")

        val app1Info = packageManager.getAppVersion(app1)
        val app2Info = packageManager.getAppVersion(app2)

        return app1Info.first == app2Info.first
    }

    fun getOverlayIdForTheme(position: Int): String {

        log.debug("Getting overlayId for theme {} and types3 {} for position {} state overlay {} state of type3 {}", themePack.themes[position], themePack.type3, position, themePackState[position], type3)

        val theme = themePack.themes[position]
        val state = themePackState[position]

        val destAppId = theme.application

        val themeName = packageManager.getInstalledTheme(appId).name

        val type1a = theme.type1.find {it.suffix == "a"}?.extension?.getOrNull(state.type1a)
        val type1b = theme.type1.find {it.suffix == "b"}?.extension?.getOrNull(state.type1b)
        val type1c = theme.type1.find {it.suffix == "c"}?.extension?.getOrNull(state.type1c)
        val type2 = theme.type2?.extensions?.getOrNull(state.type2)
        val type3 = type3

        val id = ThemeNameUtils.getTargetOverlayName(
                destAppId,
                themeName,
                type1a,
                type1b,
                type1c,
                type2,
                type3
        )

        log.debug("OverlayId: {}", id)

        return id
    }

    override fun getAppName(appId: String) = packageManager.getAppName(appId)

    override fun setCheckbox(position: Int, checked: Boolean) {
        themePackState[position].checked = checked
    }

    override fun setType1a(position: Int, spinnerPosition: Int) {
        themePackState[position].type1a = spinnerPosition
        seq.add(position)
        detailedView?.refreshHolder(position)
    }

    override fun setType1b(position: Int, spinnerPosition: Int) {
        themePackState[position].type1b = spinnerPosition
        seq.add(position)
        detailedView?.refreshHolder(position)
    }

    override fun setType1c(position: Int, spinnerPosition: Int) {
        themePackState[position].type1c = spinnerPosition
        seq.add(position)
        detailedView?.refreshHolder(position)
    }

    override fun setType2(position: Int, spinnerPosition: Int) {
        themePackState[position].type2 = spinnerPosition
        seq.add(position)
        detailedView?.refreshHolder(position)
    }

    override fun setType3(type3Extension: Type3Extension) {
        type3 = type3Extension
    }

    override fun openInSplit(adapterPosition: Int) {
        val app = themePack.themes[adapterPosition].application
        log.debug("Opening {} in split", app)
        if (!activityProxy.openActivityInSplit(app)) {
            detailedView?.showToast("App couldn't be opened")
        }
    }

    override fun setClipboard(s: String) = clipboardManager.addToClipboard(s)

    var compiling = false

    override fun compileRunSelected() {

        val themesToCompile = themePackState.mapIndexed { index, themePackAdapterState -> index to themePackAdapterState }
                .filter { it.second.checked }

        deselectAll()

        detailedView?.showCompilationProgress(themesToCompile.size)

        compilePositions(themesToCompile.map { it.first }, {
            themePackState[it].compiling = false
            detailedView?.increaseDialogProgress()
        }, {
            detailedView?.hideCompilationProgress()
        }
        )
    }

    override fun compileRunActivateSelected() {

        val themesToCompile = themePackState.mapIndexed { index, themePackAdapterState -> index to themePackAdapterState }
                .filter { it.second.checked }

        deselectAll()

        detailedView?.showCompilationProgress(themesToCompile.size)

        compilePositions(themesToCompile.map { it.first },
                { position ->
                    themePackState[position].compiling = false
                    val overlayId = getOverlayIdForTheme(position)
                    if (packageManager.isPackageInstalled(overlayId)) {
                        overlayService.enableExclusive(getOverlayIdForTheme(position)).get()
                    }
                    detailedView?.increaseDialogProgress()
                },
                {
                    detailedView?.hideCompilationProgress()
                }
        )
    }

    override fun compileAndRun(adapterPosition: Int) {

        compilePositions(listOf(adapterPosition), { position ->
            themePackState[position].compiling = false
            val overlayId = getOverlayIdForTheme(position)
            if (packageManager.isPackageInstalled(overlayId)) {
                toggle(adapterPosition)
            }
        })
    }

    override fun selectAll() {

        if (!this::themePackState.isInitialized) {
            return
        }

        themePackState.forEachIndexed { index, state ->
            if (!state.checked) {
                detailedView?.refreshHolder(index)
                state.checked = true
            }
        }
    }

    override fun deselectAll() {

        if (!this::themePackState.isInitialized) {
            return
        }

        themePackState.forEachIndexed { index, state ->
            if (state.checked) {
                detailedView?.refreshHolder(index)
                state.checked = false
            }
        }
    }

    override fun restartSystemUI() {
        log.debug("Resetting SystemUI")
        overlayService.restartSystemUI()
    }

    private fun compilePositions(positions: List<Int>, afterInstalling: (Int) -> Unit, onComplete: () -> Unit = {}): Disposable {

        positions.forEach { themePackState[it].compiling = true; detailedView?.refreshHolder(it) }

        log.debug("Compiling overlays for {}", positions.map { themePack.themes[it].application })

        val disp = positions.toList().toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .filter {
                    val overlay = getOverlayIdForTheme(it)

                    if (packageManager.isPackageInstalled(overlay) && areVersionsTheSame(overlay, appId)) {
                        log.debug("{} is installed and has the same version", overlay)
                        afterInstalling(it)
                        themePackState[it].compiling = false
                        detailedView?.refreshHolder(it)
                        false
                    } else {
                        true
                    }
                }
                .flatMap {
                    val adapterPosition = it

                    compileForPositionObservable(it)
                            .subscribeOn(Schedulers.computation())
                            .observeOn(Schedulers.computation())
                            .zipWith(listOf(it), { a,b -> Pair(Result.of { a },b) })
                            .onErrorReturn { t ->
                                log.warn("Overlay compilation failed", t)
                                Pair(Result.error(t as Exception), adapterPosition)
                            }
                }
                .map {

                    val file = it.first.component1()

                    themePackState[it.second].compiling = false

                    if (file != null) {

                        log.debug("Installing overlay {}", file)
                        overlayService.installApk(file).get()
                        log.debug("Installing overlay {} finished", file)
                        val overlay = getOverlayIdForTheme(it.second)

                        //Replacing substratum theme (the keys are different and overlay can't be just replaced)
                        if (packageManager.isPackageInstalled(overlay) && !areVersionsTheSame(overlay, appId)) {
                            overlayService.uninstallApk(overlay).get()
                            overlayService.installApk(file).get()
                        }

                        file.delete()

                    }

                    afterInstalling(it.second)
                    it
                }
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    val adapterPosition = it.second
                    val state = themePackState[adapterPosition]
                    state.compiling = false
                    detailedView?.refreshHolder(adapterPosition)
                    it
                }
                .doOnComplete {
                    onComplete.invoke()
                }
                .map { it.first }
                .filter { it.component2() != null }
                .map { it.component2()!! }
                .filter { it.message != null }
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ errors ->
                    if (errors.isNotEmpty()) {
                        detailedView?.showError(errors.map { it.message!! })
                        log.warn("Compilation error: {}", errors)
                    }
//                })
                }, { onError ->
                    //Something gone horribly wrong
                    log.error("Error: {}", onError)
                    detailedView?.showError(listOf(onError.localizedMessage))
                })

        compositeDisposable.add(disp)

        return disp

    }

    fun toggle(position: Int) {
        val theme = themePack.themes[position]
        val overlay = getOverlayIdForTheme(position)
        val info = overlayService.getOverlayInfo(overlay).get()
        if (info != null) {
            if (!info.enabled) {
                overlayService.enableExclusive(overlay).get()
            } else {
                metrics.userDisabledOverlay(overlay)
                overlayService.disableOverlay(overlay).get()
            }
        }
    }

    fun compileForPositionObservable(position: Int): Observable<File> {

        val state = themePackState[position]
        val theme = themePack.themes[position]

        val type1a = theme.type1.find {it.suffix == "a"}?.extension?.getOrNull(state.type1a)
        val type1b = theme.type1.find {it.suffix == "b"}?.extension?.getOrNull(state.type1b)
        val type1c = theme.type1.find {it.suffix == "c"}?.extension?.getOrNull(state.type1c)

        val type2 = theme.type2?.extensions?.getOrNull(state.type2)

        return compileThemeUseCase.execute(
                themePack,
                appId,
                theme.application,
                type1a?.name,
                type1b?.name,
                type1c?.name,
                type2?.name,
                type3?.name
        )

    }

    data class ThemePackAdapterState(
            var checked: Boolean = false,
            var compiling: Boolean = false,
            var type1a: Int = 0,
            var type1b: Int = 0,
            var type1c: Int = 0,
            var type2: Int = 0
    )

}
