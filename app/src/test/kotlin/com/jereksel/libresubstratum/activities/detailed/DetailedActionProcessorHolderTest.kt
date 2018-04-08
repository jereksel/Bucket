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

package com.jereksel.libresubstratum.activities.detailed

import com.jereksel.libresubstratum.Utils.initOS
import com.jereksel.libresubstratum.data.InstalledTheme
import com.jereksel.libresubstratum.domain.ClipboardManager
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.OverlayService
import com.jereksel.libresubstratum.domain.usecases.ICompileThemeUseCase
import com.jereksel.libresubstratum.domain.usecases.IGetThemeInfoUseCase
import com.jereksel.libresubstratum.presenters.PresenterTestUtils.initRxJava
import com.jereksel.libresubstratumlib.*
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.kotlintest.properties.Gen
import io.kotlintest.specs.FreeSpec
import io.reactivex.Observable
import org.assertj.core.api.Assertions.assertThat
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.concurrent.FutureTask

class DetailedActionProcessorHolderTest : FreeSpec() {

    @Mock
    lateinit var packageManager: IPackageManager
    @Mock
    lateinit var getThemeInfoUseCase: IGetThemeInfoUseCase
    @Mock
    lateinit var overlayService: OverlayService
    @Mock
    lateinit var compileThemeUseCase: ICompileThemeUseCase
    @Mock
    lateinit var clipboardManager: ClipboardManager

    private lateinit var presenter: DetailedActionProcessorHolder

    override fun beforeEach() {
        MockitoAnnotations.initMocks(this)
        presenter = DetailedActionProcessorHolder(packageManager, getThemeInfoUseCase, overlayService, mock(), compileThemeUseCase, clipboardManager)

        initOS(overlayService)
        initRxJava()
    }

