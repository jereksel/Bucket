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

import arrow.core.hashCodeForNullable
import com.jereksel.libresubstratum.activities.detailed.DetailedReducer.oneTimeFunction
import com.jereksel.libresubstratumlib.Type1Extension
import com.jereksel.libresubstratumlib.Type2Extension
import com.jereksel.libresubstratumlib.Type3Extension
import io.kotlintest.properties.Gen
import io.kotlintest.specs.FreeSpec
import org.assertj.core.api.Assertions.assertThat

class DetailedReducerTest: FreeSpec() {

    private val reducer = DetailedReducer

    init {

        "ListLoaded" - {

            "Type3 is copied properly" {

                val type3Extensions = listOf(
                        Type3Extension("a", true),
                        Type3Extension("b", false),
                        Type3Extension("c", false)
                )

                val t = DetailedResult.ListLoaded("", "", listOf(), DetailedResult.ListLoaded.Type3(type3Extensions))

                val (result, list) = reducer.apply(DetailedViewState.INITIAL, t)

                assertThat(list).isEmpty()
                assertThat(result.themePack!!.type3!!.data).isEqualTo(type3Extensions)

            }


        }

        "ChangeSpinnerSelection" - {

            val t  = table(
                    headers("Type", "Selection generator", "Type getter"),
                    row("Type1a", {listPosition: Int, position: Int ->
                        DetailedResult.ChangeSpinnerSelection.ChangeType1aSpinnerSelection(listPosition, position) as DetailedResult.ChangeSpinnerSelection
                    }, {
                        theme: DetailedViewState.Theme -> theme.type1a!! as DetailedViewState.Type }
                    ),
                    row("Type1b", {listPosition: Int, position: Int ->
                        DetailedResult.ChangeSpinnerSelection.ChangeType1bSpinnerSelection(listPosition, position) as DetailedResult.ChangeSpinnerSelection
                    }, {
                        theme: DetailedViewState.Theme -> theme.type1b!! as DetailedViewState.Type }
                    ),
                    row("Type1c", {listPosition: Int, position: Int ->
                        DetailedResult.ChangeSpinnerSelection.ChangeType1cSpinnerSelection(listPosition, position) as DetailedResult.ChangeSpinnerSelection
                    }, {
                        theme: DetailedViewState.Theme -> theme.type1c!! as DetailedViewState.Type }
                    ),
                    row("Type2", {listPosition: Int, position: Int ->
                        DetailedResult.ChangeSpinnerSelection.ChangeType2SpinnerSelection(listPosition, position) as DetailedResult.ChangeSpinnerSelection
                    }, {
                        theme: DetailedViewState.Theme -> theme.type2!! as DetailedViewState.Type }
                    )
            )

            forAll(t, { s: String, gen: (Int, Int) -> DetailedResult.ChangeSpinnerSelection, get: (DetailedViewState.Theme) -> DetailedViewState.Type ->

                "$s is changed properly" - {

                    "When recyclerView position is too large larger than list size nothing happens" {
                        val (newState, list) = reducer.apply(state, gen(1000, 1))
//                    assertThat(list).isEmpty()
                        assertThat(newState).isEqualTo(state)
                    }

                    "When position is larger than list size nothing happens" {
                        val (newState, list) = reducer.apply(state, gen(1, 100))
//                    assertThat(list).isEmpty()
                        assertThat(newState).isEqualTo(state)
                    }

                    "When position is proper, position is changed" {
                        val (newState, list) = reducer.apply(state, gen(0, 1))
//                    assertThat(list).isEmpty()
                        assertThat(get(newState.themePack!!.themes[0]).getListPosition()).isEqualTo(1)
                    }

                }

            })
        }

        "InstalledStateResult" - {

            "Result"  {

                val result = DetailedResult.InstalledStateResult.Result(
                        targetApp =          "app1",
                        targetOverlayId = "newOverlay",
                        installedResult = DetailedViewState.InstalledState.Installed("123", 123),
                        enabledState = DetailedViewState.EnabledState.ENABLED

                )

                val (newState, list) = reducer.apply(state, result)

                assertThat(list).isEmpty()

                val newTheme = newState.themePack!!.themes[0]

                assertThat(newTheme.installedState).isEqualTo(DetailedViewState.InstalledState.Installed("123", 123))
                assertThat(newTheme.enabledState).isEqualTo(DetailedViewState.EnabledState.ENABLED)
                assertThat(newTheme.overlayId).isEqualTo("newOverlay")

            }

        }

        "ChangeType3SpinnerSelection" - {

            "When position is the same nothing changes" {

                val selection = DetailedResult.ChangeType3SpinnerSelection(0)

                val (newState, list) = reducer.apply(state, selection)

                assertThat(list).isEmpty()
                assertThat(newState).isEqualTo(state)

            }

            "When position is not the same position changes" {

                val selection = DetailedResult.ChangeType3SpinnerSelection(1)

                val (newState, list) = reducer.apply(state, selection)

                assertThat(list).hasSize(1)
                assertThat(list[0]).isOfAnyClassIn(DetailedAction.GetInfoBasicAction::class.java)

                assertThat(newState.themePack!!.type3!!.position).isEqualTo(1)

            }


        }

        "ToggleCheckbox" {
            assertThat(state.themePack!!.themes[0].checked).isFalse()
            val action = DetailedResult.ToggleCheckbox(0, true)
            val (newState, list) = reducer.apply(state, action)
            assertThat(newState.themePack!!.themes[0].checked).isTrue()
            assertThat(list).isEmpty()
        }

        "PositionResult" {

            val action = DetailedResult.InstalledStateResult.PositionResult(0)
            val (newState, list) = reducer.apply(state, action)
            assertThat(newState).isEqualTo(state)
            assertThat(list).hasSize(1)
            assertThat(list[0]).isEqualTo(DetailedAction.GetInfoAction(
                    appId = "appId",
                    targetAppId = "app1",
                    type1a = Type1Extension("a", true),
                    type1b = Type1Extension("a", true),
                    type1c = Type1Extension("a", true),
                    type2 = Type2Extension("a", true),
                    type3 = Type3Extension("a", true)
            ))

        }

        "AppIdResult" - {

            "App cannot be found" {
                val action = DetailedResult.InstalledStateResult.AppIdResult("idontexist")
                val (newState, list) = reducer.apply(state, action)
                assertThat(newState).isEqualTo(state)
                assertThat(list).isEmpty()
            }

            "App is found" {
                val action = DetailedResult.InstalledStateResult.AppIdResult("app1")
                val (newState, list) = reducer.apply(state, action)
                assertThat(newState).isEqualTo(state)
                assertThat(list).hasSize(1)
                assertThat(list[0]).isEqualTo(DetailedAction.GetInfoBasicAction(0))
            }


        }

        "CompilationStatusResult" - {

            "Start Flow" {
                val action = DetailedResult.CompilationStatusResult.StartFlow("app1")
                val (newState, list) = reducer.apply(state, action)
                assertThat(list).isEmpty()
                assertThat(newState.numberOfAllCompilations).isEqualTo(1)
            }

            "Start compilation" {
                val action = DetailedResult.CompilationStatusResult.StartCompilation("app1")
                val (newState, list) = reducer.apply(state, action)
                assertThat(list).isEmpty()
                assertThat(newState.themePack!!.themes[0].compilationState).isEqualTo(DetailedViewState.CompilationState.COMPILING)
            }

            "Start installation" {
                val action = DetailedResult.CompilationStatusResult.StartInstallation("app1")
                val (newState, list) = reducer.apply(state, action)
                assertThat(list).isEmpty()
                assertThat(newState.themePack!!.themes[0].compilationState).isEqualTo(DetailedViewState.CompilationState.INSTALLING)
            }

            "Failed compilation" {
                val exception = RuntimeException("My awesome error message")
                val action = DetailedResult.CompilationStatusResult.FailedCompilation("app1", exception)
                val (newState, list) = reducer.apply(state, action)
                assertThat(list).isEmpty()
                assertThat(newState.themePack!!.themes[0].compilationError).isSameAs(exception)
                assertThat(newState.toast()).isEqualTo("Compilation failed for app1")
            }

        }

        "oneTimeFunction test" {

            forAll(Gen.string()) { s ->
                val f = oneTimeFunction(s)
                f() shouldBe s
                f() shouldBe null
                true
            }

        }

    }

