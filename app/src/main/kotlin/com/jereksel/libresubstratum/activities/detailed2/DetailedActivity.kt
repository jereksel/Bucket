package com.jereksel.libresubstratum.activities.detailed2

import activitystarter.ActivityStarter
import activitystarter.Arg
import android.os.Bundle
import com.hannesdorfmann.mosby3.mvi.MviActivity
import com.jereksel.libresubstratum.App
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.extensions.getLogger
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_detailed2.*
import org.jetbrains.anko.toast
import javax.inject.Inject

class DetailedActivity: MviActivity<DetailedView, DetailedPresenter>(), DetailedView {

    @Inject
    lateinit var detailedPresenter: DetailedPresenter

    @Arg
    lateinit var appId : String

    val log = getLogger()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed2)
        ActivityStarter.fill(this)
        (application as App).getAppComponent(this).inject(this)
    }

    override fun createPresenter(): DetailedPresenter {
        detailedPresenter.appId = appId
        return detailedPresenter
    }

    override fun getActions(): Observable<DetailedAction> {
        return BehaviorSubject.create<DetailedAction>()
    }

    override fun render(viewState: DetailedViewState) {
        toast(viewState.toString())
//        log.debug(viewState.toString())
//        textView.text = viewState.number.toString()
    }

}

