package com.jereksel.libresubstratum.activities.main

import com.jereksel.libresubstratum.View
import com.jereksel.libresubstratum.data.DetailedApplication

interface IMainView : View {
    fun addApplications(list: List<DetailedApplication>)
    fun openThemeFragment(appId: String)
}

