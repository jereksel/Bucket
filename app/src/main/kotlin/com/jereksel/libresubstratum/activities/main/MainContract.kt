package com.jereksel.libresubstratum.activities.main

import com.jereksel.libresubstratum.MVPPresenter
import com.jereksel.libresubstratum.MVPView
import com.jereksel.libresubstratum.data.InstalledTheme

interface MainContract {

    interface Presenter : MVPPresenter<View> {
        fun getApplications()
        fun openThemeScreen(appId: String)
    }

    interface View : MVPView {
        fun addApplications(list: List<InstalledTheme>)
        fun openThemeFragment(appId: String)
    }

}