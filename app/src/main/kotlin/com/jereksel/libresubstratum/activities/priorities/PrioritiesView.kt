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

package com.jereksel.libresubstratum.activities.priorities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import com.jereksel.libresubstratum.App
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.activities.priorities.PrioritiesContract.Presenter
import com.jereksel.libresubstratum.activities.priorities.PrioritiesContract.View
import com.jereksel.libresubstratum.adapters.PrioritiesAdapter
import kotlinx.android.synthetic.main.activity_priorities.*
import javax.inject.Inject

class PrioritiesView: AppCompatActivity(), View {

    @Inject lateinit var presenter: Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_priorities)
        (application as App).getAppComponent(this).inject(this)
        presenter.setView(this)
        presenter.getApplication()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun addApplications(applications: List<String>) {

        with(recyclerView) {
            layoutManager = LinearLayoutManager(this@PrioritiesView)
            itemAnimator = DefaultItemAnimator()
            adapter = PrioritiesAdapter(presenter, applications)
        }

    }
}