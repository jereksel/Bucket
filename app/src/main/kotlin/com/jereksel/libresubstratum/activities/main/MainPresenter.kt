package com.jereksel.libresubstratum.activities.main

import com.jereksel.libresubstratum.data.KeyPair
import com.jereksel.libresubstratum.domain.*
import com.jereksel.libresubstratum.extensions.getLogger
import com.jereksel.libresubstratum.extensions.safeDispose
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MainPresenter(
        val packageManager: IPackageManager,
        val themeReader: IThemeReader,
        val overlayService: OverlayService,
        val metrics: Metrics,
        val keyFinder: IKeyFinder
) : MainContract.Presenter {

    val log = getLogger()

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
                .toList()
                .flattenAsObservable { it }
                .sorted { t1, t2 -> String.CASE_INSENSITIVE_ORDER.compare(t1.name, t2.name)}
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

    override fun getKeyPair(appId: String) = keyFinder.getKey(appId)

    override fun removeView() {
        mainView = null
        subscription?.safeDispose()
    }

    override fun openThemeScreen(appId: String) {
        log.debug("Opening theme {}", appId)
        metrics.userEnteredTheme(appId)
        mainView?.openThemeFragment(appId)
    }
}
