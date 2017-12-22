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

import android.arch.lifecycle.Observer
import android.graphics.drawable.Drawable
import com.jereksel.libresubstratum.data.InstalledTheme
import com.jereksel.libresubstratum.domain.IKeyFinder
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.KeyFinder
import com.jereksel.libresubstratum.domain.OverlayService
import com.jereksel.libresubstratum.presenters.PresenterTestUtils.initLiveData
import com.jereksel.libresubstratum.presenters.PresenterTestUtils.initRxJava
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.kotlintest.mock.mock
import io.kotlintest.specs.FunSpec
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import org.assertj.core.api.Assertions.assertThat
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.io.File
import java.util.concurrent.FutureTask

class MainViewViewModelTest: FunSpec() {

    @Mock
    lateinit var packageManager: IPackageManager
    @Mock
    lateinit var overlayService: OverlayService
    @Mock
    lateinit var keyFinder: IKeyFinder
    @Mock
    lateinit var observer: Observer<List<MainViewModel>>

    lateinit var mainViewViewModel: MainViewViewModel

    override fun beforeEach() {
        MockitoAnnotations.initMocks(this)
        mainViewViewModel = MainViewViewModel(packageManager, overlayService, keyFinder)
        initRxJava()
        initLiveData()
    }

    init {

        test("init doesn't call getInstalledThemes the second time") {
            whenever(packageManager.getInstalledThemes()).thenReturn(listOf())
            mainViewViewModel.init()
            mainViewViewModel.init()
            verify(packageManager, times(1)).getInstalledThemes()
        }

        test("Values from PackageManager is passed to ObservableList") {

            val test = TestScheduler()

            RxJavaPlugins.reset()
            RxJavaPlugins.setComputationSchedulerHandler { TestScheduler() }
            RxJavaPlugins.setIoSchedulerHandler { test }

            whenever(packageManager.getInstalledThemes()).thenReturn(listOf(
                    InstalledTheme("app1", "Theme 1", "", false, "", FutureTask { File("") }),
                    InstalledTheme("app2", "Theme 2", "", false, "", FutureTask { File("") })
            ))

            mainViewViewModel.init()

            assertThat(mainViewViewModel.apps).isEmpty()

            test.triggerActions()

            assertThat(mainViewViewModel.apps).containsExactly(
                    MainViewModel("app1", "Theme 1", keyAvailable = false, heroImage = File("").absolutePath),
                    MainViewModel("app2", "Theme 2", keyAvailable = false, heroImage = File("").absolutePath)
            )

        }

        test("When permissions are required they are passed to permissions live event") {

            whenever(overlayService.requiredPermissions()).thenReturn(listOf("permission1", "permission2"))

            mainViewViewModel.tickChecks()

            assertThat(mainViewViewModel.getPermissions().value).containsOnly(
                    "permission1", "permission2"
            )

        }

        test("When permissions are not required and there is other message available it is shown") {

            val message = "Dialog message"

            whenever(overlayService.requiredPermissions()).thenReturn(listOf())
            whenever(overlayService.additionalSteps()).thenReturn(message)

            mainViewViewModel.tickChecks()

            assertThat(mainViewViewModel.getPermissions().value).isNullOrEmpty()
            assertThat(mainViewViewModel.getDialogContent().value).isEqualTo(message)

        }

        test("When permissions are not required and there are no messages nothing is passed") {

            whenever(overlayService.requiredPermissions()).thenReturn(listOf())
            whenever(overlayService.additionalSteps()).thenReturn(null)

            mainViewViewModel.tickChecks()

            assertThat(mainViewViewModel.getPermissions().value).isNullOrEmpty()
            assertThat(mainViewViewModel.getDialogContent().value).isNullOrEmpty()

        }

    }

}

