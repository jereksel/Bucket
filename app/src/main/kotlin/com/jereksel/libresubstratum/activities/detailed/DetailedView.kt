package com.jereksel.libresubstratum.activities.detailed

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable

interface DetailedView: MvpView {

    fun getActions(): Observable<DetailedAction>

    fun render(viewState: DetailedViewState)

}