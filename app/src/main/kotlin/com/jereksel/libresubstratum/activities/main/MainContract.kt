package com.jereksel.libresubstratum.activities.main

import com.jereksel.libresubstratum.MVPPresenter
import com.jereksel.libresubstratum.MVPView
import com.jereksel.libresubstratum.data.InstalledTheme
import com.jereksel.libresubstratum.data.KeyPair

interface MainContract {

    interface Presenter : MVPPresenter<View> {
        fun getApplications()
        fun openThemeScreen(appId: String)
        fun checkPermissions()
        fun getKeyPair(appId: String): KeyPair?
//        fun isThemeEncrypted(appId: String)
    }

    interface View : MVPView {
        fun addApplications(list: List<InstalledTheme>)
        fun openThemeFragment(appId: String)
        fun requestPermissions(perms: List<String>)
        fun dismissDialog()
        fun showUndismissableDialog(message: String)
    }

}