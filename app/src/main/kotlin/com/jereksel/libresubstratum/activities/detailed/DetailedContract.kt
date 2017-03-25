package com.jereksel.libresubstratum.activities.detailed

import com.jereksel.libresubstratum.MVPPresenter
import com.jereksel.libresubstratum.MVPView
import com.jereksel.libresubstratum.data.ThemePack

//public interface DetailedContract {

    public interface View : MVPView {
        fun addThemes(themePack: ThemePack)
    }

    public interface Presenter : MVPPresenter<View> {
        fun readTheme(appId: String)
    }

//}

