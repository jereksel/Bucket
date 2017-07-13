package com.jereksel.libresubstratum.views

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import com.jereksel.libresubstratum.*
import com.jereksel.libresubstratum.activities.main.MainContract
import com.jereksel.libresubstratum.activities.main.MainView
import com.jereksel.libresubstratum.data.DetailedApplication
import com.nhaarman.mockito_kotlin.verify
import io.kotlintest.mock.mock
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_main.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class,
        application = MockedApp::class,
        sdk = intArrayOf(Build.VERSION_CODES.LOLLIPOP))
class MainViewTest {

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
        activityController = Robolectric.buildActivity(MainView::class.java).create()
        activity = activityController.get()
    }

    @After
    fun cleanup() {
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
    fun `SwipeToRefresh should be unactive after returning themes`() {
        activity = Robolectric.buildActivity(MainView::class.java).create().get()
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
        val d1 : Drawable = mock()

        val apps = mutableListOf(
                DetailedApplication("id1", "name1", "author1", d1),
                DetailedApplication("id2", "name2", "author2", null)
        )

        activity.addApplications(apps)
        assertEquals(2, recyclerView.adapter.itemCount)
        recyclerView.measure(0,0)
        recyclerView.layout(0, 0, 100, 10000)
        assertEquals(2, recyclerView.childCount)
        assertSame(d1, (recyclerView.getChildAt(0).findViewById(R.id.heroimage) as ImageView).drawable)
        assertType(ColorDrawable::class, (recyclerView.getChildAt(1).findViewById(R.id.heroimage) as ImageView).drawable)
    }
}
