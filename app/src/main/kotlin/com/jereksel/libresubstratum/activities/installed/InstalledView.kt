package com.jereksel.libresubstratum.activities.installed

import android.support.v7.app.AppCompatActivity
import com.jereksel.libresubstratum.App
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.activities.installed.InstalledContract.Presenter
import com.jereksel.libresubstratum.activities.installed.InstalledContract.View
import com.jereksel.libresubstratum.adapters.InstalledAdapter
import com.jereksel.libresubstratum.data.InstalledOverlay
import com.jereksel.libresubstratum.domain.OverlayService
import kotlinx.android.synthetic.main.activity_installed.*
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EActivity
import javax.inject.Inject

@EActivity(R.layout.activity_installed)
open class InstalledView: AppCompatActivity(), View {

    @Inject lateinit var presenter : Presenter
    @Inject lateinit var overlay : OverlayService

    @AfterViews
    fun init() {
        (application as App).getAppComponent(this).inject(this)
        presenter.setView(this)
        presenter.getInstalledOverlays()
    }

    override fun addOverlays(overlays: List<InstalledOverlay>) {
        val adapter = InstalledAdapter(this, overlays, overlay)
        listView.adapter = adapter
    }
}