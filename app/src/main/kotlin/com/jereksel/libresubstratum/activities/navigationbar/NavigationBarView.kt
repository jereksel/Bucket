package com.jereksel.libresubstratum.activities.navigationbar

import activitystarter.ActivityStarter
import activitystarter.Arg
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import com.jereksel.libresubstratum.App
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.activities.navigationbar.NavigationBarContract.*
import com.jereksel.libresubstratum.adapters.NavigationBarAdapter
import com.jereksel.libresubstratum.data.NavigationBarOverlay
import kotlinx.android.synthetic.main.activity_navigationbar.*
import org.jetbrains.anko.toast
import javax.inject.Inject

class NavigationBarView: AppCompatActivity(), View {

    @Arg
    lateinit var appId : String

    @Inject lateinit var presenter: Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigationbar)
        (application as App).getAppComponent(this).inject(this)
        ActivityStarter.fill(this)
        presenter.setView(this)
        presenter.getBars(appId)
    }

    override fun show(bars: List<NavigationBarOverlay>) {
        if (bars.isEmpty()) {
            toast("No navbar icons")
        }
        with(recyclerView) {
            layoutManager = LinearLayoutManager(this@NavigationBarView)
            itemAnimator = DefaultItemAnimator()
            adapter = NavigationBarAdapter(bars)
        }
    }
}