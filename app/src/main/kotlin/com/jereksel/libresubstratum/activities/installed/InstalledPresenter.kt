package com.jereksel.libresubstratum.activities.installed

import com.jereksel.libresubstratum.activities.installed.InstalledContract.Presenter
import com.jereksel.libresubstratum.activities.installed.InstalledContract.View
import com.jereksel.libresubstratum.data.InstalledOverlay
import com.jereksel.libresubstratum.domain.IActivityProxy
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.OverlayService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.toObservable
import io.reactivex.rxkotlin.toSingle
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Subscription
import java.lang.ref.WeakReference

class InstalledPresenter(
        val packageManager: IPackageManager,
        val overlayService: OverlayService,
        val activityProxy: IActivityProxy
) : Presenter {

    private var view = WeakReference<View>(null)
    private var subscription: Disposable? = null
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

        val single = { overlayService.toggleOverlay(overlayId, enabled) }.toSingle()

        single
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())
                .subscribe { _ ->
                    view.get()?.refreshRecyclerView()
                    if (overlayId.startsWith("com.android.systemui")) {
                        view.get()?.showSnackBar("This change requires SystemUI restart", "Restart SystemUI", { overlayService.restartSystemUI() })
                    }
                }

    }

    private fun selectedOverlays() = (overlays ?: emptyList<InstalledOverlay>()).filterIndexed { index, _ -> state!![index] }

    override fun uninstallSelected() {

        val toUninstall = selectedOverlays()
                .map { it.overlayId }

        toUninstall.toSingle()
                .observeOn(Schedulers.computation())
                .subscribeOn(Schedulers.computation())
                .map {
                    overlayService.uninstallApk(it)
                    overlays?.removeAll(selectedOverlays())
                    state = overlays?.map { false }?.toTypedArray()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { _ ->
                    val o = this.overlays
                    if (o != null) {
                        this.view.get()?.addOverlays(o)
                    }
                }

    }

    override fun enableSelected() {

        val toEnable = selectedOverlays()
                .map { it.overlayId }

        toEnable.toObservable()
                .observeOn(Schedulers.computation())
                .subscribeOn(Schedulers.computation())
                .filter { overlayService.getOverlayInfo(it)?.enabled == false }
                .toList()
                .map { overlayService.enableOverlays(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { _ ->
                    deselectAll()
                    view.get()?.refreshRecyclerView()
                }

    }

    override fun disableSelected() {


        val toDisable = selectedOverlays()
                .map { it.overlayId }

        toDisable.toObservable()
                .observeOn(Schedulers.computation())
                .subscribeOn(Schedulers.computation())
                .filter { overlayService.getOverlayInfo(it)?.enabled == true }
                .toList()
                .map { overlayService.disableOverlays(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { _ ->
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