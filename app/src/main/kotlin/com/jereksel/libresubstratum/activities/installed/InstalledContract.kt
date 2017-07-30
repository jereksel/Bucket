package com.jereksel.libresubstratum.activities.installed

import com.jereksel.libresubstratum.MVPPresenter
import com.jereksel.libresubstratum.MVPView

interface InstalledContract {

    interface View : MVPView {
    }

    interface Presenter : MVPPresenter<View> {
        fun getInstalledOverlays()
    }

}