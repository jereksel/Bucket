package com.jereksel.libresubstratum.domain.usecases

import com.jereksel.libresubstratum.domain.*
import com.jereksel.libresubstratum.presenters.PresenterTestUtils.initRxJava
import com.jereksel.libresubstratumlib.*
import com.nhaarman.mockito_kotlin.*
import io.kotlintest.specs.FunSpec
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.io.File

class CompileThemeUseCaseTest: FunSpec() {

    @Mock
    lateinit var packageManager: IPackageManager
    @Mock
    lateinit var themeCompiler: ThemeCompiler

    lateinit var useCase: ICompileThemeUseCase

    override fun beforeEach() {
        MockitoAnnotations.initMocks(this)
        useCase = CompileThemeUseCase(packageManager, themeCompiler)

        initRxJava()

        whenever(themeCompiler.compileTheme(any(), any())).thenReturn(File("/"))
    }

    init {

        test("Compilation without types") {

            val themePack = ThemePack(listOf(Theme("app1")))

            whenever(packageManager.getAppVersion("theme")).thenReturn(Pair(1, "1.0"))
            whenever(packageManager.getAppName("theme")).thenReturn("Theme")

            useCase.execute(
                    themePack,
                    "theme",
                    File("/"),
                    "app1",
                    null,
                    null,
                    null,
                    null,
                    null
            ).blockingFirst()

            val themeToCompile = ThemeToCompile("app1.Theme", "theme", "app1", listOf(), null, null, 1, "1.0")

            verify(themeCompiler).compileTheme(eq(themeToCompile), any())

        }

        test("Navbar overlay") {

            val themePack = ThemePack(listOf(Theme("com.android.systemui.navbar")))

            whenever(packageManager.getAppVersion("theme")).thenReturn(Pair(1, "1.0"))
            whenever(packageManager.getAppName("theme")).thenReturn("Theme")

            useCase.execute(
                    themePack,
                    "theme",
                    File("/"),
                    "com.android.systemui.navbar",
                    null,
                    null,
                    null,
                    null,
                    null
            ).blockingFirst()

            val themeToCompile = ThemeToCompile("com.android.systemui.navbar.Theme", "theme", "com.android.systemui", listOf(), null, null, 1, "1.0")

            verify(themeCompiler).compileTheme(eq(themeToCompile), any())
        }

        test("Compilation with default type2 extension") {

            val theme = Theme("app1", listOf(), Type2Data(listOf(Type2Extension("Type2a", true), Type2Extension("Type2b", false))))
            val themePack = ThemePack(listOf(theme))

            whenever(packageManager.getAppVersion("theme")).thenReturn(Pair(1, "1.0"))
            whenever(packageManager.getAppName("theme")).thenReturn("Theme")

            useCase.execute(
                    themePack,
                    "theme",
                    File("/"),
                    "app1",
                    null,
                    null,
                    null,
                    "Type2a",
                    null
            ).blockingFirst()

            val themeToCompile = ThemeToCompile("app1.Theme", "theme", "app1", listOf(), Type2Extension("Type2a", true), null, 1, "1.0")

            verify(themeCompiler).compileTheme(eq(themeToCompile), any())

        }

        test("Compilation with non-default type2 extension") {

            val theme = Theme("app1", listOf(), Type2Data(listOf(Type2Extension("Type2a", true), Type2Extension("Type2b", false))))
            val themePack = ThemePack(listOf(theme))

            whenever(packageManager.getAppVersion("theme")).thenReturn(Pair(1, "1.0"))
            whenever(packageManager.getAppName("theme")).thenReturn("Theme")

            useCase.execute(
                    themePack,
                    "theme",
                    File("/"),
                    "app1",
                    null,
                    null,
                    null,
                    "Type2b",
                    null
            ).blockingFirst()

            val themeToCompile = ThemeToCompile("app1.Theme.Type2b", "theme", "app1", listOf(), Type2Extension("Type2b", false), null, 1, "1.0")

            verify(themeCompiler).compileTheme(eq(themeToCompile), any())

        }

        test("Compilation with non-existing type2 extension") {

            val theme = Theme("app1", listOf(), Type2Data(listOf(Type2Extension("Type2a", true), Type2Extension("Type2b", false))))
            val themePack = ThemePack(listOf(theme))

            whenever(packageManager.getAppVersion("theme")).thenReturn(Pair(1, "1.0"))
            whenever(packageManager.getAppName("theme")).thenReturn("Theme")

            useCase.execute(
                    themePack,
                    "theme",
                    File("/"),
                    "app1",
                    null,
                    null,
                    null,
                    "Type2c",
                    null
            ).blockingFirst()

            val themeToCompile = ThemeToCompile("app1.Theme", "theme", "app1", listOf(), null, null, 1, "1.0")

            verify(themeCompiler).compileTheme(eq(themeToCompile), any())
        }

        test("Compilation with default type3 extension") {

            val theme = Theme("app1", listOf())
            val themePack = ThemePack(listOf(theme), Type3Data(listOf(Type3Extension("Type3a", true), Type3Extension("Type3b", false))))

            whenever(packageManager.getAppVersion("theme")).thenReturn(Pair(1, "1.0"))
            whenever(packageManager.getAppName("theme")).thenReturn("Theme")

            useCase.execute(
                    themePack,
                    "theme",
                    File("/"),
                    "app1",
                    null,
                    null,
                    null,
                    null,
                    "Type3a"
            ).blockingFirst()

            val themeToCompile = ThemeToCompile("app1.Theme", "theme", "app1", listOf(), null, Type3Extension("Type3a", true), 1, "1.0")

            verify(themeCompiler).compileTheme(eq(themeToCompile), any())

        }

        test("Compilation with non-default type3 extension") {

            val theme = Theme("app1", listOf())
            val themePack = ThemePack(listOf(theme), Type3Data(listOf(Type3Extension("Type3a", true), Type3Extension("Type3b", false))))

            whenever(packageManager.getAppVersion("theme")).thenReturn(Pair(1, "1.0"))
            whenever(packageManager.getAppName("theme")).thenReturn("Theme")

            useCase.execute(
                    themePack,
                    "theme",
                    File("/"),
                    "app1",
                    null,
                    null,
                    null,
                    null,
                    "Type3b"
            ).blockingFirst()

            val themeToCompile = ThemeToCompile("app1.Theme.Type3b", "theme", "app1", listOf(), null, Type3Extension("Type3b", false), 1, "1.0")

            verify(themeCompiler).compileTheme(eq(themeToCompile), any())

        }

        test("Compilation with non-existing type3 extension") {

            val theme = Theme("app1", listOf())
            val themePack = ThemePack(listOf(theme), Type3Data(listOf(Type3Extension("Type3a", true), Type3Extension("Type3b", false))))

            whenever(packageManager.getAppVersion("theme")).thenReturn(Pair(1, "1.0"))
            whenever(packageManager.getAppName("theme")).thenReturn("Theme")

            useCase.execute(
                    themePack,
                    "theme",
                    File("/"),
                    "app1",
                    null,
                    null,
                    null,
                    null,
                    "Type3c"
            ).blockingFirst()

            val themeToCompile = ThemeToCompile("app1.Theme", "theme", "app1", listOf(), null, null, 1, "1.0")

            verify(themeCompiler).compileTheme(eq(themeToCompile), any())
        }

        //TYPE 1

        test("Compilation with default type1a extension") {

            val theme = Theme("app1", listOf(Type1Data(listOf(Type1Extension("Type1aa", true), Type1Extension("Type1ab", false)), "a")))
            val themePack = ThemePack(listOf(theme))

            whenever(packageManager.getAppVersion("theme")).thenReturn(Pair(1, "1.0"))
            whenever(packageManager.getAppName("theme")).thenReturn("Theme")

            useCase.execute(
                    themePack,
                    "theme",
                    File("/"),
                    "app1",
                    "Type1aa",
                    null,
                    null,
                    null,
                    null
            ).blockingFirst()

            val themeToCompile = ThemeToCompile("app1.Theme", "theme", "app1", listOf(Type1DataToCompile(Type1Extension("Type1aa", true), "a")), null, null, 1, "1.0")

            verify(themeCompiler).compileTheme(eq(themeToCompile), any())

        }

        test("Compilation with non-default type1a extension") {

            val theme = Theme("app1", listOf(Type1Data(listOf(Type1Extension("Type1aa", true), Type1Extension("Type1ab", false)), "a")))
            val themePack = ThemePack(listOf(theme))

            whenever(packageManager.getAppVersion("theme")).thenReturn(Pair(1, "1.0"))
            whenever(packageManager.getAppName("theme")).thenReturn("Theme")

            useCase.execute(
                    themePack,
                    "theme",
                    File("/"),
                    "app1",
                    "Type1ab",
                    null,
                    null,
                    null,
                    null
            ).blockingFirst()

            val themeToCompile = ThemeToCompile("app1.Theme.Type1ab", "theme", "app1", listOf(Type1DataToCompile(Type1Extension("Type1ab", false), "a")), null, null, 1, "1.0")

            verify(themeCompiler).compileTheme(eq(themeToCompile), any())

        }

        test("Compilation with non-existing type1a extension") {

            val theme = Theme("app1", listOf(Type1Data(listOf(Type1Extension("Type1aa", true), Type1Extension("Type1ab", false)), "a")))
            val themePack = ThemePack(listOf(theme))

            whenever(packageManager.getAppVersion("theme")).thenReturn(Pair(1, "1.0"))
            whenever(packageManager.getAppName("theme")).thenReturn("Theme")

            useCase.execute(
                    themePack,
                    "theme",
                    File("/"),
                    "app1",
                    "Type1ac",
                    null,
                    null,
                    null,
                    null
            ).blockingFirst()

            val themeToCompile = ThemeToCompile("app1.Theme", "theme", "app1", listOf(), null, null, 1, "1.0")

            verify(themeCompiler).compileTheme(eq(themeToCompile), any())

        }


        //TYPE1b


        test("Compilation with default type1b extension") {

            val theme = Theme("app1", listOf(Type1Data(listOf(Type1Extension("Type1ba", true), Type1Extension("Type1bb", false)), "b")))
            val themePack = ThemePack(listOf(theme))

            whenever(packageManager.getAppVersion("theme")).thenReturn(Pair(1, "1.0"))
            whenever(packageManager.getAppName("theme")).thenReturn("Theme")

            useCase.execute(
                    themePack,
                    "theme",
                    File("/"),
                    "app1",
                    null,
                    "Type1ba",
                    null,
                    null,
                    null
            ).blockingFirst()

            val themeToCompile = ThemeToCompile("app1.Theme", "theme", "app1", listOf(Type1DataToCompile(Type1Extension("Type1ba", true), "b")), null, null, 1, "1.0")

            verify(themeCompiler).compileTheme(eq(themeToCompile), any())

        }

        test("Compilation with non-default type1b extension") {

            val theme = Theme("app1", listOf(Type1Data(listOf(Type1Extension("Type1ba", true), Type1Extension("Type1bb", false)), "b")))
            val themePack = ThemePack(listOf(theme))

            whenever(packageManager.getAppVersion("theme")).thenReturn(Pair(1, "1.0"))
            whenever(packageManager.getAppName("theme")).thenReturn("Theme")

            useCase.execute(
                    themePack,
                    "theme",
                    File("/"),
                    "app1",
                    null,
                    "Type1bb",
                    null,
                    null,
                    null
            ).blockingFirst()

            val themeToCompile = ThemeToCompile("app1.Theme.Type1bb", "theme", "app1", listOf(Type1DataToCompile(Type1Extension("Type1bb", false), "b")), null, null, 1, "1.0")

            verify(themeCompiler).compileTheme(eq(themeToCompile), any())

        }

        test("Compilation with non-existing type1b extension") {

            val theme = Theme("app1", listOf(Type1Data(listOf(Type1Extension("Type1ba", true), Type1Extension("Type1bb", false)), "b")))
            val themePack = ThemePack(listOf(theme))

            whenever(packageManager.getAppVersion("theme")).thenReturn(Pair(1, "1.0"))
            whenever(packageManager.getAppName("theme")).thenReturn("Theme")

            useCase.execute(
                    themePack,
                    "theme",
                    File("/"),
                    "app1",
                    null,
                    "Type1bc",
                    null,
                    null,
                    null
            ).blockingFirst()

            val themeToCompile = ThemeToCompile("app1.Theme", "theme", "app1", listOf(), null, null, 1, "1.0")

            verify(themeCompiler).compileTheme(eq(themeToCompile), any())

        }

        //TYPE 1c

        test("Compilation with default type1c extension") {

            val theme = Theme("app1", listOf(Type1Data(listOf(Type1Extension("Type1ca", true), Type1Extension("Type1cb", false)), "c")))
            val themePack = ThemePack(listOf(theme))

            whenever(packageManager.getAppVersion("theme")).thenReturn(Pair(1, "1.0"))
            whenever(packageManager.getAppName("theme")).thenReturn("Theme")

            useCase.execute(
                    themePack,
                    "theme",
                    File("/"),
                    "app1",
                    null,
                    null,
                    "Type1ca",
                    null,
                    null
            ).blockingFirst()

            val themeToCompile = ThemeToCompile("app1.Theme", "theme", "app1", listOf(Type1DataToCompile(Type1Extension("Type1ca", true), "c")), null, null, 1, "1.0")

            verify(themeCompiler).compileTheme(eq(themeToCompile), any())

        }

        test("Compilation with non-default type1c extension") {

            val theme = Theme("app1", listOf(Type1Data(listOf(Type1Extension("Type1ca", true), Type1Extension("Type1cb", false)), "c")))
            val themePack = ThemePack(listOf(theme))

            whenever(packageManager.getAppVersion("theme")).thenReturn(Pair(1, "1.0"))
            whenever(packageManager.getAppName("theme")).thenReturn("Theme")

            useCase.execute(
                    themePack,
                    "theme",
                    File("/"),
                    "app1",
                    null,
                    null,
                    "Type1cb",
                    null,
                    null
            ).blockingFirst()

            val themeToCompile = ThemeToCompile("app1.Theme.Type1cb", "theme", "app1", listOf(Type1DataToCompile(Type1Extension("Type1cb", false), "c")), null, null, 1, "1.0")

            verify(themeCompiler).compileTheme(eq(themeToCompile), any())

        }

        test("Compilation with non-existing type1c extension") {

            val theme = Theme("app1", listOf(Type1Data(listOf(Type1Extension("Type1ca", true), Type1Extension("Type1cb", false)), "c")))
            val themePack = ThemePack(listOf(theme))

            whenever(packageManager.getAppVersion("theme")).thenReturn(Pair(1, "1.0"))
            whenever(packageManager.getAppName("theme")).thenReturn("Theme")

            useCase.execute(
                    themePack,
                    "theme",
                    File("/"),
                    "app1",
                    null,
                    null,
                    "Type1cc",
                    null,
                    null
            ).blockingFirst()

            val themeToCompile = ThemeToCompile("app1.Theme", "theme", "app1", listOf(), null, null, 1, "1.0")

            verify(themeCompiler).compileTheme(eq(themeToCompile), any())

        }


        test("Compilation with all extensions") {

            val type1as = Type1Data(listOf(Type1Extension("Type1aa", true), Type1Extension("Type1ab", false)), "a")
            val type1bs = Type1Data(listOf(Type1Extension("Type1ba", true), Type1Extension("Type1bb", false)), "b")
            val type1cs = Type1Data(listOf(Type1Extension("Type1ca", true), Type1Extension("Type1cb", false)), "c")

            val type2s = Type2Data(listOf(Type2Extension("Type2a", true), Type2Extension("Type2b", false)))
            val type3s = Type3Data(listOf(Type3Extension("Type3a", true), Type3Extension("Type3b", false)))

            val type1s = listOf(type1as, type1bs, type1cs)

            val theme = Theme("app1", type1s, type2s)
            val themePack = ThemePack(listOf(theme), type3s)

            whenever(packageManager.getAppVersion("theme")).thenReturn(Pair(1, "1.0"))
            whenever(packageManager.getAppName("theme")).thenReturn("Theme")

            useCase.execute(
                    themePack,
                    "theme",
                    File("/"),
                    "app1",
                    "Type1ab",
                    "Type1bb",
                    "Type1cb",
                    "Type2b",
                    "Type3b"
            ).blockingFirst()

            val themeToCompile = ThemeToCompile("app1.Theme.Type1abType1bbType1cb.Type2b.Type3b", "theme", "app1", listOf(
                    Type1DataToCompile(Type1Extension("Type1ab", false), "a"),
                    Type1DataToCompile(Type1Extension("Type1bb", false), "b"),
                    Type1DataToCompile(Type1Extension("Type1cb", false), "c")
            ), Type2Extension("Type2b", false), Type3Extension("Type3b", false), 1, "1.0")

            verify(themeCompiler).compileTheme(eq(themeToCompile), any())
        }


    }

}
