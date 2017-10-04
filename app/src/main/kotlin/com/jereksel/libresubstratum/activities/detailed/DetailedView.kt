package com.jereksel.libresubstratum.activities.detailed

import activitystarter.ActivityStarter
import activitystarter.Arg
import android.app.ProgressDialog
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.Snackbar.LENGTH_LONG
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View.*
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
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
import org.jetbrains.anko.find
import javax.inject.Inject
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View.GONE
import org.jetbrains.anko.toast


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
        fab_compile_install.setOnClickListener { fab.close(true); presenter.compileRunSelected() }
        fab_compile_install_activate.setOnClickListener { fab.close(true); presenter.compileRunActivateSelected() }
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
//        val pDialog = ProgressDialog(this)
//        pDialog.setCancelable(false)
//        pDialog.isIndeterminate = false
//        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
//        pDialog.setTitle("Compiling")
//        pDialog.max = size
        progressBar.progress = 0
        progressBar.max = size
        progressBar.secondaryProgress = Runtime.getRuntime().availableProcessors()
        progressBar.visibility = VISIBLE
//        pDialog.show()
//        dialog = pDialog
    }

    override fun increaseDialogProgress() {
        val dialog = dialog
//        if (dialog != null && dialog.isShowing) {
            runOnUiThread {
//                dialog.progress++
//                progressBar.progress = 25
                progressBar.incrementProgressBy(1)
                progressBar.incrementSecondaryProgressBy(1)
            }
//        }
    }

    override fun showError(errors: List<String>) {
        Snackbar.make(root, "Error occured during compilation", LENGTH_LONG)
                .setAction("Show error", {
                    val view = LayoutInflater.from(it.context).inflate(R.layout.dialog_compilationerror, null)
                    val textView = view.find<TextView>(R.id.errorTextView)
                    val errorText = errors.joinToString(separator = "\n")
                    textView.text = errorText

                    val builder = AlertDialog.Builder(it.context)
                    builder.setTitle("Error")
                    builder.setView(view)

                    builder.setPositiveButton("Copy to clipboard", { _, _ ->
                        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("LibreSubstratum error", errorText)
                        clipboard.primaryClip = clip
                        toast("Copied to clipboard")
                    })

                    builder.show()
                }).show()

//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

    override fun hideCompileDialog() {
        val dialog = dialog
        if (dialog != null && dialog.isShowing) {
            dialog.dismiss()
        }
        progressBar.visibility = GONE
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
