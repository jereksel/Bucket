package com.jereksel.libresubstratum.activities.installed

import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.activities.installed.InstalledContract.Presenter
import com.jereksel.libresubstratum.activities.installed.InstalledContract.View
import com.jereksel.libresubstratum.data.InstalledOverlay
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.extensions.has
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class InstalledPresenter(val packageManager: IPackageManager): Presenter {

    private var view: View? = null

    override fun setView(view: View) {
        this.view = view
    }

    val metadataOverlayTarget = "Substratum_Target"
    val metadataOverlayParent = "Substratum_Parent"

    private var subscription: Subscription? = null

    override fun getInstalledOverlays() {

        subscription = Observable.fromCallable { packageManager.getApplications() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())
                .flatMapIterable { it }
                .filter { it.metadata.has(metadataOverlayTarget) }
                .map {
                    val overlay = it.appId
                    val parent = it.metadata.getString(metadataOverlayParent)
                    val parentIcon = packageManager.getAppIcon(parent)!!
                    val parentName = packageManager.getAppName(parent)
                    val target = it.metadata.getString(metadataOverlayTarget)
                    val targetIcon = packageManager.getAppIcon(target)!!
//                    val targetName = packageManager.getAppName(target)
                    val targetName = getTargetName(overlay, target)
                    InstalledOverlay(overlay, parent, parentName, parentIcon, target, targetName, targetIcon)
                }
                .toList()
                .subscribe { view?.addOverlays(it) }

    }

    private fun getTargetName(overlayId: String, targetId: String) =
            when {
                overlayId.startsWith("com.android.systemui.navbars") -> packageManager.stringIdToString(R.string.systemui_navigation)
                overlayId.startsWith("com.android.systemui.headers") -> packageManager.stringIdToString(R.string.systemui_headers)
                overlayId.startsWith("com.android.systemui.tiles") -> packageManager.stringIdToString(R.string.systemui_qs_tiles)
                overlayId.startsWith("com.android.systemui.statusbars") -> packageManager.stringIdToString(R.string.systemui_statusbar)
                overlayId.startsWith("com.android.settings.icons") -> packageManager.stringIdToString(R.string.settings_icons)
                else -> packageManager.getAppName(targetId)
            }

    override fun removeView() {
        view = null
    }

}