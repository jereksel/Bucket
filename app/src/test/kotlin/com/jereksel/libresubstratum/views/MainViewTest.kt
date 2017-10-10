package com.jereksel.libresubstratum.views

import android.content.ComponentName
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.jereksel.libresubstratum.*
import com.jereksel.libresubstratum.activities.detailed.DetailedView
import com.jereksel.libresubstratum.activities.detailed.DetailedViewStarter
import com.jereksel.libresubstratum.activities.installed.InstalledView
import com.jereksel.libresubstratum.activities.main.MainContract
import com.jereksel.libresubstratum.activities.main.MainView
import com.jereksel.libresubstratum.data.InstalledTheme
import com.jereksel.libresubstratum.data.MainViewTheme
import com.nhaarman.mockito_kotlin.verify
import io.kotlintest.mock.`when`
import io.kotlintest.mock.mock
import kotlinx.android.synthetic.main.activity_main.*
import org.apache.commons.lang3.reflect.FieldUtils.readStaticField
import org.jetbrains.anko.find
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config
import org.robolectric.shadow.api.Shadow
import org.robolectric.shadows.ShadowActivity
import org.robolectric.shadows.ShadowApplication
import org.robolectric.shadows.ShadowDialog
import org.robolectric.shadows.ShadowToast
import java.util.concurrent.FutureTask

@Suppress("IllegalIdentifier")
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class,
        application = MockedApp::class,
        sdk = intArrayOf(Build.VERSION_CODES.LOLLIPOP))
class MainViewTest: BaseRobolectricTest() {

    lateinit var activityController : ActivityController<MainView>
    lateinit var activity : MainContract.View
    lateinit var presenter: MainContract.Presenter

    var activityCasted by ResettableLazy { activity as AppCompatActivity? }
    var swipeToRefresh by ResettableLazy { activityCasted!!.swiperefresh }
    var recyclerView by ResettableLazy { activityCasted!!.recyclerView }

    @Before
    fun setup() {
        val app = RuntimeEnvironment.application as MockedApp
        presenter = app.mockedMainPresenter
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
    fun `SwipeToRefresh should be active after opening activity`() {
        assertTrue(swipeToRefresh.isRefreshing)
    }

    @Test
    fun `getApplication() should be invoked after opening activity`() {
        verify(presenter).getApplications()
    }

    @Test
    fun `checkPermissions() should be invoked after opening activity`() {
        verify(presenter).checkPermissions()
    }

    @Test
    fun `SwipeToRefresh should be unactive after returning themes`() {
        activity.addApplications(mutableListOf())
        assertFalse(swipeToRefresh.isRefreshing)
    }

    //TODO: Add message that there ara no themes
    @Test
    fun `RecyclerView should be empty when no themes are returned`() {
        activity.addApplications(mutableListOf())
        assertEquals(0, recyclerView.adapter.itemCount)
    }

    @Test
    fun `RecyclerView should show returned themes`() {
        val apps = mutableListOf(
                MainViewTheme("id1", "name1", "author1", null, false),
                MainViewTheme("id2", "name2", "author2", null, false)
        )

        activity.addApplications(apps)
        assertEquals(2, recyclerView.adapter.itemCount)
        recyclerView.measure(0,0)
        recyclerView.layout(0, 0, 100, 10000)
        assertEquals(2, recyclerView.childCount)
        assertType(ColorDrawable::class, recyclerView.getChildAt(1).find<ImageView>(R.id.heroimage).drawable)
    }

    @Test
    fun `OpenThemeScreen should be called after view click`() {
        val apps = listOf(
                InstalledTheme("id1", "name1", "author1", null),
                InstalledTheme("id2", "name2", "author2", null)
        )

        activity.addApplications(apps.map { MainViewTheme.fromInstalledTheme(it, false) })
        recyclerView.measure(0, 0);
        recyclerView.layout(0, 0, 100, 10000);

        val view = recyclerView.getChildAt(0)
        view.performClick()

        verify(presenter).openThemeScreen("id1")
    }

    @Test
    fun `DetailedView should be opened after openThemeFragmentCall`() {
        activity.openThemeFragment("id1")
        val nextIntent = Shadows.shadowOf(activity as AppCompatActivity).peekNextStartedActivity()
//        assertEquals(nextIntent.getStringExtra("appId"), "id1")
        assertEquals("id1", nextIntent.getStringExtra(readStaticField(DetailedViewStarter::class.java, "APP_ID_KEY", true) as String))
        assertEquals(ComponentName(activity as AppCompatActivity, DetailedView::class.java), nextIntent.component)
    }

    @Test
    fun `InstalledView should be opened after clicking on actionbar item`() {
        val item = mock<MenuItem>()
        `when`(item.itemId).thenReturn(R.id.action_installed)
        (activity as AppCompatActivity).onOptionsItemSelected(item)
        val nextIntent = Shadows.shadowOf(activity as AppCompatActivity).peekNextStartedActivity()
        assertEquals(nextIntent.component, ComponentName(activity as AppCompatActivity, InstalledView::class.java))
    }

    @Test
    fun `Lock should be visible if theme is encrypted`() {

        val encryptedApp = MainViewTheme("app1", "App nr. 1", "Author 1", null, true)
        activity.addApplications(listOf(encryptedApp))

        recyclerView.measure(0, 0);
        recyclerView.layout(0, 0, 100, 10000);

        val view = recyclerView.getChildAt(0).find<ImageView>(R.id.lock)

        assertEquals(view.visibility, View.VISIBLE)
    }

    @Test
    fun `Lock should not be visible if theme is not encrypted`() {

        val encryptedApp = MainViewTheme("app1", "App nr. 1", "Author 1", null, false)
        activity.addApplications(listOf(encryptedApp))

        recyclerView.measure(0, 0);
        recyclerView.layout(0, 0, 100, 10000);

        val view = recyclerView.getChildAt(0).find<ImageView>(R.id.lock)

        assertEquals(view.visibility, View.GONE)
    }

    @Test
    fun `When clicking on log toast is shown`() {

        val encryptedApp = MainViewTheme("app1", "App nr. 1", "Author 1", null, true)
        activity.addApplications(listOf(encryptedApp))

        recyclerView.measure(0, 0);
        recyclerView.layout(0, 0, 100, 10000);

        recyclerView.getChildAt(0).find<ImageView>(R.id.lock).performClick()

        assertEquals("Theme is encrypted. Ask themer to also include unencrypted files.", ShadowToast.getTextOfLatestToast())

    }

    @Test
    fun `showUndismissableDialog should show dialog with given message`() {

        val message = "Very important message"

        activity.showUndismissableDialog(message)

        val dialog = ShadowDialog.getLatestDialog()
        val displayed = dialog.find<TextView>(android.R.id.message).text.toString()

        assertEquals(message, displayed)

        dialog.dispatchKeyEvent(KeyEvent(0, 0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK, 0))

        assertTrue(dialog.isShowing)
    }

    @Test
    fun `dismissDialog() should dismiss any dialog`() {

        `showUndismissableDialog should show dialog with given message`()

        activity.dismissDialog()

        assertFalse(ShadowDialog.getLatestDialog().isShowing)
    }

}


