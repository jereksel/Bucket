package com.jereksel.libresubstratum.activities.detailed

import com.jereksel.libresubstratum.MVPPresenter
import com.jereksel.libresubstratum.MVPView
import com.jereksel.libresubstratum.data.ThemePack

interface DetailedContract {

    interface View : MVPView {
        fun addThemes(themePack: ThemePack)
    }

    interface Presenter : MVPPresenter<View> {
        fun readTheme(appId: String)
    }

}

