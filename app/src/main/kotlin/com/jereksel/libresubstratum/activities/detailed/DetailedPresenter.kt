package com.jereksel.libresubstratum.activities.detailed

import com.jereksel.libresubstratum.data.ThemePack
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.IThemeReader
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File

class DetailedPresenter(val packageManager : IPackageManager, val themeReader: IThemeReader) : DetailedContract.Presenter {

    var detailedView : DetailedContract.View? = null

    override fun setView(view: DetailedContract.View) {
        detailedView = view
    }

    override fun removeView() {
        detailedView = null
    }

    override fun readTheme(appId: String) {

        val location = File(File(packageManager.getCacheFolder(), appId), "assets")

        Observable.fromCallable { themeReader.readThemePack(location) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())
                .map {
                    //Remove apps that are not installed
                    val installedApps = it.themes.filter { packageManager.isPackageInstalled(it.application) }
                    ThemePack(installedApps, it.type3)
                }
                .subscribe { detailedView?.addThemes(it) }
    }

}
