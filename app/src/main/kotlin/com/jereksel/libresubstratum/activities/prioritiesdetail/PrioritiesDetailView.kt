package com.jereksel.libresubstratum.activities.prioritiesdetail

import activitystarter.ActivityStarter
import activitystarter.Arg
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import com.jereksel.libresubstratum.App
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.activities.prioritiesdetail.PrioritiesDetailContract.*
import com.jereksel.libresubstratum.adapters.PrioritiesDetailAdapter
import com.jereksel.libresubstratum.data.InstalledOverlay
import kotlinx.android.synthetic.main.activity_priorities_detail.*
import javax.inject.Inject
import com.jereksel.libresubstratum.R.id.recyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import com.jereksel.libresubstratum.adapters.PrioritiesDetailItemTouchHelperCallback


class PrioritiesDetailView: AppCompatActivity(), View {

    @Inject lateinit var presenter: Presenter

    @Arg
    lateinit var targetId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_priorities)
        (application as App).getAppComponent(this).inject(this)
        ActivityStarter.fill(this)
        presenter.setView(this)
        presenter.getOverlays(targetId)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun setOverlays(overlays: List<InstalledOverlay>) {

        val adapter_ = PrioritiesDetailAdapter(overlays)

        with(recyclerView) {
            layoutManager = LinearLayoutManager(this@PrioritiesDetailView)
            itemAnimator = DefaultItemAnimator()
            adapter = PrioritiesDetailAdapter(overlays)
        }

        val callback = PrioritiesDetailItemTouchHelperCallback(adapter_)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(recyclerView)

    }

}