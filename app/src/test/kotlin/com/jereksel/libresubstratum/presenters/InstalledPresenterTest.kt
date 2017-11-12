package com.jereksel.libresubstratum.presenters

import com.jereksel.libresubstratum.activities.installed.InstalledContract
import com.jereksel.libresubstratum.activities.installed.InstalledPresenter
import com.jereksel.libresubstratum.data.InstalledOverlay
import com.jereksel.libresubstratum.domain.IActivityProxy
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.OverlayInfo
import com.jereksel.libresubstratum.domain.OverlayService
import com.jereksel.libresubstratum.presenters.PresenterTestUtils.initRxJava
import com.nhaarman.mockito_kotlin.*
import io.kotlintest.specs.FunSpec
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class InstalledPresenterTest : FunSpec() {

    @Mock
    lateinit var view: InstalledContract.View
    @Mock
    lateinit var packageManager: IPackageManager
    @Mock
    lateinit var overlayService: OverlayService
    @Mock
    lateinit var activityProxy: IActivityProxy

    lateinit var presenter: InstalledPresenter

    override fun beforeEach() {
        MockitoAnnotations.initMocks(this)
        presenter = InstalledPresenter(packageManager, overlayService, activityProxy, mock())
        presenter.setView(view)

        initRxJava()
    }

    init {

        test("GetInstalledOverlays should returned sorted overlays") {

            val captor: KArgumentCaptor<List<InstalledOverlay>> = argumentCaptor()

            val overlay1 = InstalledOverlay("id1", "id1", "Source A", mock(), "id1", "Target A", mock())
            val overlay2 = InstalledOverlay("id2", "id2", "Source B", mock(), "id2", "Target B", mock())

            whenever(packageManager.getInstalledOverlays()).thenReturn(listOf(overlay2, overlay1))

            presenter.getInstalledOverlays()
            verify(view).addOverlays(captor.capture())

            val l = captor.firstValue

            assertNotNull(l)
            assertEquals(2, l.size)
            assertEquals(listOf(overlay1, overlay2), l)

        }

        test("Snackbar is shown when overlays for com.android.systemui.* is enabled/disabled") {
            presenter.toggleOverlay("com.android.systemui.navbar", true)
            verify(view).showSnackBar(any(), any(), any())
        }

        test("Snackbar is not shown for other overlays") {
            presenter.toggleOverlay("com.jereksel.libresubstratum", true)
            verify(view, never()).showSnackBar(any(), any(), any())
        }

        test("openActivity invocation is passed to activityproxy") {
            presenter.openActivity("id")
            verify(activityProxy).openActivityInSplit("id")
        }

        test("Enabled parameter is passed to OverlayService") {

            forAll(table(
                    headers("Enabled"),
                    row(true),
                    row(false)
            )) { enabled ->
                reset(overlayService)
                presenter.toggleOverlay("app", enabled)
                if (enabled) {
                    verify(overlayService).enableOverlay("app")
                    verify(overlayService, never()).disableOverlay("app")
                } else {
                    verify(overlayService, never()).enableOverlay("app")
                    verify(overlayService).disableOverlay("app")
                }
            }

        }
        test("Set state is persistent") {

            whenever(packageManager.getInstalledOverlays()).thenReturn(
                    listOf(
                            InstalledOverlay("overlay1", "", "", mock(), "", "", mock()),
                            InstalledOverlay("overlay2", "", "", mock(), "", "", mock())
                    )
            )

            presenter.getInstalledOverlays()

            presenter.setState(0, true)
            assertTrue(presenter.getState(0))
            presenter.setState(0, false)
            assertFalse(presenter.getState(0))

        }
        val prepare = {

            whenever(overlayService.getOverlayInfo(any())).then {
                val id: String = it.getArgument(0)
                OverlayInfo(id, false)
            }

            whenever(packageManager.getInstalledOverlays()).thenReturn(
                    listOf(
                            InstalledOverlay("overlay0", "", "", mock(), "", "", mock()),
                            InstalledOverlay("overlay1", "", "", mock(), "", "", mock()),
                            InstalledOverlay("overlay2", "", "", mock(), "", "", mock()),
                            InstalledOverlay("overlay3", "", "", mock(), "", "", mock()),
                            InstalledOverlay("overlay4", "", "", mock(), "", "", mock())
                    )
            )

            presenter.getInstalledOverlays()

            presenter.setState(0, false)
            presenter.setState(1, true)
            presenter.setState(2, true)
            presenter.setState(3, false)
            presenter.setState(4, true)
        }

        test("Selected overlays are uninstalled during uninstallSelected") {
            prepare()
            presenter.uninstallSelected()
            verify(overlayService).uninstallApk("overlay1")
            verify(overlayService).uninstallApk("overlay2")
            verify(overlayService).uninstallApk("overlay4")
        }
        test("Selected overlays are enabled during enableSelected") {
            prepare()
            whenever(overlayService.getOverlayInfo("overlay1")).thenReturn(OverlayInfo("overlay1", true))
            whenever(overlayService.getOverlayInfo("overlay3")).thenReturn(OverlayInfo("overlay3", true))
            presenter.enableSelected()
            verify(overlayService).enableOverlay("overlay2")
            verify(overlayService).enableOverlay("overlay4")
            verify(overlayService, times(2)).enableOverlay(any())
        }
        test("Selected overlays are disabled during disableSelected") {
            prepare()
            whenever(overlayService.getOverlayInfo("overlay2")).thenReturn(OverlayInfo("overlay2", true))
            whenever(overlayService.getOverlayInfo("overlay4")).thenReturn(OverlayInfo("overlay4", true))
            presenter.disableSelected()
            verify(overlayService).disableOverlay("overlay2")
            verify(overlayService).disableOverlay("overlay4")
            verify(overlayService, times(2)).disableOverlay(any())
        }

        test("selectAll sets all states to true and calls refreshRV") {
            prepare()
            presenter.selectAll()
            for (i in 0..4) {
                assertTrue(presenter.getState(i))
            }
            verify(view).refreshRecyclerView()
        }
        test("deselectAll sets all states to false and calls refreshRV") {
            prepare()
            presenter.deselectAll()
            for (i in 0..4) {
                assertFalse(presenter.getState(i))
            }
            verify(view).refreshRecyclerView()
        }
        test("Restart SystemUI invokes it in OverlayService") {
            presenter.restartSystemUI()
            verify(overlayService).restartSystemUI()
        }
    }
}