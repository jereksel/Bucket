package com.jereksel.libresubstratum.activities.navigationbar

import com.jereksel.libresubstratum.activities.navigationbar.NavigationBarContract.Presenter
import com.jereksel.libresubstratum.activities.navigationbar.NavigationBarContract.View
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.IThemeReader
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.ref.WeakReference

class NavigationBarPresenter(
        val packageManager: IPackageManager,
        val themeReader: IThemeReader
) : Presenter {

    var view = WeakReference<View>(null)

    override fun setView(view: View) {
        this.view = WeakReference(view)
    }

    override fun removeView() {
    }

    override fun getBars(appId: String) {

        val location = packageManager.getAppLocation(appId)

        Observable.fromCallable { themeReader.readThemePack(location) }
                .observeOn(Schedulers.computation())
                .subscribeOn(Schedulers.computation())
                .flatMapIterable { it.themes.find { it.application == "com.android.systemui.navbars" }?.type2?.extensions ?: listOf() }
                .flatMap {
                    Observable.fromCallable { themeReader.getNavigationBar(location, it.name) }
                            .observeOn(Schedulers.io())
                            .subscribeOn(Schedulers.io())
                }
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.get()?.show(it.mapNotNull { it })
                }, {

                })

//                .map { it.themes.find { it.application == "com.android.systemui.navbars" }?.type2?.extensions ?: listOf() }
//                .map { it.mapNotNull { themeReader.getNavigationBar(location, it.name) } }
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe {
//                    view.get()?.show(it)
//                }

    }

}