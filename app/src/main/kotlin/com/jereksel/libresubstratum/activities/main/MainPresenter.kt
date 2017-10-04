package com.jereksel.libresubstratum.activities.main

import com.jereksel.libresubstratum.data.MainViewTheme
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.IThemeReader
import com.jereksel.libresubstratum.domain.OverlayService
import com.jereksel.libresubstratum.extensions.safeDispose
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File

class MainPresenter(
        val packageManager: IPackageManager,
        val themeReader: IThemeReader,
        val overlayService: OverlayService
) : MainContract.Presenter {

    companion object {
        val SUBSTRATUM_LEGACY = "Substratum_Legacy"
        val SUBSTRATUM_NAME = "Substratum_Name"
        val SUBSTRATUM_AUTHOR = "Substratum_Author"
    }

    private var mainView: MainContract.View? = null
    private var subscription: Disposable? = null

    override fun getApplications() {

        subscription?.safeDispose()

        subscription = Observable.fromCallable { packageManager.getInstalledThemes() }
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .flatMapIterable { it }
                .map { Pair(packageManager.getAppLocation(it.appId), it) }
                .flatMap {
                    Observable.just(it)
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.io())
                            .map {
                                val location = it.first
                                val theme = it.second
                                val isEncrypted = themeReader.isThemeEncrypted(location)
                                MainViewTheme.fromInstalledTheme(theme, isEncrypted)
                            }
                }
                .toList()
                .flattenAsObservable { it }
                .sorted { t1, t2 -> compareValues(t1.name, t2.name) }
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { list ->
                    mainView?.addApplications(list)
                }
    }

    override fun setView(view: MainContract.View) {
        mainView = view
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

    override fun removeView() {
        mainView = null
        subscription?.safeDispose()
    }

    override fun openThemeScreen(appId: String) {

        val source = packageManager.getAppLocation(appId)
        val dest = File(packageManager.getCacheFolder(), appId)

        mainView?.openThemeFragment(appId)

//        Observable.fromCallable { source.extractZip(dest) }
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.computation())
//                .subscribe { mainView?.openThemeFragment(appId) }
//        extractSubs = Observable.fromCallable { source.extractZip(dest, { mainView?.setDialogProgress(it) }) }
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.computation())
//                .subscribe { mainView?.openThemeFragment(appId) }

    }
}