    init {

        //loadListProcessor

        "Load theme info test" - {

            "null type3" {
                val appId = "appId"
                val appName = "appName"
                val theme = ThemePack(listOf(), null)
                whenever(getThemeInfoUseCase.getThemeInfo(appId)).thenReturn(theme)
                whenever(packageManager.getInstalledTheme(appId)).thenReturn(InstalledTheme(appId, appName, "", true, "", FutureTask { null }))

                val list = Observable.just(DetailedAction.InitialAction(appId))
                        .compose(presenter.actionProcessor)
                        .toList()
                        .blockingGet()

                assertThat(list).containsExactly(DetailedResult.ListLoaded(appId, appName, listOf(), null))

            }

            "nonnull type3" {
                val appId = "appId"
                val appName = "Awesome theme"
                val theme = ThemePack(listOf(), Type3Data(listOf(Type3Extension("Default", true), Type3Extension("Oreo", false))))
                whenever(getThemeInfoUseCase.getThemeInfo(appId)).thenReturn(theme)
                whenever(packageManager.getAppName(appId)).thenReturn(appName)
                whenever(packageManager.getInstalledTheme(appId)).thenReturn(InstalledTheme(appId, appName, "", true, "", FutureTask { null }))

                val expected = DetailedResult.ListLoaded(
                        appId,
                        appName,
                        listOf(),
                        DetailedResult.ListLoaded.Type3(
                                listOf(Type3Extension("Default", true), Type3Extension("Oreo", false))
                        )
                )

                val list = Observable.just(DetailedAction.InitialAction(appId))
                        .compose(presenter.actionProcessor)
                        .toList()
                        .blockingGet()

                assertThat(list).containsExactly(expected)

            }

            //TODO: Test - no types

            "all types" {

                val appId = "appId"
                val appName = "Awesome theme"

                val theme = ThemePack(listOf(
                        Theme(
                                application = "app1",
                                type1 = listOf(
                                        Type1Data(
                                                listOf(Type1Extension("CHOOSE ONE", true), Type1Extension("blue", false)),
                                                "a"
                                        ),
                                        Type1Data(
                                                listOf(Type1Extension("CHOOSE ONE", true), Type1Extension("black", false)),
                                                "b"
                                        ),
                                        Type1Data(
                                                listOf(Type1Extension("CHOOSE ONE", true), Type1Extension("white", false)),
                                                "c"
                                        )
                                ),
                                type2 = Type2Data(
                                        listOf(Type2Extension("SELECT ONE", true), Type2Extension("pink", false))
                                )
                        )),
                        Type3Data(listOf(Type3Extension("Default", true), Type3Extension("Oreo", false))))

                whenever(getThemeInfoUseCase.getThemeInfo(appId)).thenReturn(theme)
                whenever(packageManager.getAppName(appId)).thenReturn(appName)
                whenever(packageManager.isPackageInstalled("app1")).thenReturn(true)
                whenever(packageManager.getAppName("app1")).thenReturn("My app")
                whenever(packageManager.getInstalledTheme(appId)).thenReturn(InstalledTheme(appId, appName, "", true, "", FutureTask { null }))

                val expected = DetailedResult.ListLoaded(
                        appId,
                        appName,
                        listOf(
                                DetailedResult.ListLoaded.Theme(
                                        appId = "app1",
                                        name = "My app",
                                        type1a = DetailedResult.ListLoaded.Type1(listOf(Type1Extension("CHOOSE ONE", true), Type1Extension("blue", false))),
                                        type1b = DetailedResult.ListLoaded.Type1(listOf(Type1Extension("CHOOSE ONE", true), Type1Extension("black", false))),
                                        type1c = DetailedResult.ListLoaded.Type1(listOf(Type1Extension("CHOOSE ONE", true), Type1Extension("white", false))),
                                        type2 = DetailedResult.ListLoaded.Type2(listOf(Type2Extension("SELECT ONE", true), Type2Extension("pink", false)))

                                )
                        ),
                        DetailedResult.ListLoaded.Type3(
                                listOf(Type3Extension("Default", true), Type3Extension("Oreo", false))
                        )
                )

                val list = Observable.just(DetailedAction.InitialAction(appId))
                        .compose(presenter.actionProcessor)
                        .toList()
                        .blockingGet()

                assertThat(list).startsWith(expected)


            }


        }

        "Restart UI test" {

            val l = Observable.just(DetailedAction.RestartUIAction())
                    .compose(presenter.actionProcessor)
                    .toList()
                    .blockingGet()

            verify(overlayService).restartSystemUI()
            assertThat(l).isEmpty()

        }

        "basicGetInfoProcessor test" {
            forAll(Gen.int()) { i ->

                val l = Observable.just(DetailedAction.GetInfoBasicAction(i))
                        .compose(presenter.actionProcessor)
                        .toList()
                        .blockingGet()

                assertThat(l).containsExactly(DetailedResult.InstalledStateResult.PositionResult(i))

                true
            }
        }

        "ChangeSpinnerSelection test" - {

            "ChangeType1aSpinnerSelection test" {

                forAll(Gen.int(), Gen.int()) { rvPosition, position ->

                    val l = Observable.just(DetailedAction.ChangeSpinnerSelection.ChangeType1aSpinnerSelection(rvPosition, position))
                            .compose(presenter.actionProcessor)
                            .toList()
                            .blockingGet()

                    assertThat(l).containsExactly(DetailedResult.ChangeSpinnerSelection.ChangeType1aSpinnerSelection(rvPosition, position))

                    true
                }

            }

            "ChangeType1bSpinnerSelection test" {

                forAll(Gen.int(), Gen.int()) { rvPosition, position ->

                    val l = Observable.just(DetailedAction.ChangeSpinnerSelection.ChangeType1bSpinnerSelection(rvPosition, position))
                            .compose(presenter.actionProcessor)
                            .toList()
                            .blockingGet()

                    assertThat(l).containsExactly(DetailedResult.ChangeSpinnerSelection.ChangeType1bSpinnerSelection(rvPosition, position))

                    true
                }

            }

            "ChangeType1cSpinnerSelection test" {

                forAll(Gen.int(), Gen.int()) { rvPosition, position ->

                    val l = Observable.just(DetailedAction.ChangeSpinnerSelection.ChangeType1cSpinnerSelection(rvPosition, position))
                            .compose(presenter.actionProcessor)
                            .toList()
                            .blockingGet()

                    assertThat(l).containsExactly(DetailedResult.ChangeSpinnerSelection.ChangeType1cSpinnerSelection(rvPosition, position))

                    true
                }

            }

            "ChangeType2SpinnerSelection test" {

                forAll(Gen.int(), Gen.int()) { rvPosition, position ->

                    val l = Observable.just(DetailedAction.ChangeSpinnerSelection.ChangeType2SpinnerSelection(rvPosition, position))
                            .compose(presenter.actionProcessor)
                            .toList()
                            .blockingGet()

                    assertThat(l).containsExactly(DetailedResult.ChangeSpinnerSelection.ChangeType2SpinnerSelection(rvPosition, position))

                    true
                }

            }

        }

        "ToggleCheckbox test" {
            forAll(Gen.int(), Gen.bool()) { position, enabled ->

                val l = Observable.just(DetailedAction.ToggleCheckbox(position, enabled))
                        .compose(presenter.actionProcessor)
                        .toList()
                        .blockingGet()

                assertThat(l).containsExactly(DetailedResult.ToggleCheckbox(position, enabled))

                true

            }
        }

    }

}

