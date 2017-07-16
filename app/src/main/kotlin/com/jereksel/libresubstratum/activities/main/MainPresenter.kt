package com.jereksel.libresubstratum.activities.main

import android.util.Log
import com.jereksel.libresubstratum.data.DetailedApplication
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.extensions.has
import com.jereksel.libresubstratum.utils.ZipUtils.extractZip
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class MainPresenter(val packageManager: IPackageManager) : MainContract.Presenter {

    companion object {
        val SUBSTRATUM_LEGACY = "Substratum_Legacy"
        val SUBSTRATUM_NAME = "Substratum_Name"
        val SUBSTRATUM_AUTHOR = "Substratum_Author"
    }

    private var mainView: MainContract.View? = null
    private var subscription: Subscription? = null

    override fun getApplications() {

        subscription = Observable.fromCallable {packageManager.getApplications()}
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())
                .flatMapIterable { it }
                .filter { it.metadata.has(SUBSTRATUM_LEGACY) }
                .filter { it.metadata.has(SUBSTRATUM_AUTHOR) }
                .filter { it.metadata.has(SUBSTRATUM_NAME) }
                .map {
                    DetailedApplication(it.appId, it.metadata.getString(SUBSTRATUM_NAME),
                            it.metadata.getString(SUBSTRATUM_AUTHOR), packageManager.getHeroImage(it.appId))
                }
                .toList()
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
