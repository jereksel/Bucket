package com.jereksel.libresubstratum.activities.priorities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import com.jereksel.libresubstratum.App
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.activities.priorities.PrioritiesContract.Presenter
import com.jereksel.libresubstratum.activities.priorities.PrioritiesContract.View
import com.jereksel.libresubstratum.adapters.PrioritiesAdapter
import kotlinx.android.synthetic.main.activity_priorities.*
import javax.inject.Inject

class PrioritiesView: AppCompatActivity(), View {

    @Inject lateinit var presenter: Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_priorities)
        (application as App).getAppComponent(this).inject(this)
        presenter.setView(this)
        presenter.getApplication()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun addApplications(applications: List<String>) {

        with(recyclerView) {
            layoutManager = LinearLayoutManager(this@PrioritiesView)
            itemAnimator = DefaultItemAnimator()
            adapter = PrioritiesAdapter(presenter, applications)
        }

    }
}