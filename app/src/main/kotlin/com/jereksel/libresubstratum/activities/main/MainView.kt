package com.jereksel.libresubstratum.activities.main

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog.Builder
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.jereksel.changelogdialog.ChangeLogDialog
import com.jereksel.libresubstratum.App
import com.jereksel.libresubstratum.BuildConfig
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.activities.about.AboutActivity
import com.jereksel.libresubstratum.activities.detailed.DetailedViewStarter
import com.jereksel.libresubstratum.activities.installed.InstalledView
import com.jereksel.libresubstratum.activities.main.MainContract.Presenter
import com.jereksel.libresubstratum.activities.priorities.PrioritiesView
import com.jereksel.libresubstratum.adapters.MainViewAdapter
import com.jereksel.libresubstratum.adapters.PrioritiesAdapter
import com.jereksel.libresubstratum.data.Changelog
import com.jereksel.libresubstratum.data.InstalledTheme
import com.jereksel.libresubstratum.extensions.getLogger
import com.jereksel.libresubstratum.extensions.safeDispose
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity
import javax.inject.Inject

open class MainView : AppCompatActivity(), MainContract.View {

    val log = getLogger()

    @Inject lateinit var presenter: Presenter
    var clickSubscriptions: Disposable? = null
    private var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        (application as App).getAppComponent(this).inject(this)
        presenter.setView(this)
        setSupportActionBar(toolbar)
        swiperefresh.isRefreshing = true
        swiperefresh.setOnRefreshListener { presenter.getApplications() }
        presenter.getApplications()

        ChangeLogDialog.show(this, Changelog.changelog, BuildConfig.BETA)
    }

    override fun onResume() {
        super.onResume()
        presenter.checkPermissions()
    }

    override fun addApplications(list: List<InstalledTheme>) {
        clickSubscriptions?.safeDispose()
        with(recyclerView) {
            layoutManager = LinearLayoutManager(this@MainView)
            itemAnimator = DefaultItemAnimator()
            adapter = MainViewAdapter(list, presenter)
        }
        clickSubscriptions = (recyclerView.adapter as MainViewAdapter)
                .getClickObservable()
                .subscribe {
                    log.debug("Opening {}", it)
                    presenter.openThemeScreen(it.appId)
                }
        swiperefresh.isRefreshing = false
    }

    override fun openThemeFragment(appId: String) {
        DetailedViewStarter.start(this, appId)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                R.id.action_installed -> {
                    startActivity<InstalledView>()
                    true
                }
                R.id.action_about -> {
                    startActivity<AboutActivity>()
                    true
                }
                R.id.action_priorities -> {
                    startActivity<PrioritiesView>()
                    true
                }
                else ->
                    super.onOptionsItemSelected(item)
            }

    override fun requestPermissions(perms: List<String>) {
        ActivityCompat.requestPermissions(this, perms.toTypedArray(), 123)
    }

    override fun dismissDialog() {
       if (dialog?.isShowing == true) {
           dialog?.dismiss()
       }
    }

    override fun showUndismissableDialog(message: String) {
        val builder = Builder(this)
        builder.setTitle("Required action")
        builder.setMessage(message)
        builder.setCancelable(false)
        dialog = builder.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.removeView()
        clickSubscriptions?.safeDispose()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 123 && permissions.isNotEmpty()) {
            presenter.checkPermissions()
        }
    }

}
