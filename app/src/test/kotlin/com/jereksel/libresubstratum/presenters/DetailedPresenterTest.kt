package com.jereksel.libresubstratum.presenters

import com.jereksel.libresubstratum.activities.detailed.DetailedContract.Presenter
import com.jereksel.libresubstratum.activities.detailed.DetailedContract.View
import com.jereksel.libresubstratum.activities.detailed.DetailedPresenter
import com.jereksel.libresubstratum.adapters.ThemePackAdapterView
import com.jereksel.libresubstratum.data.Type1ExtensionToString
import com.jereksel.libresubstratum.data.Type2ExtensionToString
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.IThemeReader
import com.jereksel.libresubstratumlib.*
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.kotlintest.mock.mock
import io.kotlintest.specs.FunSpec
import org.mockito.ArgumentMatchers.anyString
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

    lateinit var presenter: Presenter

    override fun beforeEach() {
        MockitoAnnotations.initMocks(this)
        presenter = DetailedPresenter(packageManager, themeReader)
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
            whenever(packageManager.getCacheFolder()).thenReturn(File("/tmp"))
            whenever(themeReader.readThemePack(anyString())).thenReturn(emptyThemePack)
            whenever(themeReader.readThemePack(any<File>())).thenReturn(emptyThemePack)
            presenter.readTheme("themeId")
            verify(view).addThemes(emptyThemePack)
        }
        test("Read theme with apps") {
            val allApps = listOf("app1", "app2", "app3")
            val themes = allApps.map { Theme(it) }
            val installedApps = listOf("app1", "app3")
            val themePack = ThemePack(themes)
            val destThemePack = ThemePack(installedApps.map { Theme(it) })
            whenever(packageManager.getCacheFolder()).thenReturn(File("/tmp"))
            whenever(themeReader.readThemePack(anyString())).thenReturn(themePack)
            whenever(themeReader.readThemePack(any<File>())).thenReturn(themePack)
            allApps.forEach {
                whenever(packageManager.isPackageInstalled(it)).thenReturn(installedApps.contains(it))
            }
            presenter.readTheme("themeId")
            verify(view).addThemes(destThemePack)
        }
        test("Setting basic information") {
            val apps = listOf("a", "b", "c")
            apps.forEach {
                whenever(packageManager.getAppName(it)).thenReturn("name$it")
                whenever(packageManager.isPackageInstalled(it)).thenReturn(true)
            }
            val themes = ThemePack(apps.map { Theme(it) })
            whenever(themeReader.readThemePack(anyString())).thenReturn(themes)
            whenever(themeReader.readThemePack(any<File>())).thenReturn(themes)
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
            whenever(themeReader.readThemePack(anyString())).thenReturn(themes)
            whenever(themeReader.readThemePack(any<File>())).thenReturn(themes)
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
            whenever(themeReader.readThemePack(anyString())).thenReturn(themes)
            whenever(themeReader.readThemePack(any<File>())).thenReturn(themes)
            presenter.readTheme("id")

            presenter.setAdapterView(0, view)
            verify(view).type1aSpinner(type1a.extension.map(::Type1ExtensionToString), 0)
            verify(view).type1bSpinner(listOf(), 0)
            verify(view).type1cSpinner(listOf(), 0)
            verify(view).type2Spinner(listOf(), 0)

            presenter.setType1a(0, 1)
            reset(view)

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
            whenever(themeReader.readThemePack(anyString())).thenReturn(themes)
            whenever(themeReader.readThemePack(any<File>())).thenReturn(themes)
            presenter.readTheme("id")

            presenter.setAdapterView(0, view)
            verify(view).type1aSpinner(listOf(), 0)
            verify(view).type1bSpinner(type1b.extension.map(::Type1ExtensionToString), 0)
            verify(view).type1cSpinner(listOf(), 0)
            verify(view).type2Spinner(listOf(), 0)

            presenter.setType1b(0, 1)
            reset(view)

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
            whenever(themeReader.readThemePack(anyString())).thenReturn(themes)
            whenever(themeReader.readThemePack(any<File>())).thenReturn(themes)
            presenter.readTheme("id")

            presenter.setAdapterView(0, view)
            verify(view).type1aSpinner(listOf(), 0)
            verify(view).type1bSpinner(listOf(), 0)
            verify(view).type1cSpinner(type1c.extension.map(::Type1ExtensionToString), 0)
            verify(view).type2Spinner(listOf(), 0)

            presenter.setType1c(0, 1)
            reset(view)

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
            whenever(themeReader.readThemePack(anyString())).thenReturn(themes)
            whenever(themeReader.readThemePack(any<File>())).thenReturn(themes)
            presenter.readTheme("id")

            presenter.setAdapterView(0, view)
            verify(view).type1aSpinner(listOf(), 0)
            verify(view).type1bSpinner(listOf(), 0)
            verify(view).type1cSpinner(listOf(), 0)
            verify(view).type2Spinner(type2.extensions.map(::Type2ExtensionToString), 0)

            presenter.setType2(0, 1)
            reset(view)

            presenter.setAdapterView(0, view)
            verify(view).type1aSpinner(listOf(), 0)
            verify(view).type1bSpinner(listOf(), 0)
            verify(view).type1cSpinner(listOf(), 0)
            verify(view).type2Spinner(type2.extensions.map(::Type2ExtensionToString), 1)
        }
    }
}