package com.jereksel.libresubstratum.activities.detailed2

import android.os.Bundle
import com.hannesdorfmann.mosby3.mvi.MviActivity
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.extensions.getLogger
import kotlinx.android.synthetic.main.activity_detailed2.*

class DetailedActivity: MviActivity<DetailedView, DetailedPresenter>(), DetailedView {

    val log = getLogger()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed2)
    }

    override fun createPresenter(): DetailedPresenter {
        return DetailedPresenter()
    }

    override fun render(viewState: DetailedViewState) {
        log.debug(viewState.toString())
        textView.text = viewState.number.toString()
    }

}

