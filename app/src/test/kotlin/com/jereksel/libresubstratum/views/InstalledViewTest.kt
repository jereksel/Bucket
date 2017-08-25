package com.jereksel.libresubstratum.views

import android.os.Build
import android.support.v7.app.AppCompatActivity
import com.jereksel.libresubstratum.*
import com.jereksel.libresubstratum.activities.installed.InstalledContract.Presenter
import com.jereksel.libresubstratum.activities.installed.InstalledContract.View
import com.jereksel.libresubstratum.activities.installed.InstalledView
import com.nhaarman.mockito_kotlin.verify
import kotlinx.android.synthetic.main.activity_main.*
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class,
        application = MockedApp::class,
        sdk = intArrayOf(Build.VERSION_CODES.LOLLIPOP),
        shadows = arrayOf(ShadowSnackbar::class)
)
class InstalledViewTest: BaseRobolectricTest() {

    lateinit var activityController: ActivityController<InstalledView>
    lateinit var activity: View
    lateinit var presenter: Presenter

    var activityCasted by ResettableLazy { activity as AppCompatActivity? }
    var swipeToRefresh by ResettableLazy { activityCasted!!.swiperefresh }
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
        swipeToRefresh = null
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
    fun `Snackbar is shown after showSnackbar is invoked`() {
        assertNull(ShadowSnackbar.getLatestSnackbar())
        activity.showSnackBar("message", "button", {})
        assertNotNull(ShadowSnackbar.getLatestSnackbar())
        assertEquals("message", ShadowSnackbar.getTextOfLatestSnackbar())
    }

}
