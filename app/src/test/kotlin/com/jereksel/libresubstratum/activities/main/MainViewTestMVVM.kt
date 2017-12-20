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

package com.jereksel.libresubstratum.activities.main

import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.databinding.ObservableList
import android.databinding.ViewDataBinding
import android.support.v7.app.AppCompatActivity
import com.jereksel.libresubstratum.BaseRobolectricTest
import com.jereksel.libresubstratum.MockedApp
import com.jereksel.libresubstratum.ResettableLazy
import com.nhaarman.mockito_kotlin.*
import io.reactivex.internal.operators.observable.ObservableLift
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.plugins.RxJavaPlugins.*
import org.assertj.android.api.Assertions.assertThat
import kotlinx.android.synthetic.main.activity_main.*
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.robolectric.Robolectric
import org.robolectric.RuntimeEnvironment
import org.robolectric.android.controller.ActivityController

@Suppress("IllegalIdentifier")
class MainViewTestMVVM: BaseRobolectricTest() {

    lateinit var activityController : ActivityController<MainView>
    lateinit var activity : MainContract.View
    lateinit var presenter: MainContract.Presenter

    var activityCasted by ResettableLazy { activity as AppCompatActivity? }
    var swipeToRefresh by ResettableLazy { activityCasted!!.swiperefresh }
    var recyclerView by ResettableLazy { activityCasted!!.recyclerView }

    lateinit var viewModel: IMainViewViewModel

    lateinit var apps: ObservableList<MainViewModel>
    lateinit var str: ObservableBoolean

    @Before
    fun setup() {
        val app = RuntimeEnvironment.application as MockedApp
        val factory = MockedApp.viewModelFactory
        apps = ObservableArrayList<MainViewModel>()
        str = ObservableBoolean(false)
        viewModel = mock()
        whenever(factory.create(IMainViewViewModel::class.java)).thenReturn(viewModel)
        whenever(viewModel.getAppsObservable()).thenReturn(apps)
        whenever(viewModel.getSwipeToRefreshObservable()).thenReturn(str)
        activityController = Robolectric.buildActivity(MainView::class.java).create().resume()
        activity = activityController.get()
    }

    @Test
    fun `Init is called`() {
        verify(viewModel).init()
    }

    @Test
    @Ignore
    fun `on STR reset is called`() {
        verify(viewModel, never()).reset()
        swipeToRefresh.isRefreshing = true
        swipeToRefresh.isRefreshing = false
        (activity as MainView).binding.executePendingBindings()
        verify(viewModel).reset()
    }

    @Test
    fun `str observableBoolean`() {
        assertThat(swipeToRefresh.isRefreshing).isFalse()
        str.set(true)
        (activity as MainView).binding.executePendingBindings()
        assertThat(swipeToRefresh.isRefreshing).isTrue()
        str.set(false)
        (activity as MainView).binding.executePendingBindings()
        assertThat(swipeToRefresh.isRefreshing).isFalse()
    }

    @After
    fun cleanup() {
        activityCasted?.finish()
        activityController.destroy()
        activityCasted = null
        swipeToRefresh = null
        recyclerView = null
    }


}