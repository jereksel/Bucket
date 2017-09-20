package com.jereksel.libresubstratum.presenters

import com.jereksel.libresubstratum.activities.detailed.DetailedContract.Presenter
import com.jereksel.libresubstratum.activities.detailed.DetailedContract.View
import com.jereksel.libresubstratum.activities.detailed.DetailedPresenter
import com.jereksel.libresubstratumlib.Theme
import com.jereksel.libresubstratumlib.ThemePack
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.IThemeReader
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
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
    }
}