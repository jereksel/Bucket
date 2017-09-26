package com.jereksel.libresubstratum.activities.navigationbar

import com.jereksel.libresubstratum.MVPPresenter
import com.jereksel.libresubstratum.MVPView
import com.jereksel.libresubstratum.data.NavigationBarOverlay

interface NavigationBarContract {

    interface View: MVPView {
        fun show(bars: List<NavigationBarOverlay>)
    }

    interface Presenter: MVPPresenter<View> {
        fun getBars(appId: String)
    }

}