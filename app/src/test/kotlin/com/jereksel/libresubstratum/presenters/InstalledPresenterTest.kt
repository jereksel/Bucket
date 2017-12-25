package com.jereksel.libresubstratum.presenters

import com.jereksel.libresubstratum.Utils.initOS
import com.jereksel.libresubstratum.activities.installed.InstalledContract
import com.jereksel.libresubstratum.activities.installed.InstalledPresenter
import com.jereksel.libresubstratum.data.InstalledOverlay
import com.jereksel.libresubstratum.domain.IActivityProxy
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.OverlayInfo
import com.jereksel.libresubstratum.domain.OverlayService
import com.jereksel.libresubstratum.presenters.PresenterTestUtils.initRxJava
import org.assertj.core.api.Assertions.*
import com.nhaarman.mockito_kotlin.*
import io.kotlintest.specs.FunSpec
import com.jereksel.libresubstratum.utils.FutureUtils.toFuture
import kotlinx.coroutines.experimental.runBlocking
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

        initOS(overlayService)
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
            runBlocking {
                presenter.toggleOverlay("com.android.systemui.navbar", true)
            }
            verify(view).showSnackBar(any(), any(), any())
        }

        test("Snackbar is not shown for other overlays") {
            whenever(overlayService.enableOverlay(any())).thenReturn(Unit.toFuture())
            whenever(overlayService.disableOverlay(any())).thenReturn(Unit.toFuture())
            runBlocking {
                presenter.toggleOverlay("com.jereksel.libresubstratum", true)
            }
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
                initOS(overlayService)
                runBlocking {
                    presenter.toggleOverlay("app", enabled)
                }
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

            assertFalse(presenter.getState("overlay1"))
            presenter.setState("overlay1", true)
            assertTrue(presenter.getState("overlay1"))
            presenter.setState("overlay1", false)
            assertFalse(presenter.getState("overlay1"))

        }
        val prepare = {

            whenever(overlayService.getOverlayInfo(any())).then {
                val id: String = it.getArgument(0)
                OverlayInfo(id, "", false).toFuture()
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

            presenter.setState("overlay0", false)
            presenter.setState("overlay1", true)
            presenter.setState("overlay2", true)
            presenter.setState("overlay3", false)
            presenter.setState("overlay4", true)
        }

        test("Selected overlays are uninstalled during uninstallSelected") {
            prepare()
            whenever(overlayService.uninstallApk(any())).thenReturn(Unit.toFuture())
            runBlocking {
                presenter.uninstallSelected()
            }
            verify(overlayService).uninstallApk("overlay1")
            verify(overlayService).uninstallApk("overlay2")
            verify(overlayService).uninstallApk("overlay4")
        }
        test("Selected overlays are enabled during enableSelected") {
            prepare()
            whenever(overlayService.getOverlayInfo("overlay1")).thenReturn(OverlayInfo("overlay1", "", true).toFuture())
            whenever(overlayService.getOverlayInfo("overlay3")).thenReturn(OverlayInfo("overlay3", "", true).toFuture())
            whenever(overlayService.enableOverlay(any())).thenReturn(Unit.toFuture())
            runBlocking {
                presenter.enableSelected()
            }
            verify(overlayService).enableOverlay("overlay2")
            verify(overlayService).enableOverlay("overlay4")
            verify(overlayService, times(2)).enableOverlay(any())
        }
        test("Selected overlays are disabled during disableSelected") {
            prepare()
            whenever(overlayService.getOverlayInfo("overlay2")).thenReturn(OverlayInfo("overlay2", "", true).toFuture())
            whenever(overlayService.getOverlayInfo("overlay4")).thenReturn(OverlayInfo("overlay4", "", true).toFuture())
            whenever(overlayService.disableOverlay(any())).thenReturn(Unit.toFuture())
            runBlocking {
                presenter.disableSelected()
            }
            verify(overlayService).disableOverlay("overlay2")
            verify(overlayService).disableOverlay("overlay4")
            verify(overlayService, times(2)).disableOverlay(any())
        }

        test("selectAll sets all states to true and calls refreshRV") {
            prepare()
            presenter.selectAll()
            for (i in 0..4) {
                assertTrue(presenter.getState("overlay$i"))
            }
            verify(view).refreshRecyclerView()
        }
        test("deselectAll sets all states to false and calls refreshRV") {
            prepare()
            presenter.deselectAll()
            for (i in 0..4) {
                assertFalse(presenter.getState("overlay$i"))
            }
            verify(view).refreshRecyclerView()
        }
        test("Setting filter would provide filtered data with updateOverlays") {

            whenever(overlayService.getOverlayInfo(any())).then {
                val id: String = it.getArgument(0)
                OverlayInfo(id, "", false)
            }

            whenever(packageManager.getInstalledOverlays()).thenReturn(
                    listOf(
                            InstalledOverlay("overlay2", "", "Theme1", null, "", "Chrome", null),
                            InstalledOverlay("overlay1", "", "Theme1", null, "", "Settings", null),
                            InstalledOverlay("overlay0", "", "Theme1", null, "", "System", null),
                            InstalledOverlay("overlay3", "", "Theme2", null, "", "Chrome", null),
                            InstalledOverlay("overlay4", "", "Theme2", null, "", "System", null)
                    )
            )

            presenter.getInstalledOverlays()

            val captor: KArgumentCaptor<List<InstalledOverlay>> = argumentCaptor()

            presenter.setFilter("Theme1")

            verify(view).updateOverlays(captor.capture())

            assertThat(captor.firstValue).containsExactlyElementsOf(
                    listOf(
                            InstalledOverlay("overlay2", "", "Theme1", null, "", "Chrome", null),
                            InstalledOverlay("overlay1", "", "Theme1", null, "", "Settings", null),
                            InstalledOverlay("overlay0", "", "Theme1", null, "", "System", null)
                    )
            )

            reset(view)

            presenter.setFilter("Chrome")

            verify(view).updateOverlays(captor.capture())

            assertThat(captor.secondValue).containsExactlyElementsOf(
                    listOf(
                            InstalledOverlay("overlay2", "", "Theme1", null, "", "Chrome", null),
                            InstalledOverlay("overlay3", "", "Theme2", null, "", "Chrome", null)
                    )
            )

        }
        test("selectAll with filter selects only overlays that are shown to user") {
            whenever(overlayService.getOverlayInfo(any())).then {
                val id: String = it.getArgument(0)
                OverlayInfo(id, "", false)
            }

            whenever(packageManager.getInstalledOverlays()).thenReturn(
                    listOf(
                            InstalledOverlay("overlay0", "", "Theme1", null, "", "Chrome", null),
                            InstalledOverlay("overlay1", "", "Theme1", null, "", "Settings", null),
                            InstalledOverlay("overlay2", "", "Theme1", null, "", "System", null),
                            InstalledOverlay("overlay3", "", "Theme2", null, "", "Chrome", null),
                            InstalledOverlay("overlay4", "", "Theme2", null, "", "System", null)
                    )
            )

            presenter.getInstalledOverlays()

            for(i in 0..4) {
                assertThat(presenter.getState("overlay$i")).isFalse()
            }

            presenter.setFilter("System")
            presenter.selectAll()

            assertThat(presenter.getState("overlay0")).isFalse()
            assertThat(presenter.getState("overlay1")).isFalse()
            assertThat(presenter.getState("overlay2")).isTrue()
            assertThat(presenter.getState("overlay3")).isFalse()
            assertThat(presenter.getState("overlay4")).isTrue()

        }
        test("deselectAll with filter disables only overlays that are shown to user") {

            whenever(overlayService.getOverlayInfo(any())).then {
                val id: String = it.getArgument(0)
                OverlayInfo(id, "", false)
            }

            whenever(packageManager.getInstalledOverlays()).thenReturn(
                    listOf(
                            InstalledOverlay("overlay0", "", "Theme1", null, "", "Chrome", null),
                            InstalledOverlay("overlay1", "", "Theme1", null, "", "Settings", null),
                            InstalledOverlay("overlay2", "", "Theme1", null, "", "System", null),
                            InstalledOverlay("overlay3", "", "Theme2", null, "", "Chrome", null),
                            InstalledOverlay("overlay4", "", "Theme2", null, "", "System", null)
                    )
            )

            presenter.getInstalledOverlays()

            for(i in 0..4) {
                presenter.setState("overlay$i", true)
            }

            for(i in 0..4) {
                assertThat(presenter.getState("overlay$i")).isTrue()
            }

            presenter.setFilter("System")
            presenter.deselectAll()

            assertThat(presenter.getState("overlay0")).isTrue()
            assertThat(presenter.getState("overlay1")).isTrue()
            assertThat(presenter.getState("overlay2")).isFalse()
            assertThat(presenter.getState("overlay3")).isTrue()
            assertThat(presenter.getState("overlay4")).isFalse()

        }
        test("Restart SystemUI invokes it in OverlayService") {
            presenter.restartSystemUI()
            verify(overlayService).restartSystemUI()
        }
    }
}