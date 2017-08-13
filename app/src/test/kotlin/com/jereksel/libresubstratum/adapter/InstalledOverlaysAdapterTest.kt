package com.jereksel.libresubstratum.adapter

import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import com.jereksel.libresubstratum.BuildConfig
import com.jereksel.libresubstratum.MockedApp
import com.jereksel.libresubstratum.RecViewActivity
import com.jereksel.libresubstratum.ResettableLazy
import com.jereksel.libresubstratum.activities.installed.InstalledContract.Presenter
import com.jereksel.libresubstratum.adapters.InstalledOverlaysAdapter
import com.jereksel.libresubstratum.data.InstalledOverlay
import com.jereksel.libresubstratum.domain.OverlayInfo
import com.nhaarman.mockito_kotlin.verify
import io.kotlintest.mock.`when`
import io.kotlintest.mock.mock
import kotlinx.android.synthetic.main.activity_reconly.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class,
        application = MockedApp::class,
        sdk = intArrayOf(Build.VERSION_CODES.LOLLIPOP))
class InstalledOverlaysAdapterTest {

    lateinit var activityController: ActivityController<RecViewActivity>
    lateinit var activity: AppCompatActivity

    @Mock
    lateinit var presenter: Presenter

    var activityCasted by ResettableLazy { activity as AppCompatActivity? }
    var recyclerView by ResettableLazy { activityCasted!!.recyclerView }

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
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
    fun `toggleOverlay is called after clicking on item`() {

        val apps = listOf(InstalledOverlay("id", "", "", mock(), "", "", mock(), "type1"))

        val adapter_ = InstalledOverlaysAdapter(activity, apps, presenter)

        `when`(presenter.getOverlayInfo("id")).thenReturn(OverlayInfo(false))

        recyclerView.run {
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            adapter = adapter_
            measure(0, 0)
            layout(0, 0, 100, 10000)
        }

        val child = recyclerView.layoutManager.findViewByPosition(0)
        child.performClick()
        verify(presenter).toggleOverlay("id", true)

    }

    @Test
    fun `Text color is red when overlay is disabled and green when enabled`() {

        mapOf(
                "id1" to true,
                "id2" to false
        ).forEach { id, enabled ->

            val color = if(enabled) Color.GREEN else Color.RED

            val apps = listOf(InstalledOverlay(id, "", "", mock(), "", "", mock()))

            val adapter_ = InstalledOverlaysAdapter(activity, apps, presenter)

            `when`(presenter.getOverlayInfo(id)).thenReturn(OverlayInfo(enabled))

            recyclerView.run {
                layoutManager = LinearLayoutManager(context)
                itemAnimator = DefaultItemAnimator()
                adapter = adapter_
                measure(0, 0)
                layout(0, 0, 100, 10000)
            }

            val child = recyclerView.layoutManager.findViewByPosition(0)
            val viewHolder = recyclerView.getChildViewHolder(child) as InstalledOverlaysAdapter.ViewHolder
            assertEquals(color, viewHolder.targetName.currentTextColor)
        }

    }

}
