/*
 * Copyright (C) 2017 Andrzej Ressel (jereksel@gmail.com)
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

package com.jereksel.libresubstratum.activities.main

import com.jereksel.libresubstratum.data.InstalledTheme
import com.jereksel.libresubstratum.domain.IKeyFinder
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.OverlayService
import com.jereksel.libresubstratum.presenters.PresenterTestUtils.initLiveData
import com.jereksel.libresubstratum.presenters.PresenterTestUtils.initRxJava
import com.jereksel.libresubstratum.utils.FutureUtils.toFuture
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.kotlintest.mock.mock
import io.kotlintest.specs.FunSpec
import kotlinx.coroutines.experimental.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class MainViewViewModelTest: FunSpec() {

    @Mock
    lateinit var packageManager: IPackageManager
    @Mock
    lateinit var overlayService: OverlayService
    @Mock
    lateinit var keyFinder: IKeyFinder

    lateinit var mainViewViewModel: MainViewViewModel

    override fun beforeEach() {
        MockitoAnnotations.initMocks(this)
        mainViewViewModel = MainViewViewModel(packageManager, overlayService, keyFinder, mock())
        initRxJava()
        initLiveData()
    }

    init {

        test("init doesn't call getInstalledThemes the second time") {
            whenever(packageManager.getInstalledThemes()).thenReturn(listOf())
            mainViewViewModel.init()
            mainViewViewModel.init()
            runBlocking {
                mainViewViewModel.job.join()
            }
            verify(packageManager, times(1)).getInstalledThemes()
        }

        test("Values from PackageManager is passed to ObservableList") {

            whenever(packageManager.getInstalledThemes()).thenReturn(listOf(
                    InstalledTheme("app1", "Theme 1", "", false, ""),
                    InstalledTheme("app2", "Theme 2", "", false, ""),
                    InstalledTheme("app3", "Theme 3", "", false, "")
            ))

            whenever(packageManager.getHeroImage(anyString())).thenReturn(null.toFuture())

            mainViewViewModel.init()

            runBlocking {
                mainViewViewModel.job.join()
            }

            assertThat(mainViewViewModel.getAppsObservable()).containsExactly(
                    MainViewModel("app1", "Theme 1", keyAvailable = false, heroImage = null),
                    MainViewModel("app2", "Theme 2", keyAvailable = false, heroImage = null),
                    MainViewModel("app3", "Theme 3", keyAvailable = false, heroImage = null)
            )

        }

        test("When permissions are required they are passed to permissions live event") {

            whenever(overlayService.requiredPermissions()).thenReturn(listOf("permission1", "permission2"))

            runBlocking {
                mainViewViewModel.tickChecks()
            }

            assertThat(mainViewViewModel.getPermissions().value).containsOnly(
                    "permission1", "permission2"
            )

        }

        test("When permissions are not required and there is other message available it is shown") {

            val message = "Dialog message"

            whenever(overlayService.requiredPermissions()).thenReturn(listOf())
            whenever(overlayService.additionalSteps()).thenReturn(message.toFuture())

            runBlocking {
                mainViewViewModel.tickChecks()
            }

            assertThat(mainViewViewModel.getPermissions().value).isNullOrEmpty()
            assertThat(mainViewViewModel.getDialogContent().value).isEqualTo(message)

        }

        test("When permissions are not required and there are no messages nothing is passed") {

            whenever(overlayService.requiredPermissions()).thenReturn(listOf())
            whenever(overlayService.additionalSteps()).thenReturn(null.toFuture())

            runBlocking {
                mainViewViewModel.tickChecks()
            }

            assertThat(mainViewViewModel.getPermissions().value).isNullOrEmpty()
            assertThat(mainViewViewModel.getDialogContent().value).isNullOrEmpty()

        }

        test("Reset removed apps and redownloads them") {

            whenever(packageManager.getInstalledThemes()).thenReturn(listOf(
                    InstalledTheme("app1", "Theme 1", "", false, ""),
                    InstalledTheme("app2", "Theme 2", "", false, "")
            ))

            whenever(packageManager.getHeroImage(anyString())).thenReturn(null.toFuture())

            mainViewViewModel.init()

            runBlocking {
                mainViewViewModel.job.join()
            }

            assertThat(mainViewViewModel.getAppsObservable()).containsExactly(
                    MainViewModel("app1", "Theme 1", keyAvailable = false, heroImage = null),
                    MainViewModel("app2", "Theme 2", keyAvailable = false, heroImage = null)
            )

            whenever(packageManager.getInstalledThemes()).thenReturn(listOf(
                    InstalledTheme("app1", "Theme 1", "", false, ""),
                    InstalledTheme("app2", "Theme 2", "", false, ""),
                    InstalledTheme("app3", "Theme 3", "", false, "")
            ))

            mainViewViewModel.reset()

            //FIXME: Too flaky
//            assertThat(mainViewViewModel.getSwipeToRefreshObservable().get()).isTrue()

            runBlocking {
                mainViewViewModel.job.join()
            }

            assertThat(mainViewViewModel.getAppsObservable()).containsExactly(
                    MainViewModel("app1", "Theme 1", keyAvailable = false, heroImage = null),
                    MainViewModel("app2", "Theme 2", keyAvailable = false, heroImage = null),
                    MainViewModel("app3", "Theme 3", keyAvailable = false, heroImage = null)
            )

            assertThat(mainViewViewModel.getSwipeToRefreshObservable().get()).isFalse()


        }

    }

}

