package com.jereksel.libresubstratum.views

import android.content.ComponentName
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.jereksel.libresubstratum.*
import com.jereksel.libresubstratum.activities.detailed.DetailedView
import com.jereksel.libresubstratum.activities.detailed.DetailedViewStarter
import com.jereksel.libresubstratum.activities.installed.InstalledView
import com.jereksel.libresubstratum.activities.main.MainContract
import com.jereksel.libresubstratum.activities.main.MainView
import com.jereksel.libresubstratum.data.InstalledTheme
import com.jereksel.libresubstratum.data.KeyPair
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.kotlintest.mock.`when`
import io.kotlintest.mock.mock
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import kotlinx.android.synthetic.main.activity_main.*
import org.apache.commons.lang3.reflect.FieldUtils.readStaticField
import org.jetbrains.anko.find
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.robolectric.Robolectric
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.android.controller.ActivityController
import org.robolectric.shadows.ShadowDialog
import org.robolectric.shadows.ShadowToast
import java.util.concurrent.FutureTask

@Suppress("IllegalIdentifier")
class MainViewTest2 : BaseRobolectricTest() {

    lateinit var activityController : ActivityController<MainView>
    lateinit var activity : MainContract.View
    lateinit var presenter: MainContract.Presenter

    var activityCasted by ResettableLazy { activity as AppCompatActivity? }
    var swipeToRefresh by ResettableLazy { activityCasted!!.swiperefresh }
    var recyclerView by ResettableLazy { activityCasted!!.recyclerView }

    @Before
    fun setup() {
        RxJavaPlugins.reset()
        val app = RuntimeEnvironment.application as MockedApp
        presenter = MockedApp.mockedMainPresenter
        reset(presenter)
        activityController = Robolectric.buildActivity(MainView::class.java).create().resume()
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
    fun `Lock should be visible if theme key cannot be found`() {

        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }

        val encryptedApp = InstalledTheme("id1", "name1", "author1", false, "2", FutureTask { null })
        whenever(presenter.getKeyPair("id1")).thenReturn(null)
        activity.addApplications(listOf(encryptedApp))

        recyclerView.measure(0, 0);
        recyclerView.layout(0, 0, 100, 10000);

        val view = recyclerView.getChildAt(0).find<ImageView>(R.id.lock)

        assertEquals(view.visibility, View.VISIBLE)
    }

    @Test
    fun `Lock should not be visible if theme is not encrypted`() {

        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }

        val encryptedApp = InstalledTheme("id1", "name1", "author1", false, "2", FutureTask { null })
        whenever(presenter.getKeyPair("id1")).thenReturn(KeyPair.EMPTYKEY)
        activity.addApplications(listOf(encryptedApp))

        recyclerView.measure(0, 0);
        recyclerView.layout(0, 0, 100, 10000);

        val view = recyclerView.getChildAt(0).find<ImageView>(R.id.lock)

        assertEquals(view.visibility, View.GONE)
    }

    @Test
    fun `When clicking on lock toast is shown`() {

        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }

        val encryptedApp = InstalledTheme("id1", "name1", "author1", false, "2", FutureTask { null })
        activity.addApplications(listOf(encryptedApp))

        recyclerView.measure(0, 0);
        recyclerView.layout(0, 0, 100, 10000);

        recyclerView.getChildAt(0).find<ImageView>(R.id.lock).performClick()

        assertEquals(RuntimeEnvironment.application.getString(R.string.unsupported_template_toast), ShadowToast.getTextOfLatestToast())

    }

    @Test
    fun `When waiting for keypair spinner is shown`() {

        val testScheduler = TestScheduler()

        RxJavaPlugins.reset()
        RxJavaPlugins.setComputationSchedulerHandler { testScheduler }
        RxJavaPlugins.setIoSchedulerHandler { testScheduler }

        val encryptedApp = InstalledTheme("id1", "name1", "author1", false, "2", FutureTask { null })
        whenever(presenter.getKeyPair("id1")).thenReturn(KeyPair.EMPTYKEY)
        activity.addApplications(listOf(encryptedApp))

        recyclerView.measure(0, 0);
        recyclerView.layout(0, 0, 100, 10000);

        val view = recyclerView.getChildAt(0).find<ProgressBar>(R.id.progressBar)

        assertEquals(view.visibility, View.VISIBLE)
    }

    @Test
    fun `When keypair exists spinner is hidden`() {

        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }

        val encryptedApp = InstalledTheme("id1", "name1", "author1", false, "2", FutureTask { null })
        whenever(presenter.getKeyPair("id1")).thenReturn(KeyPair.EMPTYKEY)
        activity.addApplications(listOf(encryptedApp))

        recyclerView.measure(0, 0);
        recyclerView.layout(0, 0, 100, 10000);

        val view = recyclerView.getChildAt(0).find<ProgressBar>(R.id.progressBar)

        assertEquals(view.visibility, View.GONE)
    }

    @Test
    fun `dismissDialog() should dismiss any dialog`() {

        val message = "Very important message"

        activity.showUndismissableDialog(message)

        assertTrue(ShadowDialog.getLatestDialog().isShowing)

        activity.dismissDialog()

        assertFalse(ShadowDialog.getLatestDialog().isShowing)
    }

}


