package com.jereksel.libresubstratum.activities.prioritiesdetail

import com.jereksel.libresubstratum.MVPPresenter
import com.jereksel.libresubstratum.MVPView
import com.jereksel.libresubstratum.data.InstalledOverlay

interface PrioritiesDetailContract {

    abstract class Presenter : MVPPresenter<View>() {
        abstract fun getOverlays(targetId: String)
        abstract fun updateOverlays(overlays: List<InstalledOverlay>)
        abstract fun updatePriorities(overlays: List<InstalledOverlay>)
        abstract fun openAppInSplit(targetId: String)
    }

    interface View : MVPView {
        fun setOverlays(overlays: List<InstalledOverlay>)
        fun showFab()
        fun hideFab()
        fun notifyPrioritiesChanged()
    }

}