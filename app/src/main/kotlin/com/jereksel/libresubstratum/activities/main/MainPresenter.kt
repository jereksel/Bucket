package com.jereksel.libresubstratum.activities.main

import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.extensions.safeUnsubscribe
import com.jereksel.libresubstratum.utils.ZipUtils.extractZip
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File

class MainPresenter(val packageManager: IPackageManager) : MainContract.Presenter {

    companion object {
        val SUBSTRATUM_LEGACY = "Substratum_Legacy"
        val SUBSTRATUM_NAME = "Substratum_Name"
        val SUBSTRATUM_AUTHOR = "Substratum_Author"
    }

    private var mainView: MainContract.View? = null
    private var subscription: Subscription? = null

    override fun getApplications() {

        subscription?.safeUnsubscribe()

        subscription = Observable.fromCallable { packageManager.getInstalledThemes() }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { mainView?.addApplications(it) }
    }

    override fun setView(view: MainContract.View) {
        mainView = view
    }

    override fun removeView() {
        mainView = null
        if (subscription?.isUnsubscribed ?: false) {
           subscription?.unsubscribe()
        }
    }

    override fun openThemeScreen(appId: String) {

        val source = packageManager.getAppLocation(appId)
        val dest = File(packageManager.getCacheFolder(), appId)

        Observable.fromCallable { source.extractZip(dest) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())
                .subscribe { mainView?.openThemeFragment(appId) }

    }

}
