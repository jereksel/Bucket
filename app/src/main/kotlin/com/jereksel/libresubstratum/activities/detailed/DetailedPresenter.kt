package com.jereksel.libresubstratum.activities.detailed

import com.google.common.io.Files
import com.jereksel.libresubstratum.activities.detailed.DetailedContract.Presenter
import com.jereksel.libresubstratum.activities.detailed.DetailedContract.View
import com.jereksel.libresubstratum.adapters.ThemePackAdapterView
import com.jereksel.libresubstratum.data.Type1ExtensionToString
import com.jereksel.libresubstratum.data.Type2ExtensionToString
import com.jereksel.libresubstratum.domain.*
import com.jereksel.libresubstratum.extensions.getFile
import com.jereksel.libresubstratumlib.ThemePack
import com.jereksel.libresubstratumlib.ThemeToCompile
import com.jereksel.libresubstratumlib.Type1DataToCompile
import com.jereksel.libresubstratumlib.Type1Extension
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File
import java.lang.ref.WeakReference

class DetailedPresenter(
        val packageManager: IPackageManager,
        val themeReader: IThemeReader,
        val overlayService: OverlayService,
        val activityProxy: IActivityProxy,
        val themeCompiler: ThemeCompiler
) : Presenter {

    var detailedView: View? = null
    lateinit var themePackState: List<ThemePackAdapterState>
    lateinit var themePack: ThemePack
    lateinit var appId: String
    var init = false

    override fun setView(view: DetailedContract.View) {
        detailedView = view
    }

    override fun removeView() {
        detailedView = null
    }

    override fun readTheme(appId: String) {

        if (init) {
            detailedView?.addThemes(themePack)
            return
        }
        init = true

        this.appId = appId
        val location = File(File(packageManager.getCacheFolder(), appId), "assets")

        Observable.fromCallable { themeReader.readThemePack(location) }
                .observeOn(Schedulers.computation())
                .subscribeOn(Schedulers.computation())
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
        val theme = themePack.themes[position]
        val state = themePackState[position]

        if (!seq.contains(position)) {

            view.setAppIcon(packageManager.getAppIcon(theme.application))
            view.setAppName(packageManager.getAppName(theme.application))
            view.setAppId(theme.application)
            view.setCheckbox(state.checked)

            view.type1aSpinner((theme.type1.find { it.suffix == "a" }?.extension ?: listOf()).map(::Type1ExtensionToString), state.type1a)
            view.type1bSpinner((theme.type1.find { it.suffix == "b" }?.extension ?: listOf()).map(::Type1ExtensionToString), state.type1b)
            view.type1cSpinner((theme.type1.find { it.suffix == "c" }?.extension ?: listOf()).map(::Type1ExtensionToString), state.type1c)
            view.type2Spinner((theme.type2?.extensions ?: listOf()).map(::Type2ExtensionToString), state.type2)

        } else {
            seq.remove(position)
        }

        val overlay = getOverlayIdForTheme(position)

        view.setInstalled(packageManager.isPackageInstalled(overlay))
        view.setCompiling(state.compiling)
    }

    fun getOverlayIdForTheme(position: Int): String {

        val theme = themePack.themes[position]
        val state = themePackState[position]

        val type1a = theme.type1.find {it.suffix == "a"}?.extension?.getOrNull(state.type1a)
        val type1b = theme.type1.find {it.suffix == "b"}?.extension?.getOrNull(state.type1b)
        val type1c = theme.type1.find {it.suffix == "c"}?.extension?.getOrNull(state.type1c)
        val type2 = theme.type2?.extensions?.getOrNull(state.type2)

        val suffix = listOf(type1a, type1b, type1c).mapNotNull {
            if (it?.default?.not() ?: false) {
                it?.name?.replace(" ", "")?.replace("-", "")?.replace("_","")
            } else {
                null
            }
        }.joinToString(separator="")

        val themeName = packageManager.getAppName(appId)

        val overlay = "${theme.application}.$themeName" + if (suffix.isNotEmpty()) { ".$suffix" } else { "" } + if (type2?.default?.not() ?: false) { ".${type2!!.name.replace(" ", "").replace("-","").replace("_", "")}"  } else { "" }

        return overlay

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

    override fun compileAndRun(adapterPosition: Int) {

        val state = themePackState[adapterPosition]
        state.compiling = true

        detailedView?.refreshHolder(adapterPosition)

        Thread {
            compileAndRun1(adapterPosition)
        }.start()
    }

    fun compileAndRun1(adapterPosition: Int) {
        val overlay = getOverlayIdForTheme(adapterPosition)
        val position = adapterPosition
        val state = themePackState[position]
        val theme = themePack.themes[position]
        if (packageManager.isPackageInstalled(overlay)) {
            val info = overlayService.getOverlayInfo(overlay)
            val overlays = overlayService.getAllOverlaysForApk(theme.application).filter { it.enabled }
            overlays.forEach { overlayService.disableOverlay(it.overlayId) }
            overlayService.toggleOverlay(overlay, !info.enabled)
        } else {
            val location = getFile(packageManager.getCacheFolder(), appId, "assets", "overlays", themePack.themes[adapterPosition].application)
//            val location = File(File(packageManager.getCacheFolder(), appId), themePack.themes[adapterPosition].application)

            val type1a = theme.type1.find {it.suffix == "a"}?.extension?.getOrNull(state.type1a)
            val type1b = theme.type1.find {it.suffix == "b"}?.extension?.getOrNull(state.type1b)
            val type1c = theme.type1.find {it.suffix == "c"}?.extension?.getOrNull(state.type1c)

            val type1 = listOf(type1a, type1b, type1c).zip(listOf("a", "b", "c"))
                    .filter { it.first != null }
                    .map {
                        Type1DataToCompile(it.first!!, it.second)
                    }
            val type2 = theme.type2?.extensions?.getOrNull(state.type2)

            val themeToCompile = ThemeToCompile(overlay,appId,theme.application, type1, type2)

            val apk = themeCompiler.compileTheme(themeToCompile, location)

//            apk.setReadable(true, false)
//            apk.setExecutable(true, false)

            val overlays = overlayService.getAllOverlaysForApk(theme.application).filter { it.enabled }
            overlayService.installApk(apk)

//            Thread {
                overlayService.disableOverlays(overlays.map { it.overlayId })
//                overlays.forEach{overlayService.disableOverlay(it.overlayId)}
                overlayService.enableOverlay(overlay)
//            }.start()


//            activityProxy.showToast("Overlay is not installed")
        }

        state.compiling = false
        seq.add(position)
        detailedView?.refreshHolder(position)

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
