package com.jereksel.libresubstratum.activities.detailed2

import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import com.jereksel.libresubstratum.activities.detailed2.DetailedViewState.Companion.INITIAL
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.extensions.getLogger
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.zipWith
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DetailedPresenter @Inject constructor(
        val actionProcessor: DetailedActionProcessorHolder,
        val packageManager: IPackageManager
): MviBasePresenter<DetailedView, DetailedViewState>() {

    val log = getLogger()

    lateinit var appId: String

    override fun bindIntents() {

        actionProcessor.appId = appId

        val simpleProcessor = DetailedSimpleUIActionProcessor(appId, viewStateObservable)

        viewStateObservable.subscribe {
            log.debug("New state: {}", it)
        }

        val s1 = intent(DetailedView::getSimpleUIActions)
                .compose(simpleProcessor.actionProcessor)

        val s2 = intent(DetailedView::getActions)
                .mergeWith(Observable.just(DetailedAction.InitialAction(appId)))

//        val o1 = intent(DetailedView::getSimpleUIActions)
//                .compose(simpleProcessor.actionProcessor)
//
//        val o2 = intent(DetailedView::getActions)
//                .mergeWith(Observable.just(DetailedAction.InitialAction(appId)))
        val states = Observable.merge(s1, s2)
//        val states = s2
                .compose(actionProcessor.actionProcessor)
                .scan(INITIAL, DetailedReducer)
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())

//        subscribeViewState(
//                Observable.merge(o1, o2), DetailedView::render
//        )

        subscribeViewState(states, DetailedView::render)

    }

    fun getAppIcon(appId: String) = packageManager.getAppIcon(appId)

}