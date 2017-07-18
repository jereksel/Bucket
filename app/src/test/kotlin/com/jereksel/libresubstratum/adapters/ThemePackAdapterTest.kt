package com.jereksel.libresubstratum.adapters

import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import com.jereksel.libresubstratum.BuildConfig
import com.jereksel.libresubstratum.MockedApp
import com.jereksel.libresubstratum.RecViewActivity
import com.jereksel.libresubstratum.ResettableLazy
import com.jereksel.libresubstratumlib.Theme
import com.jereksel.libresubstratumlib.ThemePack
import io.kotlintest.mock.mock
import kotlinx.android.synthetic.main.activity_reconly.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.robolectric.Robolectric
import org.robolectric.RuntimeEnvironment
import org.robolectric.android.controller.ActivityController
import org.assertj.core.api.Assertions.*
import org.assertj.android.api.Assertions.assertThat
import org.assertj.android.recyclerview.v7.api.Assertions.assertThat
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class,
        application = MockedApp::class,
        sdk = intArrayOf(Build.VERSION_CODES.LOLLIPOP))
class ThemePackAdapterTest {

    lateinit var activityController : ActivityController<RecViewActivity>
    lateinit var activity : AppCompatActivity

    var activityCasted by ResettableLazy { activity as AppCompatActivity? }
    var recyclerView by ResettableLazy { activityCasted!!.recyclerView }

    @Before
    fun setup() {
        val app = RuntimeEnvironment.application as MockedApp
        activityController = Robolectric.buildActivity(RecViewActivity::class.java).create()
        activity = activityController.get()
    }

    @After
    fun cleanup() {
//        activityController.destroy()
        activityCasted = null
        recyclerView = null
    }

    @Test
    fun `Theme without extensions shouldn't have spinners`() {
        val theme = ThemePack(listOf(Theme("app1")))
        val adapter_ = ThemePackAdapter(theme, mock())
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            adapter = adapter_
            measure(0, 0)
            layout(0, 0, 100, 10000)
        }
        assertThat(adapter_).hasItemCount(1)
        val child = recyclerView.layoutManager.findViewByPosition(0)
        val viewHolder = recyclerView.getChildViewHolder(child) as ThemePackAdapter.ViewHolder
        viewHolder.type1Spinners.forEach { assertThat(it).isGone }
    }
}