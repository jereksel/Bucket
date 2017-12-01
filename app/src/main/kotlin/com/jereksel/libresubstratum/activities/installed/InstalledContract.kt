package com.jereksel.libresubstratum.activities.installed

import com.jereksel.libresubstratum.MVPPresenter
import com.jereksel.libresubstratum.MVPView
import com.jereksel.libresubstratum.data.InstalledOverlay
import com.jereksel.libresubstratum.domain.OverlayInfo

interface InstalledContract {

    interface View : MVPView {
        fun addOverlays(overlays: List<InstalledOverlay>)
        fun updateOverlays(overlays: List<InstalledOverlay>)
        fun showSnackBar(message: String, buttonText: String, callback: () -> Unit)
        fun refreshRecyclerView()
    }

    abstract class Presenter : MVPPresenter<View>() {
        abstract fun getInstalledOverlays()
        abstract fun toggleOverlay(overlayId: String, enabled: Boolean)
        abstract fun getOverlayInfo(overlayId: String): OverlayInfo?
        abstract fun openActivity(appId: String): Boolean
        abstract fun uninstallSelected()
        abstract  fun disableSelected()
        abstract fun enableSelected()
        abstract fun selectAll()
        abstract fun deselectAll()
        abstract fun restartSystemUI()
        abstract fun setFilter(filter: String)

        //RecyclerView
        abstract fun setState(overlayId: String, isEnabled: Boolean)
        abstract fun getState(overlayId: String): Boolean
    }

}