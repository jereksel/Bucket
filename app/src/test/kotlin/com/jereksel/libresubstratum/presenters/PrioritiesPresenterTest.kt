package com.jereksel.libresubstratum.presenters

import com.jereksel.libresubstratum.activities.priorities.PrioritiesContract
import com.jereksel.libresubstratum.activities.priorities.PrioritiesPresenter
import com.jereksel.libresubstratum.data.InstalledOverlay
import com.jereksel.libresubstratum.data.InstalledTheme
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.OverlayInfo
import com.jereksel.libresubstratum.domain.OverlayService
import com.jereksel.libresubstratum.presenters.PresenterTestUtils.initRxJava
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.kotlintest.specs.FunSpec
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.concurrent.FutureTask

class PrioritiesPresenterTest: FunSpec() {

    @Mock
    lateinit var overlayService: OverlayService
    @Mock
    lateinit var packageManager : IPackageManager
    @Mock
    lateinit var view: PrioritiesContract.View

    lateinit var prioritiesPresenter: PrioritiesPresenter

    override fun beforeEach() {
        MockitoAnnotations.initMocks(this)
        prioritiesPresenter = PrioritiesPresenter(overlayService, packageManager)
        initRxJava()
    }

    init {

        test("getApplications test") {

            whenever(packageManager.getInstalledOverlays()).thenReturn(listOf(
                    installedOverlayFactory("appa1", "appa"),
                    installedOverlayFactory("appa2", "appa"),
                    installedOverlayFactory("appb1", "appb"),
                    installedOverlayFactory("appb2", "appb"),
                    installedOverlayFactory("appb3", "appb"),
                    installedOverlayFactory("appc1", "appc")
            ))

            whenever(overlayService.getOverlayInfo("appa1")).thenReturn(OverlayInfo("", false))
            whenever(overlayService.getOverlayInfo("appa2")).thenReturn(OverlayInfo("", true))
            whenever(overlayService.getOverlayInfo("appb1")).thenReturn(OverlayInfo("", true))
            whenever(overlayService.getOverlayInfo("appb2")).thenReturn(OverlayInfo("", true))
            whenever(overlayService.getOverlayInfo("appb3")).thenReturn(OverlayInfo("", true))
            whenever(overlayService.getOverlayInfo("appc1")).thenReturn(OverlayInfo("", true))

            val installedTheme = InstalledTheme("", "", "", true, "", FutureTask { null })

            whenever(packageManager.getInstalledTheme("appb")).thenReturn(installedTheme)

            prioritiesPresenter.setView(view)

            prioritiesPresenter.getApplication()

            verify(view).addApplications(argThat { toList() == listOf(installedTheme) })

        }

    }

    fun installedOverlayFactory(overlayId: String, targetId: String) = InstalledOverlay(overlayId, "", "", null, targetId, "", null)


}