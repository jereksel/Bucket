package com.jereksel.libresubstratum.activities.installed

import com.jereksel.libresubstratum.activities.installed.InstalledContract.Presenter
import com.jereksel.libresubstratum.activities.installed.InstalledContract.View
import com.jereksel.libresubstratum.domain.IPackageManager
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.lang.ref.WeakReference

class InstalledPresenter(val packageManager: IPackageManager): Presenter {

    private var view = WeakReference<View>(null)
    private var subscription: Subscription? = null

    override fun setView(view: View) {
        this.view = WeakReference(view)
    }

    override fun getInstalledOverlays() {

        subscription = Observable.fromCallable { packageManager.getInstalledOverlays() }
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .map {
                    it.sortedWith(compareBy({it.targetName}, {it.sourceThemeName}, {it.type1a},
                            {it.type1b}, {it.type1c}, {it.type2}, {it.type3}) )
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view.get()?.addOverlays(it) }
    }

    override fun removeView() = Unit

}