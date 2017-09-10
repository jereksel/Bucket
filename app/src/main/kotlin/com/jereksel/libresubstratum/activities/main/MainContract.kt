package com.jereksel.libresubstratum.activities.main

import com.jereksel.libresubstratum.MVPPresenter
import com.jereksel.libresubstratum.MVPView
import com.jereksel.libresubstratum.data.InstalledTheme
import com.jereksel.libresubstratum.data.MainViewTheme

interface MainContract {

    interface Presenter : MVPPresenter<View> {
        fun getApplications()
        fun openThemeScreen(appId: String)
//        fun isThemeEncrypted(appId: String)
    }

    interface View : MVPView {
        fun addApplications(list: List<MainViewTheme>)
        fun openThemeFragment(appId: String)
        fun setDialogProgress(progress: Int)
    }

}