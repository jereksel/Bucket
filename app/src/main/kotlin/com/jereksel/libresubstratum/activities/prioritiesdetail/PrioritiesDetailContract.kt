package com.jereksel.libresubstratum.activities.prioritiesdetail

import com.jereksel.libresubstratum.MVPPresenter
import com.jereksel.libresubstratum.MVPView
import com.jereksel.libresubstratum.data.InstalledOverlay

interface PrioritiesDetailContract {

    abstract class Presenter : MVPPresenter<View>() {
        abstract suspend fun getOverlays(targetId: String)
        abstract fun updateOverlays(overlays: List<InstalledOverlay>)
        abstract suspend fun updatePriorities(overlays: List<InstalledOverlay>)
        abstract fun openAppInSplit(targetId: String)
    }

    interface View : MVPView {
        fun setOverlays(overlays: List<InstalledOverlay>)
        fun showFab()
        fun hideFab()
        fun notifyPrioritiesChanged()
    }

}