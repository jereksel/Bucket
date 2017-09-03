package com.jereksel.libresubstratum.presenters

import android.os.AsyncTask
import com.jereksel.libresubstratum.activities.installed.InstalledContract
import com.jereksel.libresubstratum.activities.installed.InstalledPresenter
import com.jereksel.libresubstratum.data.InstalledOverlay
import com.jereksel.libresubstratum.domain.IActivityProxy
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.OverlayService
import com.nhaarman.mockito_kotlin.*
import io.kotlintest.specs.FunSpec
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.stubbing.OngoingStubbing
import rx.android.plugins.RxAndroidPlugins
import rx.android.plugins.RxAndroidSchedulersHook
import rx.plugins.RxJavaHooks
import rx.schedulers.Schedulers
import java.util.concurrent.ExecutorService

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
        presenter = InstalledPresenter(packageManager, overlayService, activityProxy)
        presenter.setView(view)
        RxJavaHooks.clear()
        RxJavaHooks.setOnComputationScheduler { Schedulers.immediate() }

        val hook = object : RxAndroidSchedulersHook() {
            override fun getMainThreadScheduler() = Schedulers.immediate()
        }

        RxAndroidPlugins.getInstance().reset()
        RxAndroidPlugins.getInstance().registerSchedulersHook(hook)
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
                presenter.toggleOverlay("app", enabled)
                verify(overlayService).toggleOverlay("app", enabled)
            }

        }
        test("All visible overlays are uninstalled during uninstallAll") {

            whenever(packageManager.getInstalledOverlays()).thenReturn(
                    listOf(
                            InstalledOverlay("overlay1", "", "", mock(), "", "", mock()),
                            InstalledOverlay("overlay2", "", "", mock(), "", "", mock())
                    )
            )

            presenter.getInstalledOverlays()

            presenter.uninstallAll()

            verify(overlayService).uninstallApk(listOf("overlay1", "overlay2"))
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

            whenever(packageManager.getInstalledOverlays()).thenReturn(
                    listOf(
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
        }

        test("Selected overlays are uninstalled during uninstallSelected") {
            prepare()
            presenter.uninstallSelected()
            verify(overlayService).uninstallApk(listOf("overlay2", "overlay3"))
        }
        test("Selected overlays are enabled during enableSelected") {
            prepare()
            presenter.enableSelected()
            verify(overlayService).enableOverlays(listOf("overlay2", "overlay3"))
        }
        test("Selected overlays are disabled during disableSelected") {
            prepare()
            presenter.disableSelected()
            verify(overlayService).disableOverlays(listOf("overlay2", "overlay3"))
        }
    }
}