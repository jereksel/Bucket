package com.jereksel.libresubstratum.activities.prioritiesdetail

import com.jereksel.libresubstratum.MVPPresenter
import com.jereksel.libresubstratum.MVPView
import com.jereksel.libresubstratum.data.InstalledOverlay

interface PrioritiesDetailContract {

    interface Presenter : MVPPresenter<View> {
        fun getOverlays(targetId: String)
    }

    interface View : MVPView {
        fun setOverlays(overlays: List<InstalledOverlay>)
    }

}