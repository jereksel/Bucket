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

import android.arch.lifecycle.MutableLiveData
import android.content.ComponentName
import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.databinding.ObservableList
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.jereksel.libresubstratum.BaseRobolectricTest
import com.jereksel.libresubstratum.MockedApp
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.ResettableLazy
import com.jereksel.libresubstratum.activities.detailed.DetailedView
import com.jereksel.libresubstratum.activities.detailed.DetailedViewStarter
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import kotlinx.android.synthetic.main.activity_main.*
import org.apache.commons.lang3.reflect.FieldUtils.readStaticField
import org.assertj.android.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.anko.find
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.robolectric.Robolectric
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.android.controller.ActivityController
import org.robolectric.shadows.ShadowDialog
import org.robolectric.shadows.ShadowToast

@Suppress("IllegalIdentifier")
class MainViewTest : BaseRobolectricTest() {

    lateinit var activityController : ActivityController<MainView>
    lateinit var activity : AppCompatActivity

    var activityCasted by ResettableLazy { activity as AppCompatActivity? }
    var swipeToRefresh by ResettableLazy { activityCasted!!.swiperefresh }
    var recyclerView by ResettableLazy { activityCasted!!.recyclerView }

    lateinit var viewModel: IMainViewViewModel

    lateinit var apps: ObservableList<MainViewModel>
    lateinit var str: ObservableBoolean
    lateinit var dialogContent: MutableLiveData<String>
    lateinit var permissions: MutableLiveData<List<String>>
    lateinit var appToOpen: MutableLiveData<String>

    @Before
    fun setup() {
        val app = RuntimeEnvironment.application as MockedApp
        val factory = MockedApp.viewModelFactory
        apps = ObservableArrayList<MainViewModel>()
        str = ObservableBoolean(false)
        dialogContent = MutableLiveData()
        viewModel = mock()
        permissions = MutableLiveData()
        appToOpen = MutableLiveData()
        whenever(factory.create(IMainViewViewModel::class.java)).thenReturn(viewModel)
        whenever(viewModel.getAppsObservable()).thenReturn(apps)
        whenever(viewModel.getSwipeToRefreshObservable()).thenReturn(str)
        whenever(viewModel.getDialogContent()).thenReturn(dialogContent)
        whenever(viewModel.getPermissions()).thenReturn(permissions)
        whenever(viewModel.getAppToOpen()).thenReturn(appToOpen)
        activityController = Robolectric.buildActivity(MainView::class.java).create().resume()
        activity = activityController.get()
    }

    @Test
    fun `Init is called`() {
        verify(viewModel).init()
    }

    @Test
    fun `tickChecks is called`() {
        verify(viewModel).tickChecks()
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

    @Test
    fun `Dialog content shows and hides dialog`() {

        ShadowDialog.reset()

        val dialog1 = ShadowDialog.getLatestDialog()
        assertThat(dialog1).isNull()

        dialogContent.postValue("Dialog content")

        val dialog2 = ShadowDialog.getLatestDialog()
        assertThat(dialog2).isNotNull
        assertThat(dialog2).isShowing
        val dialog2Content = dialog2.find<TextView>(android.R.id.message).text.toString()
        assertThat(dialog2Content).isEqualTo("Dialog content")

        dialogContent.postValue("")

        val dialog3 = ShadowDialog.getLatestDialog()
        assertThat(dialog3).isNotShowing

    }

    @Test
    fun `Progress bar is visible when key is not yet available`() {

        apps.add(
                MainViewModel("app1", "Theme 1")
        )

        (activity as MainView).binding.executePendingBindings()

        recyclerView.measure(0, 0);
        recyclerView.layout(0, 0, 100, 10000);

        val lock = recyclerView.getChildAt(0).find<View>(R.id.lock)
        val progressBar = recyclerView.getChildAt(0).find<View>(R.id.progressBar)

        assertThat(lock).isNotVisible
        assertThat(progressBar).isVisible

    }

    @Test
    fun `Lock should be visible if theme key cannot be found`() {

        apps.add(
                MainViewModel("app1", "Theme 1", false)
        )

        (activity as MainView).binding.executePendingBindings()

        recyclerView.measure(0, 0);
        recyclerView.layout(0, 0, 100, 10000);

        val lock = recyclerView.getChildAt(0).find<View>(R.id.lock)
        val progressBar = recyclerView.getChildAt(0).find<View>(R.id.progressBar)

        assertThat(lock).isVisible
        assertThat(progressBar).isNotVisible
    }

    @Test
    fun `Lock should not be visible when theme key is found`() {

        apps.add(
                MainViewModel("app1", "Theme 1", true)
        )

        (activity as MainView).binding.executePendingBindings()

        recyclerView.measure(0, 0);
        recyclerView.layout(0, 0, 100, 10000);

        val lock = recyclerView.getChildAt(0).find<View>(R.id.lock)
        val progressBar = recyclerView.getChildAt(0).find<View>(R.id.progressBar)

        assertThat(lock).isNotVisible
        assertThat(progressBar).isNotVisible

    }

    @Test
    fun `App is opened after pushing id to apptoOpen`() {

        appToOpen.postValue("my.substratum.theme")

        val nextIntent = Shadows.shadowOf(activity as AppCompatActivity).peekNextStartedActivity()
        assertThat(nextIntent.getStringExtra(readStaticField(DetailedViewStarter::class.java, "APP_ID_KEY", true) as String)).isEqualTo("my.substratum.theme")
        assertThat(nextIntent.component).isEqualTo(ComponentName(activity as AppCompatActivity, DetailedView::class.java))
    }

    @Test
    fun `App is is pushed after clicking card`() {

        apps.add(
                MainViewModel("my.substratum.theme", "Theme 1", true)
        )

        (activity as MainView).binding.executePendingBindings()

        recyclerView.measure(0, 0);
        recyclerView.layout(0, 0, 100, 10000);

        recyclerView.getChildAt(0).performClick()

        val nextIntent = Shadows.shadowOf(activity as AppCompatActivity).peekNextStartedActivity()
        assertThat(nextIntent.getStringExtra(readStaticField(DetailedViewStarter::class.java, "APP_ID_KEY", true) as String)).isEqualTo("my.substratum.theme")
        assertThat(nextIntent.component).isEqualTo(ComponentName(activity as AppCompatActivity, DetailedView::class.java))

    }

    @Test
    fun `Toast is shown after clicking on lock`() {

        apps.add(
                MainViewModel("app1", "Theme 1", false)
        )

        (activity as MainView).binding.executePendingBindings()

        recyclerView.measure(0, 0);
        recyclerView.layout(0, 0, 100, 10000);

        recyclerView.getChildAt(0).find<ImageView>(R.id.lock).performClick()

        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo(RuntimeEnvironment.application.getString(R.string.unsupported_template_toast))

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