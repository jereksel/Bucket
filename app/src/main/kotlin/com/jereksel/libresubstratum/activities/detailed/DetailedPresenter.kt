package com.jereksel.libresubstratum.activities.detailed

import android.os.AsyncTask
import android.util.Log
import com.github.kittinunf.result.Result
import com.jereksel.libresubstratum.activities.detailed.DetailedContract.Presenter
import com.jereksel.libresubstratum.activities.detailed.DetailedContract.View
import com.jereksel.libresubstratum.activities.detailed.DetailedPresenter.CompilationState.*
import com.jereksel.libresubstratum.adapters.ThemePackAdapterView
import com.jereksel.libresubstratum.data.KeyPair
import com.jereksel.libresubstratum.data.Type1ExtensionToString
import com.jereksel.libresubstratum.data.Type2ExtensionToString
import com.jereksel.libresubstratum.domain.*
import com.jereksel.libresubstratum.domain.usecases.ICompileThemeUseCase
import com.jereksel.libresubstratum.domain.usecases.IGetThemeInfoUseCase
import com.jereksel.libresubstratum.extensions.getFile
import com.jereksel.libresubstratum.extensions.getLogger
import com.jereksel.libresubstratum.utils.ThemeNameUtils
import com.jereksel.libresubstratumlib.ThemePack
import com.jereksel.libresubstratumlib.Type3Extension
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.exceptions.Exceptions
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.reactivestreams.Publisher
import java.io.File
import java.util.*

