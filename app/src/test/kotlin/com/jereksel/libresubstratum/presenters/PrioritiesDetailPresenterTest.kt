package com.jereksel.libresubstratum.presenters

import com.jereksel.libresubstratum.activities.prioritiesdetail.PrioritiesDetailContract
import com.jereksel.libresubstratum.activities.prioritiesdetail.PrioritiesDetailPresenter
import com.jereksel.libresubstratum.data.InstalledOverlay
import com.jereksel.libresubstratum.domain.IActivityProxy
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.OverlayInfo
import com.jereksel.libresubstratum.domain.OverlayService
import com.jereksel.libresubstratum.presenters.PresenterTestUtils.initRxJava
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.kotlintest.specs.FunSpec
import org.assertj.core.api.Assertions.assertThat
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class PrioritiesDetailPresenterTest: FunSpec() {

    @Mock
    lateinit var overlayService: OverlayService
    @Mock
    lateinit var packageManager: IPackageManager
    @Mock
    lateinit var activityProxy: IActivityProxy
    @Mock
    lateinit var view: PrioritiesDetailContract.View

    lateinit var prioritiesDetailPresenter: PrioritiesDetailPresenter

    override fun beforeEach() {
        MockitoAnnotations.initMocks(this)
        prioritiesDetailPresenter = PrioritiesDetailPresenter(overlayService, packageManager, activityProxy)
        prioritiesDetailPresenter.setView(view)
        initRxJava()
    }

    init {

        test("getOverlays") {

            val installedOverlay = listOf(
                    InstalledOverlay("overlay1", "", "", null, "app", "", null),
                    InstalledOverlay("overlay2", "", "", null, "app", "", null),
                    InstalledOverlay("overlay3", "", "", null, "app", "", null),
                    InstalledOverlay("overlay4", "", "", null, "app2", "", null),
                    InstalledOverlay("overlay5", "", "", null, "app2", "", null)
            )

            whenever(packageManager.getInstalledOverlays()).thenReturn(installedOverlay)
            whenever(overlayService.getOverlaysPrioritiesForTarget("app")).thenReturn(
                    listOf(OverlayInfo("overlay1", true), OverlayInfo("overlay2", true), OverlayInfo("overlay3", false))
            )

            prioritiesDetailPresenter.getOverlays("app")

            verify(view).setOverlays(listOf(
                    InstalledOverlay("overlay1", "", "", null, "app", "", null),
                    InstalledOverlay("overlay2", "", "", null, "app", "", null)
            ))


        }

        test("openAppInSplit") {
            prioritiesDetailPresenter.openAppInSplit("12345")
            verify(activityProxy).openActivityInSplit("12345")
        }


        test("updatePriorities passes overlayid to overlayManager") {

            prioritiesDetailPresenter.updatePriorities(listOf(
                    InstalledOverlay("overlay1", "", "", null, "app", "", null),
                    InstalledOverlay("overlay2", "", "", null, "app", "", null),
                    InstalledOverlay("overlay3", "", "", null, "app", "", null)
            ))

            verify(overlayService).updatePriorities(listOf(
                    "overlay1", "overlay2", "overlay3"
            ))


        }

        test("updateOverlays") {

            val installedOverlay = listOf(
                    InstalledOverlay("overlay1", "", "", null, "app", "", null),
                    InstalledOverlay("overlay2", "", "", null, "app", "", null),
                    InstalledOverlay("overlay3", "", "", null, "app", "", null),
                    InstalledOverlay("overlay4", "", "", null, "app2", "", null),
                    InstalledOverlay("overlay5", "", "", null, "app2", "", null)
            )

            whenever(packageManager.getInstalledOverlays()).thenReturn(installedOverlay)
            whenever(overlayService.getOverlaysPrioritiesForTarget("app")).thenReturn(
                    listOf(OverlayInfo("overlay1", true), OverlayInfo("overlay2", true), OverlayInfo("overlay3", false))
            )

            prioritiesDetailPresenter.getOverlays("app")

            prioritiesDetailPresenter.fabShown = false

            prioritiesDetailPresenter.updateOverlays(listOf(
                    InstalledOverlay("overlay2", "", "", null, "app", "", null),
                    InstalledOverlay("overlay1", "", "", null, "app", "", null)
            ))

            verify(view).showFab()

            assertThat(prioritiesDetailPresenter.fabShown).isTrue()

            reset(view)

            prioritiesDetailPresenter.updateOverlays(listOf(
                    InstalledOverlay("overlay1", "", "", null, "app", "", null),
                    InstalledOverlay("overlay2", "", "", null, "app", "", null)
            ))

            verify(view).hideFab()

            assertThat(prioritiesDetailPresenter.fabShown).isFalse()

        }

    }

}
