package com.jereksel.libresubstratum.activities.main

import android.os.Bundle
import com.jereksel.libresubstratum.data.DetailedApplication
import com.jereksel.libresubstratum.domain.IPackageManager
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.filterNotNull
import rx.schedulers.Schedulers

class MainPresenter(val packageManager: IPackageManager) : IMainPresenter {

    companion object {
        val SUBSTRATUM_LEGACY = "Substratum_Legacy"
        val SUBSTRATUM_NAME = "Substratum_Name"
        val SUBSTRATUM_AUTHOR = "Substratum_Author"
    }

    private var mainView: IMainView? = null
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

    override fun setView(view: IMainView) {
        mainView = view
    }

    override fun removeView() {
        mainView = null
        if (subscription?.isUnsubscribed ?: false) {
           subscription?.unsubscribe()
        }
    }

    fun Bundle.has(key: String) = this.get(key) != null
}
