package com.jereksel.libresubstratum.activities.detailed2

import activitystarter.ActivityStarter
import activitystarter.Arg
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.hannesdorfmann.mosby3.mvi.MviActivity
import com.jereksel.libresubstratum.App
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.data.Type3ExtensionToString
import com.jereksel.libresubstratum.extensions.getLogger
import com.jereksel.libresubstratum.extensions.list
import com.jereksel.libresubstratum.extensions.selectListener
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_detailed.*
import javax.inject.Inject

class DetailedActivity: MviActivity<DetailedView, DetailedPresenter>(), DetailedView {

    @Inject
    lateinit var detailedPresenter: DetailedPresenter

    @Arg
    lateinit var appId : String

    lateinit var viewState: DetailedViewState

    val uiAction = BehaviorSubject.create<DetailedAction>()

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
        return (recyclerView.adapter as DetailedAdapter).recyclerViewDetailedActions.mergeWith(uiAction)
    }

//    var initialized = false

    override fun render(viewState: DetailedViewState) {
//        toast(viewState.toString())

        this.viewState = viewState

        if (viewState.themePack != null) {
            (recyclerView.adapter as DetailedAdapter).update(viewState.themePack.themes)
        }

        val type3 = viewState.themePack?.type3

        if (type3 != null && type3.data.isNotEmpty()) {
//            initialized = true
            spinner.visibility = View.VISIBLE
            spinner.list = type3.data.map { Type3ExtensionToString(it) }
            spinner.setSelection(type3.position)
            spinner.selectListener {
                uiAction.onNext(DetailedAction.ChangeType3SpinnerSelection(it))
            }
        } else {
            spinner.visibility = View.GONE
        }


//        log.debug(viewState.toString())
//        textView.text = viewState.number.toString()
    }

}

