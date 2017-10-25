package com.jereksel.libresubstratum.views

import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.jereksel.libresubstratum.*
import com.jereksel.libresubstratum.activities.installed.InstalledContract.Presenter
import com.jereksel.libresubstratum.activities.installed.InstalledContract.View
import com.jereksel.libresubstratum.activities.installed.InstalledView
import com.jereksel.libresubstratum.data.InstalledOverlay
import com.jereksel.libresubstratum.domain.OverlayInfo
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.kotlintest.mock.mock
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_installed.*
import org.jetbrains.anko.find
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config
import org.robolectric.fakes.RoboMenuItem

@Suppress("IllegalIdentifier")
@Config(
        shadows = arrayOf(ShadowSnackbar::class)
)
class InstalledViewTest: BaseRobolectricTest() {

    lateinit var activityController: ActivityController<InstalledView>
    lateinit var activity: View
    lateinit var presenter: Presenter

    var activityCasted by ResettableLazy { activity as AppCompatActivity? }
//    var swipeToRefresh by ResettableLazy { activityCasted!!.swiperefresh }
    var recyclerView by ResettableLazy { activityCasted!!.recyclerView }

    @Before
    fun setup() {
        val app = RuntimeEnvironment.application as MockedApp
        presenter = app.mockedInstalledPresenter
        activityController = Robolectric.buildActivity(InstalledView::class.java).create()
        activity = activityController.get()
    }

    @After
    fun cleanup() {
        activityCasted?.finish()
        activityController.destroy()
        activityCasted = null
//        swipeToRefresh = null
        recyclerView = null
    }

    @Test
    fun `getInstalledOverlays() in invoked after creating activity`() {
        verify(presenter).getInstalledOverlays()
    }

    @Test
    fun `Recyclerview should be empty after activity is created`() {
        assertNull(recyclerView.adapter)
    }

    @Test
    fun `Recyclerview should have items after addOverlays is invoked`() {
        activity.addOverlays(listOf())
        assertNotNull(recyclerView.adapter)
    }

    @Test
    fun `After click on card checkbox is toggled`() {

        activity.addOverlays(
                listOf(
                    InstalledOverlay("appid", "", "", mock(), "", "", mock())
                )
        )

        whenever(presenter.getOverlayInfo("appid")).thenReturn(OverlayInfo("appid", true))

        recyclerView.measure(0, 0)
        recyclerView.layout(0, 0, 100, 10000)

        assertEquals(1, recyclerView.childCount)
        assertEquals(false, (recyclerView.getChildAt(0).find<CheckBox>(R.id.checkbox).isChecked))

        recyclerView.getChildAt(0).performClick()

        assertEquals(true, (recyclerView.getChildAt(0).find<CheckBox>(R.id.checkbox).isChecked))

    }

    @Test
    fun `Name is green when overlay is enabled and red when it's disabled`() {

        activity.addOverlays(
                listOf(
                        InstalledOverlay("appid1", "", "", mock(), "", "", mock()),
                        InstalledOverlay("appid2", "", "", mock(), "", "", mock())
                )
        )

        whenever(presenter.getOverlayInfo("appid1")).thenReturn(OverlayInfo("appid1", true))
        whenever(presenter.getOverlayInfo("appid2")).thenReturn(OverlayInfo("appid2", false))

        recyclerView.measure(0, 0)
        recyclerView.layout(0, 0, 100, 10000)

        assertEquals(2, recyclerView.childCount)

        assertEquals(Color.GREEN, (recyclerView.getChildAt(0).find<TextView>(R.id.target_name).currentTextColor))
        assertEquals(Color.RED, (recyclerView.getChildAt(1).find<TextView>(R.id.target_name).currentTextColor))

    }

    @Test
    fun `After checkbox click state in presenter is changed`() {

        activity.addOverlays(
                listOf(
                        InstalledOverlay("appid1", "", "", mock(), "", "", mock()),
                        InstalledOverlay("appid2", "", "", mock(), "", "", mock())
                )
        )

        whenever(presenter.getOverlayInfo("appid1")).thenReturn(OverlayInfo("appid1", true))
        whenever(presenter.getOverlayInfo("appid2")).thenReturn(OverlayInfo("appid2", false))

        recyclerView.measure(0, 0)
        recyclerView.layout(0, 0, 100, 10000)

        assertEquals(2, recyclerView.childCount)

        recyclerView.getChildAt(0).performClick()

        verify(presenter).setState(0, true)

    }

    @Test
    fun `Getstate determine if checkbox is checked`() {

        activity.addOverlays(
                listOf(
                        InstalledOverlay("appid1", "", "", mock(), "", "", mock()),
                        InstalledOverlay("appid2", "", "", mock(), "", "", mock())
                )
        )

        whenever(presenter.getState(0)).thenReturn(true)
        whenever(presenter.getState(1)).thenReturn(false)
        whenever(presenter.getOverlayInfo("appid1")).thenReturn(OverlayInfo("appid1", true))
        whenever(presenter.getOverlayInfo("appid2")).thenReturn(OverlayInfo("appid2", false))

        recyclerView.measure(0, 0)
        recyclerView.layout(0, 0, 100, 10000)

        assertEquals(2, recyclerView.childCount)

        assertEquals(true, (recyclerView.getChildAt(0).find<CheckBox>(R.id.checkbox).isChecked))
        assertEquals(false, (recyclerView.getChildAt(1).find<CheckBox>(R.id.checkbox).isChecked))
    }

    @Test
    fun `Fab click test`() {

        activityCasted!!.fab_enable.callOnClick()
        verify(presenter).enableSelected()

        reset(presenter)

        activityCasted!!.fab_disable.callOnClick()
        verify(presenter).disableSelected()

        reset(presenter)

        activityCasted!!.fab_uninstall.callOnClick()
        verify(presenter).uninstallSelected()

        reset(presenter)
    }

    @Test
    fun `Snackbar is shown after showSnackbar is invoked`() {
        activity.showSnackBar("message", "button", {})
        assertNotNull(ShadowSnackbar.getLatestSnackbar())
        assertEquals("message", ShadowSnackbar.getTextOfLatestSnackbar())
    }

    @Test
    fun `Clicking on selectAll invokes it in presenter`() {
        activityCasted?.onOptionsItemSelected(RoboMenuItem(R.id.action_selectall))
        verify(presenter).selectAll()
    }

    @Test
    fun `Clicking on unselectAll invokes it in presenter`() {
        activityCasted?.onOptionsItemSelected(RoboMenuItem(R.id.action_deselectall))
        verify(presenter).deselectAll()
    }

}
