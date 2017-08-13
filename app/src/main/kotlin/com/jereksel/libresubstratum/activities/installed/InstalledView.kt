package com.jereksel.libresubstratum.activities.installed

import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import com.jereksel.libresubstratum.App
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.activities.installed.InstalledContract.Presenter
import com.jereksel.libresubstratum.activities.installed.InstalledContract.View
import com.jereksel.libresubstratum.adapters.InstalledOverlaysAdapter
import com.jereksel.libresubstratum.data.InstalledOverlay
import com.jereksel.libresubstratum.domain.OverlayService
import kotlinx.android.synthetic.main.activity_installed.*
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EActivity
import javax.inject.Inject

@EActivity(R.layout.activity_installed)
open class InstalledView : AppCompatActivity(), View {

    @Inject lateinit var presenter: Presenter

    @AfterViews
    fun init() {
        (application as App).getAppComponent(this).inject(this)
        presenter.setView(this)
        presenter.getInstalledOverlays()
//        swiperefresh.isRefreshing = true
//        swiperefresh.setOnRefreshListener { presenter.getInstalledOverlays() }
    }

    override fun addOverlays(overlays: List<InstalledOverlay>) {
        val adapter_ = InstalledOverlaysAdapter(this, overlays, presenter)
        with(recyclerView) {
            layoutManager = LinearLayoutManager(this@InstalledView)
            itemAnimator = DefaultItemAnimator()
            adapter = adapter_
        }
//        swiperefresh.isRefreshing = false
    }

    override fun showSnackBar(message: String, buttonText: String, callback: () -> Unit) {
        Snackbar.make(recyclerView, message, Snackbar.LENGTH_INDEFINITE)
                .setAction(buttonText, { _ -> callback() }).show()
    }
}