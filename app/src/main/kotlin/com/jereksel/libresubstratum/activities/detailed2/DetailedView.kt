package com.jereksel.libresubstratum.activities.detailed2

import com.hannesdorfmann.mosby3.mvi.MviActivity
import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable

interface DetailedView: MvpView {

    fun getSimpleUIActions(): Observable<DetailedSimpleUIAction>

    fun getActions(): Observable<DetailedAction>

    fun render(viewState: DetailedViewState)

}