package com.jereksel.libresubstratum.activities.installed

import android.support.v7.app.AppCompatActivity
import com.jereksel.libresubstratum.App
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.activities.installed.InstalledContract.Presenter
import com.jereksel.libresubstratum.activities.installed.InstalledContract.View
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EActivity
import javax.inject.Inject

@EActivity(R.layout.activity_installed)
open class InstalledView: AppCompatActivity(), View {

    @Inject lateinit var presenter : Presenter

    @AfterViews
    fun init() {
        (application as App).getAppComponent(this).inject(this)
        presenter.getInstalledOverlays()
    }

}