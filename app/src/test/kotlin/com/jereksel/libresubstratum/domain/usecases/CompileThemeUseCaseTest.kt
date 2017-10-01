package com.jereksel.libresubstratum.domain.usecases

import com.jereksel.libresubstratum.domain.*
import com.jereksel.libresubstratumlib.Theme
import com.jereksel.libresubstratumlib.ThemePack
import com.jereksel.libresubstratumlib.ThemeToCompile
import com.nhaarman.mockito_kotlin.*
import io.kotlintest.specs.FunSpec
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.android.plugins.RxAndroidPlugins
import rx.android.plugins.RxAndroidSchedulersHook
import rx.plugins.RxJavaHooks
import rx.schedulers.Schedulers
import java.io.File

class CompileThemeUseCaseTest: FunSpec() {

    @Mock
    lateinit var packageManager: IPackageManager
    @Mock
    lateinit var themeReader: IThemeReader
    @Mock
    lateinit var overlayService: OverlayService
    @Mock
    lateinit var activityProxy: IActivityProxy
    @Mock
    lateinit var themeCompiler: ThemeCompiler
    @Mock
    lateinit var themeExtractor: ThemeExtractor

    lateinit var useCase: ICompileThemeUseCase

    override fun beforeEach() {
        MockitoAnnotations.initMocks(this)
        useCase = CompileThemeUseCase(packageManager, themeReader, overlayService, activityProxy, themeCompiler, themeExtractor)
//        presenter1 = spy(DetailedPresenter(packageManager, themeReader, overlayService, mock(), mock(), mock(), compileThemeUseCase))
//        presenter = presenter1
//        presenter.setView(view)

        RxJavaHooks.clear()
        RxJavaHooks.setOnComputationScheduler { Schedulers.immediate() }

        val hook = object : RxAndroidSchedulersHook() {
            override fun getMainThreadScheduler() = Schedulers.immediate()
        }

        RxAndroidPlugins.getInstance().reset()
        RxAndroidPlugins.getInstance().registerSchedulersHook(hook)
    }

    init {

        test("Compilation without types") {

            val theme = ThemePack(listOf(Theme("app1")))
//            useCase.execute(
//                    theme,
//
//            )

            whenever(packageManager.getAppVersion("theme")).thenReturn(Pair(1, "1.0"))
            whenever(themeReader.readThemePack(anyOrNull())).thenReturn(theme)
            whenever(packageManager.getAppName("theme")).thenReturn("Theme")

            useCase.execute(
                    theme.themes[0],
                    "theme",
                    File("/"),
                    "app1",
                    null,
                    null,
                    null,
                    null,
                    null
            ).toBlocking().first()

            val themeToCompile = ThemeToCompile("app1.Theme", "theme", "app1", listOf(), null, null, 1, "1.0")

            verify(themeCompiler).compileTheme(eq(themeToCompile), any())

        }





    }

}
