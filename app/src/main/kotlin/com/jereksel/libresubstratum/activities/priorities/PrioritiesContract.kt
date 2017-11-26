package com.jereksel.libresubstratum.activities.priorities

import android.graphics.drawable.Drawable
import com.jereksel.libresubstratum.MVPPresenter
import com.jereksel.libresubstratum.MVPView

interface PrioritiesContract {

    interface Presenter : MVPPresenter<View> {
        fun getApplication()
        fun getIcon(appId: String): Drawable?
    }

    interface View : MVPView {
        fun addApplications(applications: List<String>)
    }

}