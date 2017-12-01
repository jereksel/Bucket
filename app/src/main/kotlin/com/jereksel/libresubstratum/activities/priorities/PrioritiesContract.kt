package com.jereksel.libresubstratum.activities.priorities

import android.graphics.drawable.Drawable
import com.jereksel.libresubstratum.MVPPresenter
import com.jereksel.libresubstratum.MVPView

interface PrioritiesContract {

    abstract class Presenter : MVPPresenter<View>() {
        abstract fun getApplication()
        abstract fun getIcon(appId: String): Drawable?
    }

    interface View : MVPView {
        fun addApplications(applications: List<String>)
    }

}