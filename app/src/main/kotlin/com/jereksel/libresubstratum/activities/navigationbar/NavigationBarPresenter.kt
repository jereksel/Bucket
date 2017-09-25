package com.jereksel.libresubstratum.activities.navigationbar

import com.jereksel.libresubstratum.activities.navigationbar.NavigationBarContract.Presenter
import com.jereksel.libresubstratum.activities.navigationbar.NavigationBarContract.View
import java.lang.ref.WeakReference

class NavigationBarPresenter: Presenter {

    var view = WeakReference<View>(null)

    override fun setView(view: View) {
        this.view = WeakReference(view)
    }

    override fun removeView() {
    }

    override fun getBars() {



    }

}