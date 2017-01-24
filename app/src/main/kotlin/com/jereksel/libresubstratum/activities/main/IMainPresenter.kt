package com.jereksel.libresubstratum.activities.main

import com.jereksel.libresubstratum.Presenter

interface IMainPresenter : Presenter<IMainView> {
    fun getApplications()
}
