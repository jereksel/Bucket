package com.jereksel.libresubstratum.views

import com.jereksel.libresubstratum.BaseRobolectricTest
import com.jereksel.libresubstratum.MockedApp
import com.jereksel.libresubstratum.activities.priorities.PrioritiesContract
import com.jereksel.libresubstratum.activities.priorities.PrioritiesView
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.plugins.RxJavaPlugins
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.robolectric.Robolectric
import org.robolectric.RuntimeEnvironment
import org.robolectric.android.controller.ActivityController

@Suppress("IllegalIdentifier")
class PrioritiesViewTest: BaseRobolectricTest() {

    lateinit var activityController : ActivityController<PrioritiesView>
    lateinit var activity : PrioritiesContract.View
    lateinit var presenter: PrioritiesContract.Presenter

    @Before
    fun setup() {
        RxJavaPlugins.reset()
        val app = RuntimeEnvironment.application as MockedApp
        presenter = app.mockedPrioritiesPresenter
        activityController = Robolectric.buildActivity(PrioritiesView::class.java).create().resume()
        activity = activityController.get()
    }

    @Test
    fun `getApplication() is started after entering activity`() {
        verify(presenter).getApplication()
    }

    @After
    fun cleanup() {
        activityController.destroy()
    }

}