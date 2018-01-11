package com.jereksel.libresubstratum.activities.detailed2

import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import com.jereksel.libresubstratum.activities.detailed2.DetailedViewState.Companion.INITIAL
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DetailedPresenter @Inject constructor(
        val actionProcessor: DetailedActionProcessorHolder
): MviBasePresenter<DetailedView, DetailedViewState>() {

    lateinit var appId: String

    override fun bindIntents() {

//        val o = Observable.mergeArray(
//                Observable.just(DetailedAction.InitialAction)
//        )
//                .map { DetailedResult.ListLoaded(listOf()) }

        val o = intent(DetailedView::getActions)
                .mergeWith(Observable.just(DetailedAction.InitialAction(appId)))
                .compose(actionProcessor.actionProcessor)
                .scan(INITIAL, DetailedReducer)
                .observeOn(AndroidSchedulers.mainThread())

        subscribeViewState(o, DetailedView::render)

    }

}