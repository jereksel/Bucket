package com.jereksel.libresubstratum.views

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import com.jereksel.libresubstratum.BaseRobolectricTest
import com.jereksel.libresubstratum.MockedApp
import com.jereksel.libresubstratum.ResettableLazy
import com.jereksel.libresubstratum.activities.installed.InstalledView
import com.jereksel.libresubstratum.activities.prioritiesdetail.PrioritiesDetailContract
import com.jereksel.libresubstratum.activities.prioritiesdetail.PrioritiesDetailView
import com.jereksel.libresubstratum.data.InstalledOverlay
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.plugins.RxJavaPlugins
import kotlinx.android.synthetic.main.activity_priorities_detail.*
import org.assertj.android.recyclerview.v7.api.Assertions.assertThat
import org.assertj.android.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.robolectric.Robolectric
import org.robolectric.RuntimeEnvironment
import org.robolectric.android.controller.ActivityController
import org.robolectric.shadows.ShadowApplication

@Suppress("IllegalIdentifier")
class PrioritiesDetailViewTest: BaseRobolectricTest() {

    lateinit var activityController : ActivityController<PrioritiesDetailView>
    lateinit var activity : PrioritiesDetailContract.View
    lateinit var presenter: PrioritiesDetailContract.Presenter

    var activityCasted by ResettableLazy { activity as AppCompatActivity? }
    var recyclerView by ResettableLazy { activityCasted!!.recyclerView }
    var fab by ResettableLazy { activityCasted!!.fab }

    val appId = "id1"

    @Before
    fun setup() {
        RxJavaPlugins.reset()
        val app = RuntimeEnvironment.application as MockedApp
        presenter = app.mockedPrioritiesDetailPresenter
        val intent = Intent(ShadowApplication.getInstance().applicationContext, PrioritiesDetailView::class.java)
        val APP_ID_EXTRA = "com.jereksel.libresubstratum.activities.prioritiesdetail.targetIdStarterKey"
        intent.putExtra(APP_ID_EXTRA, appId)
        activityController = Robolectric.buildActivity(PrioritiesDetailView::class.java, intent).create().resume()
        activity = activityController.get()
    }

    @Test
    fun `getOverlays is called after start`() {
        verify(presenter).getOverlays(appId)
    }

    @Test
    fun `setOverlays sets RV`() {
        val list = listOf(
                InstalledOverlay("overlay1", "", "", null, "", "", null),
                InstalledOverlay("overlay2", "", "", null, "", "", null),
                InstalledOverlay("overlay3", "", "", null, "", "", null)
        )

        activity.setOverlays(list)

        assertThat(recyclerView.adapter).hasItemCount(3)

    }

    @Test
    fun `showFab makes fab visible and hideFab makes it invisible`(){

        val list = listOf(
                InstalledOverlay("overlay1", "", "", null, "", "", null),
                InstalledOverlay("overlay2", "", "", null, "", "", null),
                InstalledOverlay("overlay3", "", "", null, "", "", null)
        )

        activity.setOverlays(list)

        assertThat(fab).isNotVisible
        activity.showFab()
        assertThat(fab).isVisible
        activity.hideFab()
        assertThat(fab).isNotVisible

    }

    @After
    fun cleanup() {
        activityCasted?.finish()
        activityController.destroy()
        recyclerView = null
    }

}