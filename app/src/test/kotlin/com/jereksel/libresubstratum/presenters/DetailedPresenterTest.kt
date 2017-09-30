package com.jereksel.libresubstratum.presenters

import com.jereksel.libresubstratum.activities.detailed.DetailedContract.Presenter
import com.jereksel.libresubstratum.activities.detailed.DetailedContract.View
import com.jereksel.libresubstratum.activities.detailed.DetailedPresenter
import com.jereksel.libresubstratum.adapters.ThemePackAdapterView
import com.jereksel.libresubstratum.data.Type1ExtensionToString
import com.jereksel.libresubstratum.data.Type2ExtensionToString
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.IThemeReader
import com.jereksel.libresubstratum.domain.OverlayInfo
import com.jereksel.libresubstratum.domain.OverlayService
import com.jereksel.libresubstratumlib.*
import com.nhaarman.mockito_kotlin.*
import io.kotlintest.mock.mock
import io.kotlintest.specs.FunSpec
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.android.plugins.RxAndroidPlugins
import rx.android.plugins.RxAndroidSchedulersHook
import rx.plugins.RxJavaHooks
import rx.schedulers.Schedulers
import java.io.File

class DetailedPresenterTest : FunSpec() {

    @Mock
    lateinit var view: View
    @Mock
    lateinit var packageManager: IPackageManager
    @Mock
    lateinit var themeReader: IThemeReader
    @Mock
    lateinit var overlayService: OverlayService

    lateinit var presenter: Presenter

    override fun beforeEach() {
        MockitoAnnotations.initMocks(this)
        presenter = DetailedPresenter(packageManager, themeReader, overlayService, mock(), mock(), mock(), compileThemeUseCase)
        presenter.setView(view)

        RxJavaHooks.clear()
        RxJavaHooks.setOnComputationScheduler { Schedulers.immediate() }

        val hook = object : RxAndroidSchedulersHook() {
            override fun getMainThreadScheduler() = Schedulers.immediate()
        }

        RxAndroidPlugins.getInstance().reset()
        RxAndroidPlugins.getInstance().registerSchedulersHook(hook)
    }

    override fun afterEach() {
        presenter.removeView()
    }