    private val state = DetailedViewState(
            themePack = DetailedViewState.ThemePack(
                    appId = "appId",
                    themeName = "Theme",
                    type3 = DetailedViewState.Type3(
                            position = 0,
                            data = listOf(
                                    Type3Extension("a", true),
                                    Type3Extension("b", false),
                                    Type3Extension("c", false)
                            )
                    ),
                    themes = listOf(
                            DetailedViewState.Theme(
                                    appId = "app1",
                                    name = "name",
                                    overlayId = "overlayId",
                                    type1a = DetailedViewState.Type1(
                                            position = 0,
                                            data = listOf(
                                                    Type1Extension("a", true),
                                                    Type1Extension("b", false),
                                                    Type1Extension("c", false)
                                            )
                                    ),

                                    type1b = DetailedViewState.Type1(
                                            position = 0,
                                            data = listOf(
                                                    Type1Extension("a", true),
                                                    Type1Extension("b", false),
                                                    Type1Extension("c", false)
                                            )
                                    ),
                                    type1c = DetailedViewState.Type1(
                                            position = 0,
                                            data = listOf(
                                                    Type1Extension("a", true),
                                                    Type1Extension("b", false),
                                                    Type1Extension("c", false)
                                            )
                                    ),
                                    type2 = DetailedViewState.Type2(
                                            position = 0,
                                            data = listOf(
                                                    Type2Extension("a", true),
                                                    Type2Extension("b", false),
                                                    Type2Extension("c", false)
                                            )
                                    ),
                                    checked = false,
                                    compilationError = null,
                                    compilationState = DetailedViewState.CompilationState.DEFAULT,
                                    enabledState = DetailedViewState.EnabledState.DISABLED,
                                    installedState = DetailedViewState.InstalledState.Unknown
                            )
                    )
            ),
            compilationError = null,
            numberOfAllCompilations = 0,
            numberOfFinishedCompilations = 0,
            toast = { null }
    )


}