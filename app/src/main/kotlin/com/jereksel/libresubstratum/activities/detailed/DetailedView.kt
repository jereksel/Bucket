package com.jereksel.libresubstratum.activities.detailed

import activitystarter.ActivityStarter
import activitystarter.Arg
import android.app.ProgressDialog
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import com.jereksel.libresubstratum.App
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.activities.detailed.DetailedContract.Presenter
import com.jereksel.libresubstratum.activities.detailed.DetailedContract.View
import com.jereksel.libresubstratum.adapters.ThemePackAdapter
import com.jereksel.libresubstratum.data.Type3ExtensionToString
import com.jereksel.libresubstratum.extensions.list
import com.jereksel.libresubstratumlib.ThemePack
import com.jereksel.libresubstratumlib.Type3Extension
import kotlinx.android.synthetic.main.activity_detailed.*
import javax.inject.Inject

open class DetailedView : AppCompatActivity(), View {

    @Arg
    lateinit var appId : String

    private var dialog: ProgressDialog? = null

    @Inject lateinit var presenter : Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed)
        ActivityStarter.fill(this)
        (application as App).getAppComponent(this).inject(this)
        presenter = (lastCustomNonConfigurationInstance ?: presenter) as Presenter
        title = presenter.getAppName(appId)
        presenter.setView(this)
        presenter.readTheme(appId)
    }

    override fun addThemes(themePack: ThemePack) {
        with(recyclerView) {
            layoutManager = LinearLayoutManager(this@DetailedView)
            itemAnimator = DefaultItemAnimator()
            adapter = ThemePackAdapter(presenter)
        }
        val type3 = themePack.type3
        if (type3 != null) {
            spinner.visibility = VISIBLE
            spinner.list = type3.extensions.map(::Type3ExtensionToString)
            spinner.selectListener {
                presenter.setType3(it)
                recyclerView.adapter.notifyDataSetChanged()
            }
        }
        fab_compile_install.setOnClickListener { presenter.compileRunSelected() }
        fab_compile_install_activate.setOnClickListener { presenter.compileRunActivateSelected() }
    }

    override fun refreshHolder(position: Int) {
        recyclerView.post { recyclerView.adapter.notifyItemChanged(position, "") }
    }

    override fun showToast(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show()
    }

    override fun showSnackBar(message: String, buttonText: String, callback: () -> Unit) {
        Snackbar.make(recyclerView, message, Snackbar.LENGTH_LONG)
                .setAction(buttonText, { _ -> callback() }).show()
    }

    override fun showCompileDialog(size: Int) {
        val pDialog = ProgressDialog(this)
        pDialog.setCancelable(false)
        pDialog.isIndeterminate = false
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        pDialog.setTitle("Compiling")
        pDialog.max = size
        pDialog.show()
        dialog = pDialog
    }

    override fun increaseDialogProgress() {
        val dialog = dialog
        if (dialog != null && dialog.isShowing) {
            runOnUiThread {
                dialog.progress++
            }
        }
    }

    override fun hideCompileDialog() {
        val dialog = dialog
        if (dialog != null && dialog.isShowing) {
            dialog.dismiss()
        }
        recyclerView.adapter?.notifyDataSetChanged()
    }


    override fun onDestroy() {
        super.onDestroy()
        presenter.removeView()
    }

    override fun onRetainCustomNonConfigurationInstance() = presenter

    private fun Spinner.selectListener(fn: (Type3Extension) -> Unit) {

        this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit

            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                fn((parent.getItemAtPosition(position) as Type3ExtensionToString).type3)
            }
        }
    }

}
