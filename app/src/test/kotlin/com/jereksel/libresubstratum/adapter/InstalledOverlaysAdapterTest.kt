package com.jereksel.libresubstratum.adapter

import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.jereksel.libresubstratum.*
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
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowToast

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class,
        application = MockedApp::class,
        sdk = intArrayOf(Build.VERSION_CODES.LOLLIPOP))
class InstalledOverlaysAdapterTest: BaseRobolectricTest() {

    lateinit var activityController: ActivityController<RecViewActivity>
    lateinit var activity: AppCompatActivity

    @Mock
    lateinit var presenter: Presenter

    var activityCasted by ResettableLazy { activity as AppCompatActivity? }
    var recyclerView by ResettableLazy { activityCasted!!.recyclerView }

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        activityController = Robolectric.buildActivity(RecViewActivity::class.java).create()
        activity = activityController.get()
    }

    @After
    fun cleanup() {
        activityCasted?.finish()
//        activityController.destroy()
//        activityController.pause().stop().destroy()
        activityCasted = null
        recyclerView = null
    }

    @Test
    fun `toggleOverlay is called after long clicking on item`() {

        val apps = listOf(InstalledOverlay("id", "", "", mock(), "", "", mock(), "type1"))

        val adapter_ = InstalledOverlaysAdapter(apps, presenter)

        `when`(presenter.getOverlayInfo("id")).thenReturn(OverlayInfo("id", false))

        recyclerView.run {
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            adapter = adapter_
            measure(0, 0)
            layout(0, 0, 100, 10000)
        }

        val child = recyclerView.layoutManager.findViewByPosition(0)
        child.performLongClick()
        verify(presenter).toggleOverlay("id", true)

    }

    @Test
    fun `openActivity is called after long clicking on image`() {

        val apps = listOf(InstalledOverlay("id", "", "", mock(), "targetid", "", mock(), "type1"))

        val adapter_ = InstalledOverlaysAdapter(apps, presenter)

        `when`(presenter.getOverlayInfo("id")).thenReturn(OverlayInfo("id", false))

        recyclerView.run {
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            adapter = adapter_
            measure(0, 0)
            layout(0, 0, 100, 10000)
        }

        val child = recyclerView.layoutManager.findViewByPosition(0).findViewById<View>(R.id.theme_icon)
        child.performLongClick()
        verify(presenter).openActivity("targetid")

    }

    @Test
    fun `Toast is shown when openActivity was unsuccessful`() {

        val apps = listOf(InstalledOverlay("id", "", "", mock(), "targetid", "", mock(), "type1"))

        val adapter_ = InstalledOverlaysAdapter(apps, presenter)

        `when`(presenter.getOverlayInfo("id")).thenReturn(OverlayInfo("id", false))
        `when`(presenter.openActivity("targetid")).thenReturn(false)

        recyclerView.run {
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            adapter = adapter_
            measure(0, 0)
            layout(0, 0, 100, 10000)
        }

        val child = recyclerView.layoutManager.findViewByPosition(0).findViewById<View>(R.id.theme_icon)
        child.performLongClick()
        verify(presenter).openActivity("targetid")
        assertNotNull(ShadowToast.getLatestToast())
        assertEquals(1, ShadowToast.shownToastCount())

    }

    @Test
    fun `Text color is red when overlay is disabled and green when enabled`() {

        mapOf(
                "id1" to true,
                "id2" to false
        ).forEach { id, enabled ->

            val color = if(enabled) Color.GREEN else Color.RED

            val apps = listOf(InstalledOverlay(id, "", "", mock(), "", "", mock()))

            val adapter_ = InstalledOverlaysAdapter(apps, presenter)

            `when`(presenter.getOverlayInfo(id)).thenReturn(OverlayInfo(id, enabled))

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
