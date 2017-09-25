package com.jereksel.libresubstratum.activities.navigationbar

import com.jereksel.libresubstratum.MVPPresenter
import com.jereksel.libresubstratum.MVPView

/**
 * Created by Andrzej on 2017-09-25.
 */
interface NavigationBarContract {

    interface View: MVPView {

    }

    interface Presenter: MVPPresenter<View> {
        fun getBars()
    }

}