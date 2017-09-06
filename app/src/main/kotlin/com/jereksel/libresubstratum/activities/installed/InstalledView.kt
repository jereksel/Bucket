package com.jereksel.libresubstratum.activities.installed

import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.jereksel.libresubstratum.App
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.activities.installed.InstalledContract.Presenter
import com.jereksel.libresubstratum.activities.installed.InstalledContract.View
import com.jereksel.libresubstratum.adapters.InstalledOverlaysAdapter
import com.jereksel.libresubstratum.data.InstalledOverlay
import kotlinx.android.synthetic.main.activity_installed.*
import javax.inject.Inject

open class InstalledView : AppCompatActivity(), View {

    @Inject lateinit var presenter: Presenter
    val mLayoutManager by lazy { LinearLayoutManager(this@InstalledView) }
    var layoutState: Parcelable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_installed)
        (application as App).getAppComponent(this).inject(this)
        presenter = (lastCustomNonConfigurationInstance ?: presenter) as Presenter
        presenter.setView(this)
        presenter.getInstalledOverlays()
        fab_uninstall.setOnClickListener { fab.close(true); presenter.uninstallSelected() }
        fab_enable.setOnClickListener { fab.close(true); presenter.enableSelected() }
        fab_disable.setOnClickListener { fab.close(true); presenter.disableSelected() }
    }

    override fun addOverlays(overlays: List<InstalledOverlay>) {
        mLayoutManager.onRestoreInstanceState(layoutState)
        with(recyclerView) {
            layoutManager = mLayoutManager
            itemAnimator = DefaultItemAnimator()
            adapter = InstalledOverlaysAdapter(overlays, presenter)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("STATE", mLayoutManager.onSaveInstanceState())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        layoutState = savedInstanceState.getParcelable("STATE")
    }

    override fun hideRecyclerView() {
        (recyclerView.adapter as InstalledOverlaysAdapter).destroy()
        recyclerView.adapter = null
        Toast.makeText(this, "Removing all", Toast.LENGTH_SHORT).show()
    }

    override fun showRecyclerView() {
        Toast.makeText(this, "Removing completed", Toast.LENGTH_SHORT).show()
        presenter.getInstalledOverlays()
        finish()
    }

    override fun refreshRecyclerView() = recyclerView.adapter.notifyDataSetChanged()

    override fun showSnackBar(message: String, buttonText: String, callback: () -> Unit) {
        Snackbar.make(recyclerView, message, Snackbar.LENGTH_LONG)
                .setAction(buttonText, { _ -> callback() }).show()
    }

    override fun onRetainCustomNonConfigurationInstance() = presenter

}