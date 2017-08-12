package com.jereksel.libresubstratum.activities.installed

import com.jereksel.libresubstratum.MVPPresenter
import com.jereksel.libresubstratum.MVPView
import com.jereksel.libresubstratum.data.InstalledOverlay

interface InstalledContract {

    interface View : MVPView {
        fun addOverlays(overlays: List<InstalledOverlay>)
        fun showSnackBar(message: String, buttonText: String, callback: () -> Unit)
    }

    interface Presenter : MVPPresenter<View> {
        fun getInstalledOverlays()
        fun toggleOverlay(overlayId: String, enabled: Boolean)
    }

}