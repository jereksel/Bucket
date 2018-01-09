package com.jereksel.libresubstratum.activities.detailed2

import com.hannesdorfmann.mosby3.mvi.MviActivity
import com.hannesdorfmann.mosby3.mvp.MvpView

interface DetailedView: MvpView {



    fun render(viewState: DetailedViewState)

}