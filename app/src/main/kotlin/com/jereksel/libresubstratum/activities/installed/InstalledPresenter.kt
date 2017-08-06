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

    companion object {
        val metadataOverlayTarget = "Substratum_Target"
        val metadataOverlayParent = "Substratum_Parent"

        val metadataOverlayType1a = "Substratum_Type1a"
        val metadataOverlayType1b = "Substratum_Type1b"
        val metadataOverlayType1c = "Substratum_Type1c"
        val metadataOverlayType2 = "Substratum_Type2"
        val metadataOverlayType3 = "Substratum_Type3"
    }

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
                    val targetName = getTargetName(overlay, target)

                    val type1a = it.metadata.getString(metadataOverlayType1a)
                    val type1b = it.metadata.getString(metadataOverlayType1b)
                    val type1c = it.metadata.getString(metadataOverlayType1c)
                    val type2 = it.metadata.getString(metadataOverlayType2)
                    val type3 = it.metadata.getString(metadataOverlayType3)
//                    val targetName = packageManager.getAppName(target)
                    InstalledOverlay(overlay, parent, parentName, parentIcon, target, targetName,
                            targetIcon, type1a, type1b, type1c, type2, type3)
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