class DetailedPresenter(
        private val packageManager: IPackageManager,
        private val getThemeInfoUseCase: IGetThemeInfoUseCase,
        private val overlayService: OverlayService,
        private val activityProxy: IActivityProxy,
        private val compileThemeUseCase: ICompileThemeUseCase,
        private val clipboardManager: ClipboardManager,
        private val metrics: Metrics
) : Presenter {

    var detailedView: View? = null
    lateinit var themePackState: List<ThemePackAdapterState>
    lateinit var themePack: ThemePack
    lateinit var appId: String
    var compositeDisposable = CompositeDisposable()
    val log = getLogger()

    private var type3: Type3Extension? = null

    var init = false

    override fun setView(view: DetailedContract.View) {
        detailedView = view
    }

    override fun removeView() {
        detailedView = null

        compositeDisposable.clear()
        compositeDisposable = CompositeDisposable()
    }

    override fun readTheme(appId: String) {

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

    override fun setAdapterView(position: Int, view: ThemePackAdapterView) {

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
            val overlayInfo = overlayService.getOverlayInfo(overlay)

            if (overlayVersionInfo.first != themeVersionInfo.first) {
                //Overlay can be updated
                view.setInstalled(overlayVersionInfo.second, themeVersionInfo.second)
            } else {
                view.setInstalled(null, null)
            }

            if (overlayInfo != null) {
                view.setEnabled(overlayInfo.enabled)
            } else {
                view.setEnabled(false)
            }
        }

        view.setCompiling(state.compiling.text)
    }

    //TODO: Change name
    fun areVersionsTheSame(app1: String, app2: String): Boolean {

        Log.d("areVersionTheSame", "$app1 vs $app2")

        val app1Info = packageManager.getAppVersion(app1)
        val app2Info = packageManager.getAppVersion(app2)

        return app1Info.first == app2Info.first
    }

    fun getOverlayIdForTheme(position: Int): String {


        val theme = themePack.themes[position]
        val state = themePackState[position]

        val destAppId = theme.application

        val themeName = packageManager.getInstalledTheme(appId).name

        val type1a = theme.type1.find {it.suffix == "a"}?.extension?.getOrNull(state.type1a)
        val type1b = theme.type1.find {it.suffix == "b"}?.extension?.getOrNull(state.type1b)
        val type1c = theme.type1.find {it.suffix == "c"}?.extension?.getOrNull(state.type1c)
        val type2 = theme.type2?.extensions?.getOrNull(state.type2)
        val type3 = type3

        return ThemeNameUtils.getTargetOverlayName(
                destAppId,
                themeName,
                type1a,
                type1b,
                type1c,
                type2,
                type3
        )
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
                        activateExclusive(position)
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
        themePackState.forEachIndexed { index, state ->
            if (!state.checked) {
                detailedView?.refreshHolder(index)
                state.checked = true
            }
        }
    }

    override fun deselectAll() {
        themePackState.forEachIndexed { index, state ->
            if (state.checked) {
                detailedView?.refreshHolder(index)
                state.checked = false
            }
        }
    }

    override fun restartSystemUI() {
        Observable.just("")
                .observeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe {
                    overlayService.restartSystemUI()
                }
    }

    private fun compilePositions(positions: List<Int>, afterInstalling: (Int) -> Unit, onComplete: () -> Unit = {}): Disposable {

        positions.forEach { themePackState[it].compiling = WAITING_FOR_COMPILATION; detailedView?.refreshHolder(it) }

        val exceptionSubject = PublishSubject.create<Exception>()

        val notExistingDirectory = File("/")

        val exceptionDisp = exceptionSubject
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .toList()
                .subscribe { errors ->
                    if (errors.isNotEmpty()) {
                        detailedView?.showError(errors.map { (it.cause as Exception).message!! })
                        log.warn("Compilation error: {}", errors)
                    }
                }


        log.debug("COMPILATION STARTED")

        val disp = positions.toFlowable()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .filter {
                    val overlay = getOverlayIdForTheme(it)

                    if (packageManager.isPackageInstalled(overlay) && areVersionsTheSame(overlay, appId)) {
                        afterInstalling(it)
                        themePackState[it].compiling = NO_COMPILATION
                        detailedView?.refreshHolder(it)
                        false
                    } else {
                        true
                    }
                }
                .parallel()
                .runOn(Schedulers.computation())
                .flatMap {
                    themePackState[it].compiling = COMPILING
                    detailedView?.refreshHolder(it)
                    try {
                        val file = compileForPositionObservable(it).blockingGet();
                        themePackState[it].compiling = WAITING_FOR_INSTALLING
                        detailedView?.refreshHolder(it)
                        Flowable.just(Pair(it, file))
                    } catch (e: Exception) {
                        afterInstalling(it)
                        themePackState[it].compiling = NO_COMPILATION
                        detailedView?.refreshHolder(it)
                        exceptionSubject.onNext(e)
                        Flowable.just(Pair(it, notExistingDirectory))
                    }
                }
                .filter { it.second !== notExistingDirectory }
                .sequential()
//                .buffer(f)
//                .buffer(Runtime.getRuntime().availableProcessors())
                .buffer(1)
                .observeOn(Schedulers.io())
                .map { file ->
                    if (file.isEmpty()) {
                        Thread.sleep(100)
                        listOf()
                    } else {
                        log.debug("Installing overlay {}", file)
                        file.map { it.first }.forEach { themePackState[it].compiling = INSTALLING; detailedView?.refreshHolder(it) }
                        overlayService.installApk(file.map { it.second })
                        log.debug("Installing overlay {} finished", file)
                        file
                    }
                }
                .flatMap { Flowable.fromArray(*it.toTypedArray()) }
                .map {

                    val location = it.first
                    val file = it.second

                    val overlay = getOverlayIdForTheme(location)

                    //Replacing substratum theme (the keys are different and overlay can't be just replaced)
                    if (packageManager.isPackageInstalled(overlay) && !areVersionsTheSame(overlay, appId)) {
                        overlayService.uninstallApk(overlay)
                        overlayService.installApk(listOf(file))
                    }

                    file.delete()

                    afterInstalling(location)

                    themePackState[location].compiling = NO_COMPILATION
                    detailedView?.refreshHolder(location)

                    Unit

                }
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ _ ->
                    log.debug("COMPILATION ENDED")
                    onComplete()
                    exceptionSubject.onComplete()
                }, { e: Throwable ->
                    log.error("Error during processing", e)
                    log.debug("COMPILATION ENDED")
                    exceptionSubject.onNext(e as Exception)
                    exceptionSubject.onComplete()
                    onComplete()
                })

        compositeDisposable.add(exceptionDisp)
        compositeDisposable.add(disp)

        return disp

    }

    fun activateExclusive(position: Int) {
        val theme = themePack.themes[position]
        val overlay = getOverlayIdForTheme(position)
        val info = overlayService.getOverlayInfo(overlay)
        if (info?.enabled == false) {
            val overlays = overlayService.getAllOverlaysForApk(theme.application).filter { it.enabled }
            metrics.userEnabledOverlay(overlay)
            overlays.map { it.overlayId }.forEach { metrics.userDisabledOverlay(it) }
            overlayService.disableOverlays(overlays.map { it.overlayId })
            overlayService.enableOverlay(overlay)
        }
    }

    fun toggle(position: Int) {
        val theme = themePack.themes[position]
        val overlay = getOverlayIdForTheme(position)
        val info = overlayService.getOverlayInfo(overlay)
        if (info != null) {
            if (!info.enabled) {
                val overlays = overlayService.getAllOverlaysForApk(theme.application).filter { it.enabled }

                metrics.userEnabledOverlay(overlay)
                overlays.map { it.overlayId }.forEach { metrics.userDisabledOverlay(it) }

                overlayService.disableOverlays(overlays.map { it.overlayId })
                overlayService.enableOverlay(overlay)
            } else {
                metrics.userDisabledOverlay(overlay)
                overlayService.disableOverlay(overlay)
            }
        }
    }

    fun compileForPositionObservable(position: Int): Single<File> {

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
            var compiling: CompilationState = NO_COMPILATION,
            var type1a: Int = 0,
            var type1b: Int = 0,
            var type1c: Int = 0,
            var type2: Int = 0
    )

    enum class CompilationState(val text: String?) {
        NO_COMPILATION(null),
        WAITING_FOR_COMPILATION("Waiting for compilation"),
        COMPILING("Compiling"),
        WAITING_FOR_INSTALLING("Waiting for installation"),
        INSTALLING("Installing")
    }

}
