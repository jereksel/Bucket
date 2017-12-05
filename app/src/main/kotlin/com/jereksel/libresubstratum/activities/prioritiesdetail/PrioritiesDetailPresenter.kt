package com.jereksel.libresubstratum.activities.prioritiesdetail

import com.jereksel.libresubstratum.activities.prioritiesdetail.PrioritiesDetailContract.Presenter
import com.jereksel.libresubstratum.data.InstalledOverlay
import com.jereksel.libresubstratum.domain.IActivityProxy
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.OverlayService
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.guava.await
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.rx2.await
import org.jetbrains.anko.Android
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.doAsync
import java.util.concurrent.CompletableFuture

class PrioritiesDetailPresenter(
        val overlayService: OverlayService,
        val packageManager: IPackageManager,
        val activityProxy: IActivityProxy
): Presenter() {

    lateinit var overlays: List<InstalledOverlay>

    var fabShown = false

    suspend override fun getOverlays(targetId: String) {

        fabShown = false

        val installedOverlays = packageManager.getInstalledOverlays()
        val installedOverlaysMap = installedOverlays.map { it.overlayId to it }.toMap()
        val priorities = overlayService.getOverlaysPrioritiesForTarget(targetId).await().filter { it.enabled }
        val mapped = priorities.map { installedOverlaysMap[it.overlayId]!! }
        overlays = mapped

        view.get()?.setOverlays(mapped)

//
//        Schedulers.io().scheduleDirect {
//            val installedOverlays = packageManager.getInstalledOverlays()
//            val installedOverlaysMap = installedOverlays.map { it.overlayId to it }.toMap()
//            val priorities = overlayService.getOverlaysPrioritiesForTarget(targetId).filter { it.enabled }
//            val mapped = priorities.map { installedOverlaysMap[it.overlayId]!! }
//            overlays = mapped
//
//            AndroidSchedulers.mainThread().scheduleDirect {
//                view.get()?.setOverlays(mapped)
//            }
//        }

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

    suspend override fun updatePriorities(overlays: List<InstalledOverlay>) {

        overlayService.updatePriorities(overlays.map { it.overlayId }).await()

        this@PrioritiesDetailPresenter.overlays = overlays.toMutableList()
        fabShown = false
        view.get()?.hideFab()
        view.get()?.notifyPrioritiesChanged()

    }

    override fun openAppInSplit(targetId: String) {
        activityProxy.openActivityInSplit(targetId)
    }

}