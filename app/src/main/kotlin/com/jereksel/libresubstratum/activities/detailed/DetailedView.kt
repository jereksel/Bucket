/*
 * Copyright (C) 2017 Andrzej Ressel (jereksel@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.jereksel.libresubstratum.activities.detailed

import activitystarter.ActivityStarter
import activitystarter.Arg
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
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.jereksel.libresubstratum.App
import com.jereksel.libresubstratum.BuildConfig
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
import com.jereksel.libresubstratum.data.KeyPair
import org.jetbrains.anko.toast
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig
import javax.inject.Inject

open class DetailedView : AppCompatActivity(), View {

    @Arg
    lateinit var appId : String

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
        recyclerView.postDelayed ({
            showTutorial()
        }, 100)
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

    override fun showCompilationProgress(size: Int) {
        progressBar.progress = 0
        progressBar.max = size
        progressBar.secondaryProgress = Runtime.getRuntime().availableProcessors()
        progressBar.visibility = VISIBLE
    }

    override fun increaseDialogProgress() {
        runOnUiThread {
            progressBar.incrementProgressBy(1)
            progressBar.incrementSecondaryProgressBy(1)
        }
    }

    fun showTutorial() {
        val child = recyclerView.layoutManager.findViewByPosition(0) ?: return
        val rvRow = recyclerView.getChildViewHolder(child) as ThemePackAdapter.ViewHolder
        val icon = rvRow.appIcon
        val card = rvRow.card

        val config = ShowcaseConfig()
        config.delay = 500

        val sequence = if(BuildConfig.DEBUG) {
            return
        } else {
            MaterialShowcaseSequence(this, "DetailedView_1")
        }

        sequence.setConfig(config)

        sequence.apply {
            addSequenceItem(icon, "Long click to open this application. When is split mode, app will be opened in second split", "GOT IT")
            addSequenceItem(card, "Click on card to select. Long click to compile and enable overlay", "GOT IT")
        }

        sequence.start()
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
                        presenter.setClipboard(errorText)
                        toast("Copied to clipboard")
                    })

                    builder.show()
                }).show()
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
                R.id.action_restartui -> {
                    presenter.restartSystemUI()
                    true
                }
                else ->
                    super.onOptionsItemSelected(item)
            }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.detailed, menu)
        return true
    }

    override fun hideCompilationProgress() {
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
