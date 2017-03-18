package com.jereksel.libresubstratum.views

import android.app.Activity
import android.os.Build
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import com.jereksel.libresubstratum.BuildConfig
import com.jereksel.libresubstratum.ResettableLazy
import com.jereksel.libresubstratum.activities.main.MainView
import com.jereksel.libresubstratum.domain.IPackageManager
import com.nhaarman.mockito_kotlin.mockingDetails
import com.nhaarman.mockito_kotlin.verify
import io.kotlintest.mock.`when`
import io.kotlintest.mock.mock
import kotlinx.android.synthetic.main.activity_main.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.util.ActivityController
import rx.android.plugins.RxAndroidPlugins
import rx.android.plugins.RxAndroidSchedulersHook
import rx.plugins.RxJavaHooks
import rx.schedulers.Schedulers
import rx.schedulers.TestScheduler


@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class,
        application = MockedApp::class,
        sdk = intArrayOf(Build.VERSION_CODES.LOLLIPOP))
class MainViewTest {

    lateinit var packageManager : IPackageManager
    lateinit var activity : Activity
    lateinit var scheduler : TestScheduler

    var swipeToRefresh by ResettableLazy { activity.swiperefresh }
    var recyclerView by ResettableLazy { activity.recyclerView }

    @Before
    fun setup() {
        val app = RuntimeEnvironment.application as MockedApp
        packageManager = app.mockedPackageManager
        swipeToRefresh = null
        recyclerView = null
        scheduler = TestScheduler()

        RxJavaHooks.clear()
        RxJavaHooks.setOnIOScheduler({ Schedulers.immediate() })
        val hook = object : RxAndroidSchedulersHook() {
            override fun getMainThreadScheduler() = scheduler
        }

        RxAndroidPlugins.getInstance().reset()
        RxAndroidPlugins.getInstance().registerSchedulersHook(hook)
    }

    @Test
    fun noApps() {
        `when`(packageManager.getApplications()).thenReturn(mutableListOf())
        activity = Robolectric.buildActivity(MainView::class.java).create().get();
        assertTrue(swipeToRefresh.isRefreshing)
        scheduler.triggerActions()
        verify(packageManager).getApplications()
        assertFalse(swipeToRefresh.isRefreshing)
        assertEquals(0, recyclerView.adapter.itemCount)
    }
}