    init {
        test("Read empty theme") {
            val emptyThemePack = ThemePack(listOf())
            whenever(packageManager.getAppLocation("themeId")).thenReturn(File("/app.apk"))
            whenever(themeReader.readThemePack(File("/app.apk"))).thenReturn(emptyThemePack)
            presenter.readTheme("themeId")
            verify(view).addThemes(emptyThemePack)
        }
        test("Read theme with apps") {
            val allApps = listOf("app1", "app2", "app3")
            val themes = allApps.map { Theme(it) }
            val installedApps = listOf("app1", "app3")
            val themePack = ThemePack(themes)
            val destThemePack = ThemePack(installedApps.map { Theme(it) })
            whenever(packageManager.getAppLocation("themeId")).thenReturn(File("/app.apk"))
            whenever(themeReader.readThemePack(File("/app.apk"))).thenReturn(themePack)
            allApps.forEach {
                whenever(packageManager.isPackageInstalled(it)).thenReturn(installedApps.contains(it))
            }
            presenter.readTheme("themeId")
            verify(view).addThemes(destThemePack)
        }

        //ADAPTER
        test("After initialized once, themes are not imported again") {
            val themes = ThemePack()
            whenever(themeReader.readThemePack(anyOrNull())).thenReturn(themes)
            presenter.readTheme("id")
            verify(themeReader).readThemePack(anyOrNull())
            reset(themeReader)
            presenter.readTheme("id")
            verifyZeroInteractions(themeReader)
        }
        test("Setting basic information") {
            val apps = listOf("a", "b", "c")
            apps.forEach {
                whenever(packageManager.getAppName(it)).thenReturn("name$it")
                whenever(packageManager.isPackageInstalled(it)).thenReturn(true)
            }
            val themes = ThemePack(apps.map { Theme(it) })
            whenever(themeReader.readThemePack(anyOrNull())).thenReturn(themes)
            presenter.readTheme("id")

            val view = mock<ThemePackAdapterView>()
            presenter.setAdapterView(0, view)
            verify(view).setAppId("a")
            verify(view).setAppName("namea")

            val view2 = mock<ThemePackAdapterView>()
            presenter.setAdapterView(1, view2)
            verify(view2).setAppId("b")
            verify(view2).setAppName("nameb")
        }
        test("Setting checkbox test") {
            val apps = listOf("a", "b", "c")
            val view = mock<ThemePackAdapterView>()
            apps.forEach {
                whenever(packageManager.getAppName(it)).thenReturn("name$it")
                whenever(packageManager.isPackageInstalled(it)).thenReturn(true)
            }
            val themes = ThemePack(apps.map { Theme(it) })
            whenever(themeReader.readThemePack(anyOrNull())).thenReturn(themes)
            presenter.readTheme("id")

            presenter.setAdapterView(0, view)
            verify(view).setCheckbox(false)

            presenter.setCheckbox(0, true)
            reset(view)

            val view2 = mock<ThemePackAdapterView>()
            presenter.setAdapterView(0, view2)
            verify(view2).setCheckbox(true)
        }
        test("Setting type1a test") {
            val apps = listOf("a")
            val view = mock<ThemePackAdapterView>()
            apps.forEach {
                whenever(packageManager.getAppName(it)).thenReturn("name$it")
                whenever(packageManager.isPackageInstalled(it)).thenReturn(true)
            }
            val type1a = Type1Data(listOf(Type1Extension("name1", true), Type1Extension("name2", false)), "a")
            val themes = ThemePack(listOf(Theme("a", type1 = listOf(type1a))))
            whenever(themeReader.readThemePack(anyOrNull())).thenReturn(themes)
            presenter.readTheme("id")

            presenter.setAdapterView(0, view)
            verify(view).type1aSpinner(type1a.extension.map(::Type1ExtensionToString), 0)
            verify(view).type1bSpinner(listOf(), 0)
            verify(view).type1cSpinner(listOf(), 0)
            verify(view).type2Spinner(listOf(), 0)

            presenter.setType1a(0, 1)
            reset(view)

            presenter.setAdapterView(0, mock())
            presenter.setAdapterView(0, view)
            verify(view).type1aSpinner(type1a.extension.map(::Type1ExtensionToString), 1)
            verify(view).type1bSpinner(listOf(), 0)
            verify(view).type1cSpinner(listOf(), 0)
            verify(view).type2Spinner(listOf(), 0)
        }
        test("Setting type1b test") {
            val apps = listOf("a")
            val view = mock<ThemePackAdapterView>()
            apps.forEach {
                whenever(packageManager.getAppName(it)).thenReturn("name$it")
                whenever(packageManager.isPackageInstalled(it)).thenReturn(true)
            }
            val type1b = Type1Data(listOf(Type1Extension("name1", true), Type1Extension("name2", false)), "b")
            val themes = ThemePack(listOf(Theme("a", type1 = listOf(type1b))))
            whenever(themeReader.readThemePack(anyOrNull())).thenReturn(themes)
            presenter.readTheme("id")

            presenter.setAdapterView(0, view)
            verify(view).type1aSpinner(listOf(), 0)
            verify(view).type1bSpinner(type1b.extension.map(::Type1ExtensionToString), 0)
            verify(view).type1cSpinner(listOf(), 0)
            verify(view).type2Spinner(listOf(), 0)

            presenter.setType1b(0, 1)
            reset(view)

            presenter.setAdapterView(0, mock())
            presenter.setAdapterView(0, view)
            verify(view).type1aSpinner(listOf(), 0)
            verify(view).type1bSpinner(type1b.extension.map(::Type1ExtensionToString), 1)
            verify(view).type1cSpinner(listOf(), 0)
            verify(view).type2Spinner(listOf(), 0)
        }
        test("Setting type1c test") {
            val apps = listOf("a")
            val view = mock<ThemePackAdapterView>()
            apps.forEach {
                whenever(packageManager.getAppName(it)).thenReturn("name$it")
                whenever(packageManager.isPackageInstalled(it)).thenReturn(true)
            }
            val type1c = Type1Data(listOf(Type1Extension("name1", true), Type1Extension("name2", false)), "c")
            val themes = ThemePack(listOf(Theme("a", type1 = listOf(type1c))))
            whenever(themeReader.readThemePack(anyOrNull())).thenReturn(themes)
            presenter.readTheme("id")

            presenter.setAdapterView(0, view)
            verify(view).type1aSpinner(listOf(), 0)
            verify(view).type1bSpinner(listOf(), 0)
            verify(view).type1cSpinner(type1c.extension.map(::Type1ExtensionToString), 0)
            verify(view).type2Spinner(listOf(), 0)

            presenter.setType1c(0, 1)
            reset(view)

            presenter.setAdapterView(0, mock())
            presenter.setAdapterView(0, view)
            verify(view).type1aSpinner(listOf(), 0)
            verify(view).type1bSpinner(listOf(), 0)
            verify(view).type1cSpinner(type1c.extension.map(::Type1ExtensionToString), 1)
            verify(view).type2Spinner(listOf(), 0)
        }
        test("Setting type2 test") {
            val apps = listOf("a")
            val view = mock<ThemePackAdapterView>()
            apps.forEach {
                whenever(packageManager.getAppName(it)).thenReturn("name$it")
                whenever(packageManager.isPackageInstalled(it)).thenReturn(true)
            }
            val type2 = Type2Data(listOf(Type2Extension("name1", true), Type2Extension("name2", false)))
            val themes = ThemePack(listOf(Theme("a", type2 = type2)))
            whenever(themeReader.readThemePack(anyOrNull())).thenReturn(themes)
            presenter.readTheme("id")

            presenter.setAdapterView(0, view)
            verify(view).type1aSpinner(listOf(), 0)
            verify(view).type1bSpinner(listOf(), 0)
            verify(view).type1cSpinner(listOf(), 0)
            verify(view).type2Spinner(type2.extensions.map(::Type2ExtensionToString), 0)

            presenter.setType2(0, 1)
            reset(view)

            presenter.setAdapterView(0, mock())
            presenter.setAdapterView(0, view)
            verify(view).type1aSpinner(listOf(), 0)
            verify(view).type1bSpinner(listOf(), 0)
            verify(view).type1cSpinner(listOf(), 0)
            verify(view).type2Spinner(type2.extensions.map(::Type2ExtensionToString), 1)
        }
        test("Overlay id from theme data without spinners") {
            val apps = listOf("a")
            val view = mock<ThemePackAdapterView>()
            apps.forEach {
                whenever(packageManager.getAppName(it)).thenReturn("name$it")
                whenever(packageManager.isPackageInstalled(it)).thenReturn(true)
            }
            val type2 = Type2Data(listOf(Type2Extension("name1", true), Type2Extension("name2", false)))
            val themes = ThemePack(listOf(Theme("app1", type2 = type2)))
            whenever(themeReader.readThemePack(anyOrNull())).thenReturn(themes)
            whenever(packageManager.isPackageInstalled(anyOrNull())).thenReturn(true)
            presenter.readTheme("themeid")

            reset(packageManager)
            whenever(packageManager.getAppName("themeid")).thenReturn("theme1")
            presenter.setAdapterView(0, view)
            verify(packageManager).isPackageInstalled("app1.theme1")
        }
        test("Overlay id from theme data with type2 spinner") {
            val apps = listOf("app")
            val view = mock<ThemePackAdapterView>()
            apps.forEach {
                whenever(packageManager.getAppName(it)).thenReturn("name$it")
                whenever(packageManager.isPackageInstalled(it)).thenReturn(true)
            }
            val type2 = Type2Data(listOf(Type2Extension("name1", true), Type2Extension("name2", false)))
            val themes = ThemePack(listOf(Theme("app", type2 = type2)))
            whenever(themeReader.readThemePack(anyOrNull())).thenReturn(themes)
            whenever(packageManager.isPackageInstalled(anyOrNull())).thenReturn(true)
            presenter.readTheme("themeid")
            presenter.setType2(0, 1)

            reset(packageManager)
            whenever(packageManager.getAppName("themeid")).thenReturn("theme1")
            presenter.setAdapterView(0, view)
            verify(packageManager).isPackageInstalled("app.theme1.name2")
        }
        test("When overlay is not installed setInstalled is not called at all") {

            whenever(packageManager.getAppName("app1")).thenReturn("app1")
            whenever(packageManager.getAppName("themeid")).thenReturn("MyTheme")
            whenever(packageManager.isPackageInstalled("app1")).thenReturn(true)
            whenever(packageManager.isPackageInstalled("app1.themeid")).thenReturn(false)
            val themes = ThemePack(listOf(Theme("app1")))
            val view: ThemePackAdapterView = mock()
            whenever(themeReader.readThemePack(anyOrNull())).thenReturn(themes)

            presenter.readTheme("themeid")

            presenter.setAdapterView(0, view)

            verify(packageManager).isPackageInstalled("app1.MyTheme")
            verify(view, never()).setInstalled(null, null)
        }
        test("When overlay is up to date setInstalled is called with 2 nulls") {

            whenever(packageManager.getAppName("app1")).thenReturn("app1")
            whenever(packageManager.getAppName("themeid")).thenReturn("MyTheme")
            whenever(packageManager.isPackageInstalled("app1")).thenReturn(true)
            whenever(packageManager.isPackageInstalled("app1.MyTheme")).thenReturn(true)
            whenever(packageManager.getAppVersion("themeid")).thenReturn(Pair(1, "v1"))
            whenever(packageManager.getAppVersion("app1.MyTheme")).thenReturn(Pair(1, "v1"))
            whenever(overlayService.getOverlayInfo("app1.MyTheme")).thenReturn(OverlayInfo("app1.MyTheme", true))
            val themes = ThemePack(listOf(Theme("app1")))
            val view: ThemePackAdapterView = mock()
            whenever(themeReader.readThemePack(anyOrNull())).thenReturn(themes)

            presenter.readTheme("themeid")

            presenter.setAdapterView(0, view)

            verify(view).setInstalled(null, null)
        }
        test("When overlay is not up to date setInstalled is called with VersionNames") {

            whenever(packageManager.getAppName("app1")).thenReturn("app1")
            whenever(packageManager.getAppName("themeid")).thenReturn("MyTheme")
            whenever(packageManager.isPackageInstalled("app1")).thenReturn(true)
            whenever(packageManager.isPackageInstalled("app1.MyTheme")).thenReturn(true)
            whenever(packageManager.getAppVersion("themeid")).thenReturn(Pair(2, "v1.1"))
            whenever(packageManager.getAppVersion("app1.MyTheme")).thenReturn(Pair(1, "v1"))
            whenever(overlayService.getOverlayInfo("app1.MyTheme")).thenReturn(OverlayInfo("app1.MyTheme", true))
            val themes = ThemePack(listOf(Theme("app1")))
            val view: ThemePackAdapterView = mock()
            whenever(themeReader.readThemePack(anyOrNull())).thenReturn(themes)

            presenter.readTheme("themeid")

            presenter.setAdapterView(0, view)

            verify(view).setInstalled("v1", "v1.1")

        }
    }
}