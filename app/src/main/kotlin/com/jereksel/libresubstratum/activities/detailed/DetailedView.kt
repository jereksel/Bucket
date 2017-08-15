package com.jereksel.libresubstratum.activities.detailed

import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.View.VISIBLE
import com.jereksel.libresubstratum.App
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.activities.detailed.DetailedContract.Presenter
import com.jereksel.libresubstratum.activities.detailed.DetailedContract.View
import com.jereksel.libresubstratum.adapters.ThemePackAdapter
import com.jereksel.libresubstratum.data.Type3ExtensionToString
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.extensions.list
import com.jereksel.libresubstratumlib.ThemePack
import kotlinx.android.synthetic.main.activity_detailed.*
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EActivity
import org.androidannotations.annotations.Extra
import javax.inject.Inject

@EActivity(R.layout.activity_detailed)
open class DetailedView : AppCompatActivity(), View {

    @Extra
    lateinit var appId : String

    @Inject lateinit var presenter : Presenter
    @Inject lateinit var pManager : IPackageManager

    @AfterViews
    fun init() {
        (application as App).getAppComponent(this).inject(this)
//        imageView.setImageDrawable(pManager.getHeroImage(appId))
        title = presenter.getAppName(appId)
        presenter.setView(this)
        presenter.readTheme(appId)
    }

    override fun addThemes(themePack: ThemePack) {
        with(recyclerView) {
            layoutManager = LinearLayoutManager(this@DetailedView)
            itemAnimator = DefaultItemAnimator()
            adapter = ThemePackAdapter(themePack, pManager)
        }
        val type3 = themePack.type3
        if (type3 != null) {
            spinner.visibility = VISIBLE
            spinner.list = type3.extensions.map { Type3ExtensionToString(it) }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.removeView()
    }

}
