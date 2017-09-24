package com.jereksel.libresubstratum.presenters

import android.graphics.drawable.Drawable
import com.jereksel.libresubstratum.activities.main.MainContract.View
import com.jereksel.libresubstratum.activities.main.MainPresenter
import com.jereksel.libresubstratum.data.InstalledTheme
import com.jereksel.libresubstratum.data.MainViewTheme
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.IThemeReader
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.kotlintest.specs.FunSpec
import org.junit.Ignore
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.stubbing.OngoingStubbing
import rx.android.plugins.RxAndroidPlugins
import rx.android.plugins.RxAndroidSchedulersHook
import rx.plugins.RxJavaHooks
import rx.schedulers.Schedulers
import java.io.File

class MainPresenterTest : FunSpec() {

    @Mock
    lateinit var view : View
    @Mock
    lateinit var packageManager : IPackageManager
    @Mock
    lateinit var themeReader: IThemeReader

    lateinit var presenter : MainPresenter

    override fun beforeEach() {
        MockitoAnnotations.initMocks(this)
        presenter = MainPresenter(packageManager, themeReader)
        presenter.setView(view)
        RxJavaHooks.clear()
        RxJavaHooks.setOnComputationScheduler { Schedulers.immediate() }
        RxJavaHooks.setOnIOScheduler { Schedulers.immediate() }

        val hook = object : RxAndroidSchedulersHook() {
            override fun getMainThreadScheduler() = Schedulers.immediate()
        }

        RxAndroidPlugins.getInstance().reset()
        RxAndroidPlugins.getInstance().registerSchedulersHook(hook)
    }

    init {
        test("filtering") {

            val myTable = table(
                    headers("packages", "number of passed packages"),
                    row(listOf(), 0),
                    row(listOf(packageFactory("1", "App 1", "Author 1")), 1),
                    row(listOf(packageFactory("1", "App 1", "Author 1"), packageFactory("2", "App 2", "Author 2")), 2)
            )
            forAll(myTable) { packages, num ->
                beforeEach()
                whenever(packageManager.getInstalledThemes()).thenReturn(packages)
                presenter.getApplications()
                verify(view).addApplications(argThat { size == num })
            }
        }
        test("Set isEncrypted from themeReader") {

            val app1Id = "app1"
            val app1Location = File("/app1")
            val app1Drawable = null

            val app2Id = "app2"
            val app2Location = File("/app2")
            val app2Drawable = null


            val installed = listOf(
                    packageFactory(app1Id, "Theme nr.1", "author1", app1Drawable),
                    packageFactory(app2Id, "Theme nr.2", "author2", app2Drawable)
            )

            whenever(packageManager.getAppLocation(app1Id)).thenReturn(app1Location)
            whenever(packageManager.getAppLocation(app2Id)).thenReturn(app2Location)

            whenever(packageManager.getInstalledThemes()).thenReturn(installed)

            whenever(themeReader.isThemeEncrypted(app1Location)).thenReturn(true)
            whenever(themeReader.isThemeEncrypted(app2Location)).thenReturn(false)

            presenter.getApplications()

            val expected = listOf(
                    MainViewTheme(app1Id, "Theme nr.1", "author1", app1Drawable, true),
                    MainViewTheme(app2Id, "Theme nr.2", "author2", app2Drawable, false)
            )

            verify(view).addApplications(expected)

        }
        test("removeView with nulls") {
            //We check if exception is not thrown
            presenter.removeView()
        }
//        test("When checking if is encrypted app location is passed") {
//            val appLocation = File("/data/applocation")
//            val appId = "app"
//            whenever(packageManager.getAppLocation(appId)).thenReturn(appLocation)
//            presenter.isThemeEncrypted(appId)
//            verify(themeReader).isThemeEncrypted(appLocation)
//        }
    }

    fun packageFactory(id: String, name: String, author: String, drawable: File? = null): InstalledTheme {

        return InstalledTheme(id, name, author, drawable)

//        val bundle = mock<Bundle> {
//            on { get(MainPresenter.SUBSTRATUM_NAME) } - name
//            on { getString(MainPresenter.SUBSTRATUM_NAME) } - name
//            on { get(MainPresenter.SUBSTRATUM_AUTHOR) } - author
//            on { getString(MainPresenter.SUBSTRATUM_AUTHOR) } - author
//            on { get(MainPresenter.SUBSTRATUM_LEGACY) } - Any()
//        }
//
//        return Application(id, bundle)
    }

}