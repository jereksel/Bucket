package com.jereksel.libresubstratum.presenters

import android.os.Bundle
import com.jereksel.libresubstratum.activities.main.IMainView
import com.jereksel.libresubstratum.activities.main.MainPresenter
import com.jereksel.libresubstratum.data.Application
import com.jereksel.libresubstratum.domain.IPackageManager
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.kotlintest.mock.mock
import io.kotlintest.specs.FunSpec
import org.mockito.stubbing.OngoingStubbing
import rx.android.plugins.RxAndroidPlugins
import rx.android.plugins.RxAndroidSchedulersHook
import rx.plugins.RxJavaHooks
import rx.schedulers.Schedulers

class MainPresenterTest : FunSpec() {

    lateinit var presenter : MainPresenter
    lateinit var packageManager : IPackageManager
    lateinit var view : IMainView

    override fun beforeEach() {
        view = mock()
        packageManager = mock()
        presenter = MainPresenter(packageManager);
        presenter.setView(view)
        RxJavaHooks.clear()
        RxJavaHooks.setOnIOScheduler({ Schedulers.immediate() })

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
                    row(listOf(packageFactory("1", "App 1", "Author 1"), packageFactory("a", null, null)), 1)
            )
            forAll(myTable) { packages, num ->
                beforeEach()
                whenever(packageManager.getApplications()).thenReturn(packages)
                presenter.getApplications()
                verify(view).addApplications(argThat { size == num })
            }
        }
    }

    fun packageFactory(id: String, name: String?, author: String?): Application {

        val bundle = mock<Bundle> {
            on { get(MainPresenter.SUBSTRATUM_NAME) } - name
            on { getString(MainPresenter.SUBSTRATUM_NAME) } - name
            on { get(MainPresenter.SUBSTRATUM_AUTHOR) } - author
            on { getString(MainPresenter.SUBSTRATUM_AUTHOR) } - author
            on { get(MainPresenter.SUBSTRATUM_LEGACY) } - Any()
        }

        return Application(id, bundle)
    }

    infix operator fun <T> OngoingStubbing<T>.minus(t: T): OngoingStubbing<T> = thenReturn(t)

}