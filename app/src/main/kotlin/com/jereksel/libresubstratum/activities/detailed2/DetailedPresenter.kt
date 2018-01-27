package com.jereksel.libresubstratum.activities.detailed2

import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import com.jereksel.libresubstratum.activities.detailed2.DetailedViewState.Companion.INITIAL
import com.jereksel.libresubstratum.domain.ClipboardManager
import com.jereksel.libresubstratum.domain.IActivityProxy
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.extensions.getLogger
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.zipWith
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DetailedPresenter @Inject constructor(
        val actionProcessor: DetailedActionProcessorHolder,
        val packageManager: IPackageManager,
        val activityProxy: IActivityProxy,
        val clipboardManager: ClipboardManager
): MviBasePresenter<DetailedView, DetailedViewState>() {

    val log = getLogger()

    lateinit var appId: String

    override fun bindIntents() {

        val s2 = intent(DetailedView::getActions)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .startWith(DetailedAction.InitialAction(appId))

        val s3 = BehaviorSubject.create<DetailedAction>()

        val states = Observable.merge(s2, s3.observeOn(Schedulers.io()).subscribeOn(Schedulers.io()))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(actionProcessor.actionProcessor)
                .scan(INITIAL, { t1, t2 ->
                    val result = DetailedReducer.apply(t1, t2)
                    result.second.forEach { s3.onNext(it) }
                    result.first
                })
//                .sample(1, TimeUnit.SECONDS)
                .distinctUntilChanged()
                .doOnNext { log.debug("New state") }
                .observeOn(AndroidSchedulers.mainThread())

        subscribeViewState(states, DetailedView::render)

    }

    fun getAppIcon(appId: String) = packageManager.getAppIcon(appId)

    fun openInSplit(appId: String) = activityProxy.openActivityInSplit(appId)

    fun setClipboard(errorText: String) {
        clipboardManager.addToClipboard(errorText)
    }

}