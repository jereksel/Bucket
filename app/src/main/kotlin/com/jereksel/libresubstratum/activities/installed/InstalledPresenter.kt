package com.jereksel.libresubstratum.activities.installed

import com.jereksel.libresubstratum.activities.installed.InstalledContract.Presenter
import com.jereksel.libresubstratum.activities.installed.InstalledContract.View
import com.jereksel.libresubstratum.data.InstalledOverlay
import com.jereksel.libresubstratum.domain.IActivityProxy
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.OverlayService
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.toSingletonObservable
import rx.schedulers.Schedulers
import java.lang.ref.WeakReference

class InstalledPresenter(
        val packageManager: IPackageManager,
        val overlayService: OverlayService,
        val activityProxy: IActivityProxy
) : Presenter {

    private var view = WeakReference<View>(null)
    private var subscription: Subscription? = null
    private var overlays: MutableList<InstalledOverlay>? = null

    @JvmField
    var state: Array<Boolean>? = null

    override fun setView(view: View) {
        this.view = WeakReference(view)
    }

    override fun getInstalledOverlays() {

        val o = overlays

        if (o != null) {
            view.get()?.addOverlays(o)
        }

        subscription = Observable.fromCallable { packageManager.getInstalledOverlays() }
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .map {
                    it.sortedWith(compareBy({ it.sourceThemeName }, { it.targetName }, { it.type1a },
                            { it.type1b }, { it.type1c }, { it.type2 }, { it.type3 }))
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    overlays = it.toMutableList()
                    state = it.map { false }.toTypedArray()
                    view.get()?.addOverlays(it)
                }
    }

    override fun toggleOverlay(overlayId: String, enabled: Boolean) {
        overlayService.toggleOverlay(overlayId, enabled)
        if (overlayId.startsWith("com.android.systemui")) {
            view.get()?.showSnackBar("This change requires SystemUI restart", "Restart SystemUI", { overlayService.restartSystemUI() })
        }
    }

    private fun selectedOverlays() = (overlays ?: emptyList<InstalledOverlay>()).filterIndexed { index, _ -> state!![index] }

    override fun uninstallSelected() {

        val toUninstall = selectedOverlays()
                .map { it.overlayId }

        toUninstall.toSingletonObservable()
                .observeOn(Schedulers.computation())
                .subscribeOn(Schedulers.computation())
                .map {
                    overlayService.uninstallApk(it)
                    overlays?.removeAll(selectedOverlays())
                    state = overlays?.map { false }?.toTypedArray()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val o = this.overlays
                    if (o != null) {
                        this.view.get()?.addOverlays(o)
                    }
                }

    }

    override fun enableSelected() {

        val toEnable = selectedOverlays()
                .map { it.overlayId }

        Observable.from(toEnable)
                .observeOn(Schedulers.computation())
                .subscribeOn(Schedulers.computation())
                .filter { !overlayService.getOverlayInfo(it).enabled }
                .toList()
                .map { overlayService.enableOverlays(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    deselectAll()
                    view.get()?.refreshRecyclerView()
                }

    }

    override fun disableSelected() {


        val toDisable = selectedOverlays()
                .map { it.overlayId }

        Observable.from(toDisable)
                .observeOn(Schedulers.computation())
                .subscribeOn(Schedulers.computation())
                .filter { overlayService.getOverlayInfo(it).enabled }
                .toList()
                .map { overlayService.disableOverlays(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    deselectAll()
                    view.get()?.refreshRecyclerView()
                }

    }

    override fun selectAll() {
        val state = state
        if (state != null) {
            for (i in 0 until state.size) {
                state[i] = true
            }
        }
        view.get()?.refreshRecyclerView()
    }

    override fun deselectAll() {
        val state = state
        if (state != null) {
            for (i in 0 until state.size) {
                state[i] = false
            }
        }
        view.get()?.refreshRecyclerView()
    }

    override fun getOverlayInfo(overlayId: String) = overlayService.getOverlayInfo(overlayId)

    override fun openActivity(appId: String) = activityProxy.openActivityInSplit(appId)

    override fun getState(position: Int) = state!![position]

    override fun setState(position: Int, isEnabled: Boolean) {
        state!![position] = isEnabled
    }

    override fun removeView() = Unit

}