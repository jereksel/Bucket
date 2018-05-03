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

package com.jereksel.libresubstratum.activities.themelist

import android.app.Dialog
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jereksel.libresubstratum.App
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.activities.detailed.DetailedActivityStarter
import com.jereksel.libresubstratum.databinding.ActivityThemeListBinding
import com.jereksel.libresubstratum.extensions.getLogger
import com.jereksel.libresubstratum.utils.LiveDataUtils.observe
import com.jereksel.libresubstratum.utils.ViewModelUtils.get
import kotlinx.android.synthetic.main.activity_theme_list.*
import kotlinx.android.synthetic.main.activity_theme_list.view.*
import java.lang.ref.WeakReference
import javax.inject.Inject

open class ThemeListView : Fragment() {

    val log = getLogger()

//    @Inject lateinit var factory: ViewModelProvider.Factory

//    lateinit var viewModel: IThemeListViewViewModel

    lateinit var binding: ActivityThemeListBinding

    private var dialog: Dialog? = null

    lateinit var provider: WeakReference<ThemeListViewViewModelProvider>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        provider = WeakReference(context as ThemeListViewViewModelProvider)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.activity_theme_list, container, false);

        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(this@ThemeListView.activity)
            itemAnimator = DefaultItemAnimator()
//            adapter = ThemeListViewAdapter(viewModel)
        }

        return binding.root
    }

//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        binding = DataBindingUtil.inflate(
//                inflater, R.layout.activity_theme_list, container, false);

//        val view = binding.root

        val viewModelProvider = provider.get() ?: return

        val viewModel = viewModelProvider.getThemeListViewViewModel()

//        val view = inflater.inflate(R.layout.activity_theme_list, container, false)
//
//        binding = DataBindingUtil.findBinding(view)!!

        binding.viewModel = viewModel

        binding.recyclerView.adapter = ThemeListViewAdapter(viewModel)

//        with(view.recyclerView) {
//            layoutManager = LinearLayoutManager(this@ThemeListView.activity)
//            itemAnimator = DefaultItemAnimator()
//            adapter = ThemeListViewAdapter(viewModel)
//        }
//
//        viewModel.getDialogContent().observe(this) { message ->
//            dismissDialog()
//            if (!message.isNullOrEmpty()) {
//                showUndismissableDialog(message!!)
//            }
//        }

//        viewModel.getPermissions().observe(this) { permissions ->
//            if (permissions == null || permissions.isEmpty()) {
//                return@observe
//            }
//
//            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), 123)
//
//        }

        viewModel.getAppToOpen().observe(this) { appId ->
            if (!appId.isNullOrEmpty()) {
                viewModel.getAppToOpen().postValue(null)
                DetailedActivityStarter.start(context, appId)
            }
        }

        viewModel.init()

//        return view
    }

    override fun onDetach() {
        super.onDetach()
        provider.clear()
    }

/*

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as App).getAppComponent(this).inject(this)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setSupportActionBar(toolbar)

        viewModel = ViewModelProviders.of(this, factory).get()

        binding.viewModel = viewModel

        with(recyclerView) {
            layoutManager = LinearLayoutManager(this@MainView)
            itemAnimator = DefaultItemAnimator()
            adapter = ThemeListViewAdapter(viewModel)
        }

        viewModel.getDialogContent().observe(this) { message ->
            dismissDialog()
            if (!message.isNullOrEmpty()) {
                showUndismissableDialog(message!!)
            }
        }

        viewModel.getPermissions().observe(this) { permissions ->
            if (permissions == null || permissions.isEmpty()) {
                return@observe
            }

            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), 123)

        }

        viewModel.getAppToOpen().observe(this) { appId ->
            if (!appId.isNullOrEmpty()) {
                viewModel.getAppToOpen().postValue(null)
                DetailedActivityStarter.start(this, appId)
            }
        }

        viewModel.init()

        ChangeLogDialog.show(this, Changelog.changelog)
    }

    override fun onResume() {
        super.onResume()
        async(UI) {
            viewModel.tickChecks()
        }
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
                    startActivity<AboutFragment>()
                    true
                }
                R.id.action_priorities -> {
                    startActivity<PrioritiesView>()
                    true
                }
                else ->
                    super.onOptionsItemSelected(item)
            }

    private fun dismissDialog() {
        if (dialog?.isShowing == true) {
            dialog?.dismiss()
        }
    }

    private fun showUndismissableDialog(message: String) {
        val builder = Builder(this)
        builder.setTitle("Required action")
        builder.setMessage(message)
        builder.setCancelable(false)
        dialog = builder.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissDialog()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 123 && permissions.isNotEmpty()) {
            async(UI) {
                viewModel.tickChecks()
            }
        }
    }
*/

}

