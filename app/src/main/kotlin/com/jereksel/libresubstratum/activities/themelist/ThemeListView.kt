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

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jereksel.libresubstratum.App
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.activities.detailed.DetailedActivityStarter
import com.jereksel.libresubstratum.databinding.ActivityThemeListBinding
import com.jereksel.libresubstratum.extensions.getLogger
import com.jereksel.libresubstratum.utils.LiveDataUtils.observe
import javax.inject.Inject

open class ThemeListView : Fragment() {

    val log = getLogger()

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private lateinit var binding: ActivityThemeListBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as App).getAppComponent(context).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.activity_theme_list, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewModel = ViewModelProviders.of(this, factory).get(IThemeListViewViewModel::class.java)

        binding.viewModel = viewModel

        binding.recyclerView.adapter = ThemeListViewAdapter(viewModel)

        viewModel.getAppToOpen().observe(this) { appId ->
            if (!appId.isNullOrEmpty()) {
                viewModel.getAppToOpen().postValue(null)
                DetailedActivityStarter.start(context, appId)
            }
        }

        viewModel.init()
    }

}
