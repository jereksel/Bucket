package com.jereksel.libresubstratum.domain.usecases

import com.jereksel.libresubstratum.domain.*
import com.jereksel.libresubstratumlib.*
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

            val themePack = ThemePack(listOf(Theme("app1")))

            whenever(packageManager.getAppVersion("theme")).thenReturn(Pair(1, "1.0"))
            whenever(themeReader.readThemePack(anyOrNull())).thenReturn(themePack)
            whenever(packageManager.getAppName("theme")).thenReturn("Theme")

            useCase.execute(
                    themePack.themes[0],
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

        test("Compilation with default type2 extension") {

            val theme = Theme("app1", listOf(), Type2Data(listOf(Type2Extension("Type2a", true), Type2Extension("Type2b", false))))
            val themePack = ThemePack(listOf(theme))

            whenever(packageManager.getAppVersion("theme")).thenReturn(Pair(1, "1.0"))
            whenever(themeReader.readThemePack(anyOrNull())).thenReturn(themePack)
            whenever(packageManager.getAppName("theme")).thenReturn("Theme")

            useCase.execute(
                    themePack.themes[0],
                    "theme",
                    File("/"),
                    "app1",
                    null,
                    null,
                    null,
                    "Type2a",
                    null
            ).toBlocking().first()

            val themeToCompile = ThemeToCompile("app1.Theme", "theme", "app1", listOf(), Type2Extension("Type2a", true), null, 1, "1.0")

            verify(themeCompiler).compileTheme(eq(themeToCompile), any())

        }

        test("Compilation with non-default type2 extension") {

            val theme = Theme("app1", listOf(), Type2Data(listOf(Type2Extension("Type2a", true), Type2Extension("Type2b", false))))
            val themePack = ThemePack(listOf(theme))

            whenever(packageManager.getAppVersion("theme")).thenReturn(Pair(1, "1.0"))
            whenever(themeReader.readThemePack(anyOrNull())).thenReturn(themePack)
            whenever(packageManager.getAppName("theme")).thenReturn("Theme")

            useCase.execute(
                    themePack.themes[0],
                    "theme",
                    File("/"),
                    "app1",
                    null,
                    null,
                    null,
                    "Type2b",
                    null
            ).toBlocking().first()

            val themeToCompile = ThemeToCompile("app1.Theme.Type2b", "theme", "app1", listOf(), Type2Extension("Type2b", false), null, 1, "1.0")

            verify(themeCompiler).compileTheme(eq(themeToCompile), any())

        }

        test("Compilation with non-existing type2 extension") {


            val theme = Theme("app1", listOf(), Type2Data(listOf(Type2Extension("Type2a", true), Type2Extension("Type2b", false))))
            val themePack = ThemePack(listOf(theme))

            whenever(packageManager.getAppVersion("theme")).thenReturn(Pair(1, "1.0"))
            whenever(themeReader.readThemePack(anyOrNull())).thenReturn(themePack)
            whenever(packageManager.getAppName("theme")).thenReturn("Theme")

            useCase.execute(
                    themePack.themes[0],
                    "theme",
                    File("/"),
                    "app1",
                    null,
                    null,
                    null,
                    "Type2c",
                    null
            ).toBlocking().first()

            val themeToCompile = ThemeToCompile("app1.Theme", "theme", "app1", listOf(), null, null, 1, "1.0")

            verify(themeCompiler).compileTheme(eq(themeToCompile), any())
        }

        test("Compilation with default type3 extension") {

            val theme = Theme("app1", listOf())
            val themePack = ThemePack(listOf(theme), Type3Data(listOf(Type3Extension("Type3a", true), Type3Extension("Type3b", false))))

            whenever(packageManager.getAppVersion("theme")).thenReturn(Pair(1, "1.0"))
            whenever(themeReader.readThemePack(anyOrNull())).thenReturn(themePack)
            whenever(packageManager.getAppName("theme")).thenReturn("Theme")

            useCase.execute(
                    themePack.themes[0],
                    "theme",
                    File("/"),
                    "app1",
                    null,
                    null,
                    null,
                    null,
                    "Type3a"
            ).toBlocking().first()

            val themeToCompile = ThemeToCompile("app1.Theme", "theme", "app1", listOf(), null, Type3Extension("Type3a", true), 1, "1.0")

            verify(themeCompiler).compileTheme(eq(themeToCompile), any())

        }

        test("Compilation with non-default type3 extension") {

            val theme = Theme("app1", listOf())
            val themePack = ThemePack(listOf(theme), Type3Data(listOf(Type3Extension("Type3a", true), Type3Extension("Type3b", false))))

            whenever(packageManager.getAppVersion("theme")).thenReturn(Pair(1, "1.0"))
            whenever(themeReader.readThemePack(anyOrNull())).thenReturn(themePack)
            whenever(packageManager.getAppName("theme")).thenReturn("Theme")

            useCase.execute(
                    themePack.themes[0],
                    "theme",
                    File("/"),
                    "app1",
                    null,
                    null,
                    null,
                    null,
                    "Type3b"
            ).toBlocking().first()

            val themeToCompile = ThemeToCompile("app1.Theme.Type3b", "theme", "app1", listOf(), null, Type3Extension("Type3b", false), 1, "1.0")

            verify(themeCompiler).compileTheme(eq(themeToCompile), any())

        }

        test("Compilation with non-existing type3 extension") {

            val theme = Theme("app1", listOf())
            val themePack = ThemePack(listOf(theme), Type3Data(listOf(Type3Extension("Type3a", true), Type3Extension("Type3b", false))))

            whenever(packageManager.getAppVersion("theme")).thenReturn(Pair(1, "1.0"))
            whenever(themeReader.readThemePack(anyOrNull())).thenReturn(themePack)
            whenever(packageManager.getAppName("theme")).thenReturn("Theme")

            useCase.execute(
                    themePack.themes[0],
                    "theme",
                    File("/"),
                    "app1",
                    null,
                    null,
                    null,
                    null,
                    "Type3c"
            ).toBlocking().first()

            val themeToCompile = ThemeToCompile("app1.Theme", "theme", "app1", listOf(), null, null, 1, "1.0")

            verify(themeCompiler).compileTheme(eq(themeToCompile), any())
        }

    }

}
