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

    interface Presenter : MVPPresenter<View> {
        fun getInstalledOverlays()
        fun toggleOverlay(overlayId: String, enabled: Boolean)
        fun getOverlayInfo(overlayId: String): OverlayInfo?
        fun openActivity(appId: String): Boolean
        fun uninstallSelected()
        fun disableSelected()
        fun enableSelected()
        fun selectAll()
        fun deselectAll()
        fun restartSystemUI()
        fun setFilter(filter: String)

        //RecyclerView
        fun setState(overlayId: String, isEnabled: Boolean)
        fun getState(overlayId: String): Boolean
    }

}