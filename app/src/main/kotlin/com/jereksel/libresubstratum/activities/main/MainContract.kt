package com.jereksel.libresubstratum.activities.main

import com.jereksel.libresubstratum.MVPPresenter
import com.jereksel.libresubstratum.MVPView
import com.jereksel.libresubstratum.data.InstalledTheme
import com.jereksel.libresubstratum.data.KeyPair

interface MainContract {

    abstract class Presenter : MVPPresenter<View>() {
        abstract fun getApplications()
        abstract fun openThemeScreen(appId: String)
        abstract fun checkPermissions()
        abstract fun getKeyPair(appId: String): KeyPair?
    }

    interface View : MVPView {
        fun addApplications(list: List<InstalledTheme>)
        fun openThemeFragment(appId: String)
        fun requestPermissions(perms: List<String>)
        fun dismissDialog()
        fun showUndismissableDialog(message: String)
    }

}