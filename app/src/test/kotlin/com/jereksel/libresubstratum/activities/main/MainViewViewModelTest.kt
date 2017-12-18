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
import com.jereksel.libresubstratum.data.InstalledTheme
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.OverlayService
import com.jereksel.libresubstratum.presenters.PresenterTestUtils.initLiveData
import com.jereksel.libresubstratum.presenters.PresenterTestUtils.initRxJava
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.kotlintest.specs.FunSpec
import org.assertj.core.api.Assertions.assertThat
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.concurrent.FutureTask

class MainViewViewModelTest: FunSpec() {

    @Mock
    lateinit var packageManager: IPackageManager
    @Mock
    lateinit var overlayService: OverlayService
    @Mock
    lateinit var observer: Observer<List<MainViewModel>>

    lateinit var mainViewViewModel: MainViewViewModel

    override fun beforeEach() {
        MockitoAnnotations.initMocks(this)
        mainViewViewModel = MainViewViewModel(packageManager, overlayService)
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

            whenever(packageManager.getInstalledThemes()).thenReturn(listOf(
                    InstalledTheme("app1", "Theme 1", "", false, "", FutureTask { null }),
                    InstalledTheme("app2", "Theme 2", "", false, "", FutureTask { null })
            ))

            mainViewViewModel.init()

            assertThat(mainViewViewModel.apps).containsExactly(
                    MainViewModel("app1", "Theme 1"), MainViewModel("app2", "Theme 2")
            )

        }

    }

}

