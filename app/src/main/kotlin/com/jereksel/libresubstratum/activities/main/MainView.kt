package com.jereksel.libresubstratum.activities.main

import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.jereksel.libresubstratum.App
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.activities.detailed.DetailedView_
import com.jereksel.libresubstratum.activities.installed.InstalledView_
import com.jereksel.libresubstratum.activities.main.MainContract.Presenter
import com.jereksel.libresubstratum.adapters.MainViewAdapter
import com.jereksel.libresubstratum.data.InstalledTheme
import com.jereksel.libresubstratum.extensions.safeUnsubscribe
import kotlinx.android.synthetic.main.activity_main.*
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EActivity
import rx.Subscription
import javax.inject.Inject

@EActivity(R.layout.activity_main)
open class MainView : AppCompatActivity(), MainContract.View {

    @Inject lateinit var presenter: Presenter
    var clickSubscriptions: Subscription? = null
    private var dialog: ProgressDialog? = null

    @AfterViews
    fun init() {
        (application as App).getAppComponent(this).inject(this)
        presenter.setView(this)
        setSupportActionBar(toolbar)
        swiperefresh.isRefreshing = true
        swiperefresh.setOnRefreshListener { presenter.getApplications() }
        presenter.getApplications()
    }

    override fun addApplications(list: List<InstalledTheme>) {
        clickSubscriptions?.safeUnsubscribe()
        with(recyclerView) {
            layoutManager = LinearLayoutManager(this@MainView)
            itemAnimator = DefaultItemAnimator()
            adapter = MainViewAdapter(list)
        }
        clickSubscriptions = (recyclerView.adapter as MainViewAdapter)
                .getClickObservable()
                .subscribe {
                    dialog = ProgressDialog.show(this@MainView, "Extracting", "Extracting theme", true)
                    presenter.openThemeScreen(it.appId)
                }
        swiperefresh.isRefreshing = false
    }

    override fun openThemeFragment(appId: String) {
        dialog?.dismiss()
        DetailedView_.intent(this).appId(appId).start()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                R.id.action_installed -> {
                    // User chose the "Settings" item, show the app settings UI...
                    InstalledView_.intent(this).start()
                    true
                }
                else ->
                    // If we got here, the user's action was not recognized.
                    // Invoke the superclass to handle it.
                    super.onOptionsItemSelected(item)
            }

    override fun onDestroy() {
        super.onDestroy()
        presenter.removeView()
        clickSubscriptions?.safeUnsubscribe()
    }

}
