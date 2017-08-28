package com.jereksel.libresubstratum.activities.detailed

import activitystarter.ActivityStarter
import activitystarter.Arg
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import com.jereksel.libresubstratum.App
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.adapters.ThemePackAdapter
import com.jereksel.libresubstratumlib.ThemePack
import com.jereksel.libresubstratum.domain.IPackageManager
import kotlinx.android.synthetic.main.activity_detailed.*
import javax.inject.Inject

open class DetailedView : AppCompatActivity(), DetailedContract.View {

    @Arg
    lateinit var appId : String

    @Inject lateinit var presenter : DetailedContract.Presenter
    @Inject lateinit var pManager : IPackageManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed)
        ActivityStarter.fill(this)
        (application as App).getAppComponent(this).inject(this)
//        imageView.setImageDrawable(pManager.getHeroImage(appId))
        presenter.setView(this)
        presenter.readTheme(appId)
    }

    override fun addThemes(themePack: ThemePack) {
        with(recyclerView) {
            layoutManager = GridLayoutManager(this@DetailedView, 5)
            itemAnimator = DefaultItemAnimator()
            adapter = ThemePackAdapter(themePack, pManager)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.removeView()
    }

}
