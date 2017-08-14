package com.jereksel.libresubstratum.presenters

import com.jereksel.libresubstratum.activities.main.MainContract.View
import com.jereksel.libresubstratum.activities.main.MainPresenter
import com.jereksel.libresubstratum.data.InstalledTheme
import com.jereksel.libresubstratum.domain.IPackageManager
import com.nhaarman.mockito_kotlin.argThat
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

class MainPresenterTest : FunSpec() {

    @Mock
    lateinit var view : View
    @Mock
    lateinit var packageManager : IPackageManager

    lateinit var presenter : MainPresenter

    override fun beforeEach() {
        MockitoAnnotations.initMocks(this);
        presenter = MainPresenter(packageManager);
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
        test("removeView with nulls") {
            //We check if exception is not thrown
            presenter.removeView()
        }
    }

    fun packageFactory(id: String, name: String, author: String): InstalledTheme {

        return InstalledTheme(id, name, author, null)

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

    infix operator fun <T> OngoingStubbing<T>.minus(t: T): OngoingStubbing<T> = thenReturn(t)

}