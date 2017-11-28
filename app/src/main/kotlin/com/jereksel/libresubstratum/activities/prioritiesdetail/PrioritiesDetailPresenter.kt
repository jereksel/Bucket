package com.jereksel.libresubstratum.activities.prioritiesdetail

import com.jereksel.libresubstratum.activities.prioritiesdetail.PrioritiesDetailContract.Presenter
import com.jereksel.libresubstratum.activities.prioritiesdetail.PrioritiesDetailContract.View
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.OverlayService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.ref.WeakReference

class PrioritiesDetailPresenter(
        val overlayService: OverlayService,
        val packageManager: IPackageManager
): Presenter {

    var view = WeakReference<View>(null)

    override fun setView(view: View) {
        this.view = WeakReference(view)
    }

    override fun removeView() {
        view = WeakReference<View>(null)
    }

    override fun getOverlays(targetId: String) {

        Schedulers.io().scheduleDirect {
            val installedOverlays = packageManager.getInstalledOverlays()
            val installedOverlaysMap = installedOverlays.map { it.overlayId to it }.toMap()
            val priorities = overlayService.getOverlaysPrioritiesForTarget(targetId).filter { it.enabled }
            val mapped = priorities.map { installedOverlaysMap[it.overlayId]!! }

            AndroidSchedulers.mainThread().scheduleDirect {
                view.get()?.setOverlays(mapped)
            }
        }

//
//        { packageManager.getInstalledOverlays() }.toSingle().toObservable()
//                .observeOn(Schedulers.io())
//                .subscribeOn(Schedulers.io())
//                .flatMapIterable { it }
//                .filter { it.targetId == targetId }
//                .filter { overlayService.getOverlayInfo(it.overlayId)?.enabled == true }
//                .toList()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe { overlays ->
//                    view.get()?.setOverlays(overlays)
//                }


    }

}