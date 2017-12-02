package com.jereksel.libresubstratum.activities.prioritiesdetail

import activitystarter.ActivityStarter
import activitystarter.Arg
import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import com.jereksel.libresubstratum.App
import com.jereksel.libresubstratum.BuildConfig
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.activities.prioritiesdetail.PrioritiesDetailContract.Presenter
import com.jereksel.libresubstratum.activities.prioritiesdetail.PrioritiesDetailContract.View
import com.jereksel.libresubstratum.adapters.PrioritiesDetailAdapter
import com.jereksel.libresubstratum.adapters.PrioritiesDetailItemTouchHelperCallback
import com.jereksel.libresubstratum.data.InstalledOverlay
import kotlinx.android.synthetic.main.activity_priorities_detail.*
import org.jetbrains.anko.toast
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig
import javax.inject.Inject

class PrioritiesDetailView: AppCompatActivity(), View {

    @Inject lateinit var presenter: Presenter

    lateinit var adapter: PrioritiesDetailAdapter

    var dialog: Dialog? = null

    @Arg
    lateinit var targetId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_priorities_detail)
        (application as App).getAppComponent(this).inject(this)
        ActivityStarter.fill(this)
        presenter.setView(this)
        presenter.getOverlays(targetId)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        fab.hide(false)

        fab.setOnClickListener {
            presenter.updatePriorities(adapter.overlays)
        }

    }

    override fun setOverlays(overlays: List<InstalledOverlay>) {

        val adapter = PrioritiesDetailAdapter(overlays, presenter)
        this.adapter = adapter

        with(recyclerView) {
            layoutManager = LinearLayoutManager(this@PrioritiesDetailView)
            itemAnimator = DefaultItemAnimator()
            this.adapter = adapter
        }

        val callback = PrioritiesDetailItemTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        adapter.itemTouchListener = touchHelper
        touchHelper.attachToRecyclerView(recyclerView)


        recyclerView.postDelayed ({
            showTutorial()
        }, 100)

    }

    private fun showTutorial() {

        val child = recyclerView.layoutManager.findViewByPosition(0) ?: return
        val rvRow = recyclerView.getChildViewHolder(child) as PrioritiesDetailAdapter.ViewHolder
        val icon = rvRow.themeIcon
        val card = rvRow.card
        val handle = rvRow.reorder

        val config = ShowcaseConfig()
        config.delay = 500

        val sequence = if(BuildConfig.DEBUG) {
            return
        } else {
            MaterialShowcaseSequence(this, "PrioritiesDetailViewView_1")
        }

        sequence.setConfig(config)

        sequence.apply {
            addSequenceItem(icon, "Long click to open this application. When is split mode, app will be opened in second split", "GOT IT")
            addSequenceItem(card, "Click on card to move it to top of the list", "GOT IT")
            addSequenceItem(handle, "Click on handle to move card", "GOT IT")
        }

        sequence.start()


    }

    override fun showFab() = fab.show(true)

    override fun hideFab() = fab.hide(true)

    override fun notifyPrioritiesChanged() = toast(R.string.priorities_changed)

}