package com.jereksel.libresubstratum.presenters

import com.jereksel.libresubstratum.activities.installed.InstalledContract
import com.jereksel.libresubstratum.activities.installed.InstalledPresenter
import com.jereksel.libresubstratum.data.InstalledOverlay
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.OverlayService
import com.nhaarman.mockito_kotlin.*
import io.kotlintest.specs.FunSpec
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.stubbing.OngoingStubbing
import rx.android.plugins.RxAndroidPlugins
import rx.android.plugins.RxAndroidSchedulersHook
import rx.plugins.RxJavaHooks
import rx.schedulers.Schedulers


class InstalledPresenterTest : FunSpec() {

    @Mock
    lateinit var view: InstalledContract.View
    @Mock
    lateinit var packageManager: IPackageManager
    @Mock
    lateinit var overlayService: OverlayService

    lateinit var presenter: InstalledPresenter

    override fun beforeEach() {
        MockitoAnnotations.initMocks(this)
        presenter = InstalledPresenter(packageManager, overlayService)
        presenter.setView(view)
        RxJavaHooks.clear()
        RxJavaHooks.setOnComputationScheduler { Schedulers.immediate() }

        val hook = object : RxAndroidSchedulersHook() {
            override fun getMainThreadScheduler() = Schedulers.immediate()
        }

        RxAndroidPlugins.getInstance().reset()
        RxAndroidPlugins.getInstance().registerSchedulersHook(hook)
    }

    init {

        test("GetInstalledOverlays should returned sorted overlays") {

            val captor: KArgumentCaptor<List<InstalledOverlay>> = argumentCaptor()

            val overlay1 = InstalledOverlay("id1", "id1", "Source A", mock(), "id1", "Target A", mock())
            val overlay2 = InstalledOverlay("id2", "id2", "Source B", mock(), "id2", "Target B", mock())

            whenever(packageManager.getInstalledOverlays()).thenReturn(listOf(overlay2, overlay1))

            presenter.getInstalledOverlays()
            verify(view).addOverlays(captor.capture())

            val l = captor.firstValue

            assertNotNull(l)
            assertEquals(2, l.size)
            assertEquals(listOf(overlay1, overlay2), l)

        }

        test("Snackbar is shown when overlays for com.android.systemui.* is enabled/disabled") {
            presenter.toggleOverlay("com.android.systemui.navbar", true)
            verify(view).showSnackBar(any(), any(), any())
        }

        test("Snackbar is not shown for other overlays") {
            presenter.toggleOverlay("com.jereksel.libresubstratum", true)
            verify(view, never()).showSnackBar(any(), any(), any())
        }

        test("Enabled parameter is passed to OverlayService") {

            forAll(table(
                    headers("Enabled"),
                    row(true),
                    row(false)
            )) { enabled ->
                presenter.toggleOverlay("app", enabled)
                verify(overlayService).toggleOverlay("app", enabled)
            }

        }


    }


//        test("filtering") {
//
//            val myTable = table(
//                    headers("packages", "number of passed packages"),
//                    row(listOf(), 0),
//                    row(listOf(packageFactory("1", "App 1", "Author 1")), 1),
//                    row(listOf(packageFactory("1", "App 1", "Author 1"), packageFactory("a", null, null)), 1)
//            )
//            forAll(myTable) { packages, num ->
//                beforeEach()
//                whenever(packageManager.getApplications()).thenReturn(packages)
//                presenter.getApplications()
//                verify(view).addApplications(argThat { size == num })
//            }
//        }
//        test("removeView with nulls") {
//            presenter.removeView()
//        }
//    }
//
//    fun packageFactory(overlayId: String, parentId: String, parentName: String, targetId: String, targetName: String,
//                       type1a: String? = null, type1b: String? = null, type1c: String? = null,
//                       type2: String? = null, type3: String? = null): Application {
//
//
//        val bundle = mock<Bundle> {
//            on { get(InstalledPresenter.metadataOverlayParent) } - parentId
//            on { getString(InstalledPresenter.metadataOverlayParent) } - parentId
//
//            on { get(InstalledPresenter.metadataOverlayTarget) } - targetId
//            on { getString(InstalledPresenter.metadataOverlayTarget) } - targetId
//
//            listOf(
//                    Pair(InstalledPresenter.metadataOverlayType1a, type1a),
//                    Pair(InstalledPresenter.metadataOverlayType1b, type1b),
//                    Pair(InstalledPresenter.metadataOverlayType1c, type1c),
//                    Pair(InstalledPresenter.metadataOverlayType2, type2),
//                    Pair(InstalledPresenter.metadataOverlayType3, type3)
//            ).forEach {
//                on { getString(it.first) } - it.second
//            }
//
//
//        }
//
//        return Application(overlayId, bundle)
//    }

//    fun packageFactory(id: String, name: String?, author: String?): Application {
//
//        val d : Drawable = mock()
//
//        val bundle = mock<Bundle> {
//            on { get(DetailedPresenter.SUBSTRATUM_NAME) } - name
//            on { getString(MainPresenter.SUBSTRATUM_NAME) } - name
//            on { get(MainPresenter.SUBSTRATUM_AUTHOR) } - author
//            on { getString(MainPresenter.SUBSTRATUM_AUTHOR) } - author
//            on { get(MainPresenter.SUBSTRATUM_LEGACY) } - Any()
//        }
//
////        return Application(id, bundle)
//    }

    infix operator fun <T> OngoingStubbing<T>.minus(t: T): OngoingStubbing<T> = thenReturn(t)

}