package com.jereksel.libresubstratum.activities.prioritiesdetail

import com.jereksel.libresubstratum.activities.priorities.PrioritiesContract
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.OverlayService

class PrioritiesDetailPresenter(
        val overlayService: OverlayService,
        val packageManager: IPackageManager
): PrioritiesDetailContract.Presenter {

    override fun setView(view: PrioritiesDetailContract.View) {
    }

    override fun removeView() {
    }
}