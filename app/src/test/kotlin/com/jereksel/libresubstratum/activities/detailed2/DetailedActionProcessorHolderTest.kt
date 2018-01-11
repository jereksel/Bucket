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

package com.jereksel.libresubstratum.activities.detailed2

import com.jereksel.libresubstratum.Utils.initOS
import com.jereksel.libresubstratum.domain.ClipboardManager
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.OverlayService
import com.jereksel.libresubstratum.domain.usecases.ICompileThemeUseCase
import com.jereksel.libresubstratum.domain.usecases.IGetThemeInfoUseCase
import com.jereksel.libresubstratum.presenters.PresenterTestUtils.initRxJava
import com.jereksel.libresubstratumlib.ThemePack
import com.jereksel.libresubstratumlib.Type3Data
import com.jereksel.libresubstratumlib.Type3Extension
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.rubylichtenstein.rxtest.assertions.assertThat
import com.rubylichtenstein.rxtest.assertions.shouldEmit
import com.rubylichtenstein.rxtest.assertions.shouldHave
import com.rubylichtenstein.rxtest.extentions.test
import com.rubylichtenstein.rxtest.matchers.Matcher
import com.rubylichtenstein.rxtest.matchers.complete
import com.rubylichtenstein.rxtest.matchers.noErrors
import io.kotlintest.specs.FunSpec
import io.reactivex.Observable
import io.reactivex.observers.BaseTestConsumer
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class DetailedActionProcessorHolderTest: FunSpec() {

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

        test("Load theme info test - null type3") {
            val appId = "appId"
            val theme = ThemePack(listOf(), null)
            whenever(getThemeInfoUseCase.getThemeInfo(appId)).thenReturn(theme)

            Observable.just(DetailedAction.InitialAction(appId))
                    .compose(presenter.loadListProcessor)
                    .test {
                        it shouldEmit DetailedResult.ListLoaded(listOf())
                        it should complete()
                        it shouldHave noErrors()
                    }

        }

        test("Load theme info test - empty type3") {
            val appId = "appId"
            val theme = ThemePack(listOf(), Type3Data())
            whenever(getThemeInfoUseCase.getThemeInfo(appId)).thenReturn(theme)


            Observable.just(DetailedAction.InitialAction(appId))
                    .compose(presenter.loadListProcessor)
                    .test {
                        it shouldEmit DetailedResult.ListLoaded(listOf())
                        it should complete()
                        it shouldHave noErrors()
                    }

        }

        test("Load theme info test - nonnull type3") {
            val appId = "appId"
            val theme = ThemePack(listOf(), Type3Data(listOf(Type3Extension("Default", true), Type3Extension("Oreo", false))))
            whenever(getThemeInfoUseCase.getThemeInfo(appId)).thenReturn(theme)


            Observable.just(DetailedAction.InitialAction(appId))
                    .compose(presenter.loadListProcessor)
                    .test {
                        it shouldEmit DetailedResult.ListLoaded(listOf(Type3Extension("Default", true), Type3Extension("Oreo", false)))
                        it should complete()
                        it shouldHave noErrors()
                    }

        }



    }

    infix fun <T, U : BaseTestConsumer<T, U>> BaseTestConsumer<T, U>.should(matcher: Matcher<BaseTestConsumer<T, U>>)
            = assertThat<T, U>(this, matcher)

}