package com.jereksel.libresubstratum.activities.prioritiesdetail

import com.jereksel.libresubstratum.activities.prioritiesdetail.PrioritiesDetailContract.Presenter
import com.jereksel.libresubstratum.activities.prioritiesdetail.PrioritiesDetailContract.View
import com.jereksel.libresubstratum.data.InstalledOverlay
import com.jereksel.libresubstratum.domain.ActivityProxy
import com.jereksel.libresubstratum.domain.IActivityProxy
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.OverlayService
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.ref.WeakReference

class PrioritiesDetailPresenter(
        val overlayService: OverlayService,
        val packageManager: IPackageManager,
        val activityProxy: IActivityProxy
): Presenter {

    var view = WeakReference<View>(null)

    lateinit var overlays: List<InstalledOverlay>

    var fabShown = false

    override fun setView(view: View) {
        this.view = WeakReference(view)
    }

    override fun removeView() {
        view = WeakReference<View>(null)
    }

    override fun getOverlays(targetId: String) {

        fabShown = false

        Schedulers.io().scheduleDirect {
            val installedOverlays = packageManager.getInstalledOverlays()
            val installedOverlaysMap = installedOverlays.map { it.overlayId to it }.toMap()
            val priorities = overlayService.getOverlaysPrioritiesForTarget(targetId).filter { it.enabled }
            val mapped = priorities.map { installedOverlaysMap[it.overlayId]!! }
            overlays = mapped

            AndroidSchedulers.mainThread().scheduleDirect {
                view.get()?.setOverlays(mapped)
            }
        }

    }

    override fun updateOverlays(overlays: List<InstalledOverlay>) {

        if (overlays == this.overlays && fabShown) {
            fabShown = false
            view.get()?.hideFab()
        } else if (overlays != this.overlays && !fabShown) {
            fabShown = true
            view.get()?.showFab()
        }

    }

    override fun updatePriorities(overlays: List<InstalledOverlay>) {

        Single.fromCallable { overlayService.updatePriorities(overlays.map { it.overlayId }) }
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { _ ->
                    //Copy list
                    this.overlays = overlays.toMutableList()
                    fabShown = false
                    view.get()?.hideFab()
                    view.get()?.notifyPrioritiesChanged()
                }


    }

    override fun openAppInSplit(targetId: String) {
        activityProxy.openActivityInSplit(targetId)
    }

}