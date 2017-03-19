package com.jereksel.libresubstratum.activities.detailed

import com.jereksel.libresubstratum.Presenter

interface IDetailedPresenter : Presenter<IDetailedView> {
    fun readTheme(appId: String)
}
