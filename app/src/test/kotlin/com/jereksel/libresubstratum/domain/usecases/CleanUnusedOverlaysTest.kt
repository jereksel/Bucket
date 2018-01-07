/*
 * Copyright (C) 2018 Andrzej Ressel (jereksel@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.jereksel.libresubstratum.domain.usecases

import com.jereksel.libresubstratum.data.InstalledOverlay
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.OverlayService
import com.jereksel.libresubstratum.presenters.PresenterTestUtils.initRxJava
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.kotlintest.specs.FunSpec
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class CleanUnusedOverlaysTest: FunSpec() {

    @Mock
    lateinit var packageManager: IPackageManager
    @Mock
    lateinit var overlayService: OverlayService

    lateinit var useCase: ICleanUnusedOverlays

    override fun beforeEach() {
        MockitoAnnotations.initMocks(this)
        useCase = CleanUnusedOverlays(packageManager, overlayService)

        initRxJava()
    }

    init {

        test("Remove overlay test") {

            whenever(packageManager.getInstalledOverlays())
                    .thenReturn(listOf(
                            InstalledOverlay("app1.themepack1.overlay1", "themepack1", "", null, "app1", "", null),
                            InstalledOverlay("app2.themepack1.overlay1", "themepack1", "", null, "app2", "", null),
                            InstalledOverlay("app3.themepack1.overlay1", "themepack1", "", null, "app3", "", null),
                            InstalledOverlay("app1.themepack2.overlay1", "themepack2", "", null, "app1", "", null),
                            InstalledOverlay("app2.themepack2.overlay1", "themepack2", "", null, "app2", "", null)
                    ))

            whenever(packageManager.isPackageInstalled(any())).thenReturn(true)

            whenever(packageManager.isPackageInstalled("themepack2")).thenReturn(false)
            whenever(packageManager.isPackageInstalled("app3")).thenReturn(false)

            useCase.clean().get()

            verify(overlayService).uninstallApk("app1.themepack2.overlay1")
            verify(overlayService).uninstallApk("app2.themepack2.overlay1")
            verify(overlayService).uninstallApk("app3.themepack1.overlay1")

        }
    }

}