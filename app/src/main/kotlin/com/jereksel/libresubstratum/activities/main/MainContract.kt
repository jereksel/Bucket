package com.jereksel.libresubstratum.activities.main

import com.jereksel.libresubstratum.MVPPresenter
import com.jereksel.libresubstratum.MVPView
import com.jereksel.libresubstratum.data.DetailedApplication

//When I uncomment it I got ClassNotFoundException during tests
//public interface MainContract {

    public interface Presenter : MVPPresenter<View> {
        fun getApplications()
        fun openThemeScreen(appId: String)
    }

    public interface View : MVPView {
        fun addApplications(list: List<DetailedApplication>)
        fun openThemeFragment(appId: String)
    }

//}