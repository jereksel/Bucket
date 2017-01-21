package com.jereksel.libresubstratum.activities.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import com.jereksel.libresubstratum.App
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.adapters.MainViewAdapter
import com.jereksel.libresubstratum.data.DetailedApplication
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainView : AppCompatActivity(), IMainView {

    @Inject lateinit var presenter : MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        App.getAppComponent(this).inject(this)
//        (application as App).appComponent.inject(this)
        presenter.setView(this)
        presenter.getApplications()
    }

    override fun addApplications(list: List<DetailedApplication>) {
        val adapter = MainViewAdapter(list)
        val mLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.removeView()
    }
}
