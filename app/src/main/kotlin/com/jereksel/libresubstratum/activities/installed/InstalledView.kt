package com.jereksel.libresubstratum.activities.installed

import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.jereksel.libresubstratum.App
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.activities.installed.InstalledContract.Presenter
import com.jereksel.libresubstratum.activities.installed.InstalledContract.View
import com.jereksel.libresubstratum.adapters.InstalledOverlaysAdapter
import com.jereksel.libresubstratum.data.InstalledOverlay
import kotlinx.android.synthetic.main.activity_installed.*
import org.jetbrains.anko.toast
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
//        fab.setOnLongClickListener {
//            toast("TEST")
//            true
//        }
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

    override fun refreshRecyclerView() = recyclerView.adapter.notifyDataSetChanged()

    override fun showSnackBar(message: String, buttonText: String, callback: () -> Unit) {
        Snackbar.make(recyclerView, message, Snackbar.LENGTH_LONG)
                .setAction(buttonText, { _ -> callback() }).show()
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                R.id.action_selectall -> {
                    presenter.selectAll()
                    true
                }
                R.id.action_deselectall -> {
                    presenter.deselectAll()
                    true
                }
                else ->
                    super.onOptionsItemSelected(item)
            }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.installed, menu)
        return true
    }

    override fun onRetainCustomNonConfigurationInstance() = presenter

}