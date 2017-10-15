package com.jereksel.libresubstratum.presenters

import com.jereksel.libresubstratum.activities.main.MainContract.View
import com.jereksel.libresubstratum.activities.main.MainPresenter
import com.jereksel.libresubstratum.data.InstalledTheme
import com.jereksel.libresubstratum.data.MainViewTheme
import com.jereksel.libresubstratum.domain.*
import com.jereksel.libresubstratum.presenters.PresenterTestUtils.initRxJava
import com.nhaarman.mockito_kotlin.*
import io.kotlintest.specs.FunSpec
import org.junit.Assert
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.io.File
import java.util.concurrent.FutureTask

class MainPresenterTest : FunSpec() {

    @Mock
    lateinit var view : View
    @Mock
    lateinit var packageManager : IPackageManager
    @Mock
    lateinit var themeReader: IThemeReader
    @Mock
    lateinit var overlayService: OverlayService
    @Mock
    lateinit var keyFinder: IKeyFinder

    lateinit var presenter : MainPresenter

    override fun beforeEach() {
        MockitoAnnotations.initMocks(this)
        presenter = MainPresenter(packageManager, themeReader, overlayService, mock(), keyFinder)
        presenter.setView(view)

        initRxJava()
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
            val app1Drawable = null

            val app2Id = "app2"
            val app2Drawable = null


            val installed = listOf(
                    packageFactory(app1Id, "Theme nr.1", "author1", app1Drawable),
                    packageFactory(app2Id, "Theme nr.2", "author2", app2Drawable)
            )

            whenever(packageManager.getInstalledThemes()).thenReturn(installed)

            whenever(themeReader.isThemeEncrypted(app1Id)).thenReturn(true)
            whenever(themeReader.isThemeEncrypted(app2Id)).thenReturn(false)

            presenter.getApplications()

            val expected = listOf(
                    MainViewTheme(app1Id, "Theme nr.1", "author1", app1Drawable, true),
                    MainViewTheme(app2Id, "Theme nr.2", "author2", app2Drawable, false)
            )

            val argumentCaptor = argumentCaptor<List<MainViewTheme>>()
            verify(view).addApplications(argumentCaptor.capture())
            Assert.assertEquals(listOf(true, false), argumentCaptor.firstValue.map { it.isEncrypted })

        }
        test("If overlayService returns non empty list of permissions they're passed to view") {
            val perms = listOf("perm1", "perm2")
            whenever(overlayService.requiredPermissions()).thenReturn(perms)
            presenter.checkPermissions()
            verify(view).requestPermissions(perms)
        }
        test("If overlayService returns empty list of permissions and non-empty message, message is shown") {
            val message = "Do something"
            whenever(overlayService.requiredPermissions()).thenReturn(listOf())
            whenever(overlayService.additionalSteps()).thenReturn(message)
            presenter.checkPermissions()
            verify(view, never()).requestPermissions(any())
            verify(view).showUndismissableDialog(message)
        }
        test("If overlayService returns empty list and null message, nothing is shown") {
            whenever(overlayService.requiredPermissions()).thenReturn(listOf())
            whenever(overlayService.additionalSteps()).thenReturn(null)
            presenter.checkPermissions()
            verify(view, never()).requestPermissions(any())
            verify(view, never()).showUndismissableDialog(anyOrNull())
        }
        test("removeView with nulls") {
            //We check if exception is not thrown
            presenter.removeView()
        }
        test("No FC when InvalidOverlayService is returned") {

            val service = InvalidOverlayService("Something bad happeded")

            whenever(overlayService.requiredPermissions()).thenAnswer { service.requiredPermissions() }
            whenever(overlayService.additionalSteps()).thenAnswer { service.additionalSteps() }

            presenter.checkPermissions()
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

        return InstalledTheme(id, name, author, FutureTask { drawable })

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