package com.jereksel.libresubstratum.activities.priorities

import com.google.common.collect.ArrayListMultimap
import com.jereksel.libresubstratum.activities.priorities.PrioritiesContract.Presenter
import com.jereksel.libresubstratum.activities.priorities.PrioritiesContract.View
import com.jereksel.libresubstratum.data.InstalledOverlay
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.OverlayService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import java.lang.ref.WeakReference

class PrioritiesPresenter(
        val overlayService: OverlayService,
        val packageManager: IPackageManager
): Presenter {

    var view = WeakReference<View>(null)

    override fun setView(view: View) {
        this.view = WeakReference(view)
    }

    override fun getApplication() {

        Observable.fromCallable { packageManager.getInstalledOverlays() }
                .observeOn(Schedulers.computation())
                .subscribeOn(Schedulers.computation())
                .flatMap {

                    val multiMap = ArrayListMultimap.create<String, InstalledOverlay>()

                    it.forEach {
                        multiMap.put(it.targetId, it)
                    }

                    multiMap.asMap().entries.toObservable()

                }
                .filter { it.value.size > 1 }
                .map { it.key to it.value.map { overlayService.getOverlayInfo(it.overlayId)?.enabled == true } }
                .map { it.first to it.second.filter { it } }
                .filter { it.second.size > 1 }
                .map { it.first }
//                .map { packageManager.getInstalledTheme(it) }
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { themes ->
                    view.get()?.addApplications(themes)
                }

    }

    override fun getIcon(appId: String) = packageManager.getAppIcon(appId)

    override fun removeView() {
        view = WeakReference<View>(null)
    }
}