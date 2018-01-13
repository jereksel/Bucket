package com.jereksel.libresubstratum.activities.detailed2

import activitystarter.ActivityStarter
import activitystarter.Arg
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import com.hannesdorfmann.mosby3.mvi.MviActivity
import com.jereksel.libresubstratum.App
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.extensions.getLogger
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_detailed.*
import javax.inject.Inject

class DetailedActivity: MviActivity<DetailedView, DetailedPresenter>(), DetailedView {

    @Inject
    lateinit var detailedPresenter: DetailedPresenter

    @Arg
    lateinit var appId : String

    val log = getLogger()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed)
        ActivityStarter.fill(this)
        (application as App).getAppComponent(this).inject(this)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            //DefaultItemAnimator causes ripple on spinner change
            itemAnimator = null
            adapter = DetailedAdapter(listOf(), detailedPresenter)
        }
    }

    override fun createPresenter(): DetailedPresenter {
        detailedPresenter.appId = appId
        return detailedPresenter
    }

    override fun getActions(): Observable<DetailedAction> {
        return (recyclerView.adapter as DetailedAdapter).clicks
    }

    override fun render(viewState: DetailedViewState) {
//        toast(viewState.toString())

        if (viewState.themePack != null) {
            (recyclerView.adapter as DetailedAdapter).update(viewState.themePack.themes)
        }

//        log.debug(viewState.toString())
//        textView.text = viewState.number.toString()
    }

}

