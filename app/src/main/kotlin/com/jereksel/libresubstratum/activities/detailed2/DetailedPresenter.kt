package com.jereksel.libresubstratum.activities.detailed2

import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class DetailedPresenter: MviBasePresenter<DetailedView, DetailedViewState>() {

    val o = Observable
            .interval(100, TimeUnit.MILLISECONDS)
            .scan(0) { t1, _ -> t1 + 1 }

    override fun bindIntents() {

        Observable.mergeArray(
                Observable.just(DetailedAction.InitialAction)
        )

        val observable = o
                .scan(DetailedViewState(0), { old, num -> old.copy(number = num) })
                .observeOn(AndroidSchedulers.mainThread())

        subscribeViewState(observable, DetailedView::render)

    }

}