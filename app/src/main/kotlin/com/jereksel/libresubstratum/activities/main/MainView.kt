package com.jereksel.libresubstratum.activities.main

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import com.jereksel.libresubstratum.App
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.activities.detailed.DetailedView_
import com.jereksel.libresubstratum.adapters.MainViewAdapter
import com.jereksel.libresubstratum.data.DetailedApplication
import com.jereksel.libresubstratum.extensions.safeUnsubscribe
import kotlinx.android.synthetic.main.activity_main.*
import rx.Subscription
import javax.inject.Inject

class MainView : AppCompatActivity(), MainContract.View {

    @Inject lateinit var presenter : MainContract.Presenter
    var clickSubscriptions: Subscription? = null
    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        App.getAppComponent(this).inject(this)
//        (application as App).appComponent.inject(this)
        (application as App).getAppComponent(this).inject(this)
        presenter.setView(this)
        swiperefresh.isRefreshing = true
        swiperefresh.setOnRefreshListener { presenter.getApplications() }
        presenter.getApplications()
    }

    override fun addApplications(list: List<DetailedApplication>) {
        clickSubscriptions?.safeUnsubscribe()
        with(recyclerView) {
            layoutManager = LinearLayoutManager(this@MainView)
            itemAnimator = DefaultItemAnimator()
            adapter = MainViewAdapter(list)
        }
        clickSubscriptions = (recyclerView.adapter as MainViewAdapter)
                .getClickObservable()
                .subscribe {
                    dialog = ProgressDialog.show(this@MainView, "Extracting", "Extracting theme", true)
                    presenter.openThemeScreen(it.id)
                }
        swiperefresh.isRefreshing = false
    }

    override fun openThemeFragment(appId: String) {
        dialog?.dismiss()
        DetailedView_.intent(this).appId(appId).start()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.removeView()
        clickSubscriptions?.safeUnsubscribe()
    }

}
