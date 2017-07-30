package com.jereksel.libresubstratum.activities.installed

import com.jereksel.libresubstratum.activities.installed.InstalledContract.Presenter
import com.jereksel.libresubstratum.activities.installed.InstalledContract.View
import com.jereksel.libresubstratum.domain.IPackageManager

class InstalledPresenter(val packageManager: IPackageManager): Presenter {

    private var view: View? = null

    override fun setView(view: View) {
        this.view = view
    }

    override fun getInstalledOverlays() {
        packageManager.getApplications()
    }

    override fun removeView() {
        view = null
    }

}