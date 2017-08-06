package com.jereksel.libresubstratum.presenters

import android.graphics.drawable.Drawable
import android.os.Bundle
import com.google.common.io.Files
import com.jereksel.libresubstratum.activities.detailed.DetailedPresenter
import com.jereksel.libresubstratum.activities.installed.InstalledContract
import com.jereksel.libresubstratum.activities.installed.InstalledPresenter
import com.jereksel.libresubstratum.data.Application
import com.jereksel.libresubstratum.domain.IPackageManager
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.kotlintest.specs.FunSpec
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.stubbing.OngoingStubbing
import rx.android.plugins.RxAndroidPlugins
import rx.android.plugins.RxAndroidSchedulersHook
import rx.plugins.RxJavaHooks
import rx.schedulers.Schedulers
import java.io.File

class InstalledPresenterTest : FunSpec() {

    @Mock
    lateinit var view : InstalledContract.View
    @Mock
    lateinit var packageManager : IPackageManager

    lateinit var presenter : InstalledPresenter

    override fun beforeEach() {
        MockitoAnnotations.initMocks(this);
        presenter = InstalledPresenter(packageManager);
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

    }

    init {




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
    }

    fun packageFactory(overlayId: String, parentId: String, parentName: String, targetId: String, targetName: String,
                       type1a: String? = null, type1b: String? = null, type1c: String? = null,
                       type2: String? = null, type3: String? = null): Application {


        val bundle = mock<Bundle> {
            on { get(InstalledPresenter.metadataOverlayParent) } - parentId
            on { getString(InstalledPresenter.metadataOverlayParent) } - parentId

            on { get(InstalledPresenter.metadataOverlayTarget) } - targetId
            on { getString(InstalledPresenter.metadataOverlayTarget) } - targetId

            listOf(
                    Pair(InstalledPresenter.metadataOverlayType1a, type1a),
                    Pair(InstalledPresenter.metadataOverlayType1b, type1b),
                    Pair(InstalledPresenter.metadataOverlayType1c, type1c),
                    Pair(InstalledPresenter.metadataOverlayType2, type2),
                    Pair(InstalledPresenter.metadataOverlayType3, type3)
            ).forEach {
                on { getString(it.first) } - it.second
            }


        }

        return Application(overlayId, bundle)
    }

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