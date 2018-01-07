package com.jereksel.libresubstratum.presenters

import android.graphics.drawable.Drawable
import com.jereksel.libresubstratum.Utils.initOS
import com.jereksel.libresubstratum.activities.priorities.PrioritiesContract
import com.jereksel.libresubstratum.activities.priorities.PrioritiesPresenter
import com.jereksel.libresubstratum.data.InstalledOverlay
import com.jereksel.libresubstratum.data.InstalledTheme
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.OverlayInfo
import com.jereksel.libresubstratum.domain.OverlayService
import com.jereksel.libresubstratum.presenters.PresenterTestUtils.initRxJava
import com.nhaarman.mockito_kotlin.*
import io.kotlintest.specs.FunSpec
import org.assertj.core.api.Assertions.assertThat
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.concurrent.FutureTask
import com.jereksel.libresubstratum.utils.FutureUtils.toFuture

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
        initOS(overlayService)
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

            whenever(overlayService.getOverlayInfo("appa1")).thenReturn(OverlayInfo("", "", false).toFuture())
            whenever(overlayService.getOverlayInfo("appa2")).thenReturn(OverlayInfo("", "",true).toFuture())
            whenever(overlayService.getOverlayInfo("appb1")).thenReturn(OverlayInfo("", "",true).toFuture())
            whenever(overlayService.getOverlayInfo("appb2")).thenReturn(OverlayInfo("", "",true).toFuture())
            whenever(overlayService.getOverlayInfo("appb3")).thenReturn(OverlayInfo("", "",true).toFuture())
            whenever(overlayService.getOverlayInfo("appc1")).thenReturn(OverlayInfo("", "",true).toFuture())

            val installedThemea = InstalledTheme("", "", "", true, "", FutureTask { null })
            val installedThemeb = InstalledTheme("", "", "", true, "", FutureTask { null })

            whenever(packageManager.getAppName("appa")).thenReturn("Z")
            whenever(packageManager.getAppName("appb")).thenReturn("A")

            whenever(packageManager.getInstalledTheme("appa")).thenReturn(installedThemea)
            whenever(packageManager.getInstalledTheme("appb")).thenReturn(installedThemeb)

            val captor = argumentCaptor<List<String>>()

            prioritiesPresenter.setView(view)

            prioritiesPresenter.getApplication()

            verify(view).addApplications(captor.capture())

            assertThat(captor.firstValue).containsExactly("appb", "appa")

        }

        test("getIcon") {

            val appId = "APPID"
            val d: Drawable = mock()

            whenever(packageManager.getAppIcon(appId)).thenReturn(d)
            assertThat(prioritiesPresenter.getIcon(appId)).isSameAs(d)

        }

        test("getAppName") {

            val appId = "APPID"
            val appName = "My app"

            whenever(packageManager.getAppName(appId)).thenReturn(appName)
            assertThat(prioritiesPresenter.getAppName(appId)).isEqualTo(appName)

        }

    }

    fun installedOverlayFactory(overlayId: String, targetId: String) = InstalledOverlay(overlayId, "", "", null, targetId, "", null)


}