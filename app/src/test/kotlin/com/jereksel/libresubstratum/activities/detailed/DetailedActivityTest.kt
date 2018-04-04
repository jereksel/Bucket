package com.jereksel.libresubstratum.activities.detailed

import android.support.v7.app.AppCompatActivity
import com.jereksel.libresubstratum.BaseRobolectricTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.robolectric.Robolectric
import org.robolectric.android.controller.ActivityController
import org.robolectric.shadows.ShadowToast

class DetailedActivityTest: BaseRobolectricTest() {

    lateinit var activityController : ActivityController<DetailedActivity>
    lateinit var activity : DetailedActivity

    @Before
    fun setup() {
        activityController = Robolectric.buildActivity(DetailedActivity::class.java).create().resume()
        activity = activityController.get()
    }

    @Test
    fun `Toast is shown with given message`() {
        val viewState = DetailedViewState.INITIAL.copy(toast =  { "Test toast" } )
        activity.render(viewState)

        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("Test toast")
    }

}