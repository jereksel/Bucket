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
import rx.schedulers.Schedulers
import java.lang.ref.WeakReference

class InstalledPresenter(
        val packageManager: IPackageManager,
        val overlayService: OverlayService,
        val activityProxy: IActivityProxy
) : Presenter {

    private var view = WeakReference<View>(null)
    private var subscription: Subscription? = null
    private var overlays: List<InstalledOverlay>? = null

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
                    it.sortedWith(compareBy({ it.targetName }, { it.sourceThemeName }, { it.type1a },
                            { it.type1b }, { it.type1c }, { it.type2 }, { it.type3 }))
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    overlays = it
                    view.get()?.addOverlays(it)
                }
    }

    override fun toggleOverlay(overlayId: String, enabled: Boolean) {
        overlayService.toggleOverlay(overlayId, enabled)
        if (overlayId.startsWith("com.android.systemui")) {
            view.get()?.showSnackBar("This change requires SystemUI restart", "Restart SystemUI", { overlayService.restartSystemUI() })
        }
    }

    override fun getOverlayInfo(overlayId: String) = overlayService.getOverlayInfo(overlayId)

    override fun openActivity(appId: String) = activityProxy.openActivityInSplit(appId)

    override fun removeView() = Unit

}