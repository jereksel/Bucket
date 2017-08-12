package com.jereksel.libresubstratum.presenters

import com.jereksel.libresubstratum.activities.installed.InstalledContract
import com.jereksel.libresubstratum.activities.installed.InstalledPresenter
import com.jereksel.libresubstratum.data.InstalledOverlay
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

class InstalledPresenterTest : FunSpec() {

    @Mock
    lateinit var view: InstalledContract.View
    @Mock
    lateinit var packageManager: IPackageManager
    @Mock
    lateinit var overlayService: OverlayService

    lateinit var presenter: InstalledPresenter

    override fun beforeEach() {
        MockitoAnnotations.initMocks(this)
        presenter = InstalledPresenter(packageManager, overlayService)
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
    }

    infix operator fun <T> OngoingStubbing<T>.minus(t: T): OngoingStubbing<T> = thenReturn(t)

}