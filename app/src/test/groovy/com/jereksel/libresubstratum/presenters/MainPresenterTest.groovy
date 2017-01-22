package com.jereksel.libresubstratum.presenters

import android.os.Bundle
import com.jereksel.libresubstratum.activities.main.IMainView
import com.jereksel.libresubstratum.activities.main.MainPresenter
import com.jereksel.libresubstratum.data.Application
import com.jereksel.libresubstratum.domain.IPackageManager
import rx.android.plugins.RxAndroidPlugins
import rx.android.plugins.RxAndroidSchedulersHook
import rx.plugins.RxJavaHooks
import rx.schedulers.Schedulers
import spock.lang.Ignore
import spock.lang.Specification

class MainPresenterTest extends Specification {

    MainPresenter presenter;
    IPackageManager packageManager;
    IMainView view;

    def setup() {
        view = Mock()
        packageManager = Mock()
        presenter = new MainPresenter(packageManager);
        presenter.setView(view)
        RxJavaHooks.clear()
        RxJavaHooks.setOnIOScheduler({ Schedulers.immediate() })
        def hook = [
                "getMainThreadScheduler": Schedulers.immediate(),
        ] as RxAndroidSchedulersHook

        RxAndroidPlugins.instance.reset()
        RxAndroidPlugins.instance.registerSchedulersHook(hook)
    }

    def "filtering"() {
        setup:
        packageManager.getApplications() >> a
        when:
        presenter.getApplications()
        then:
        1 * view.addApplications({ it.size() == b })
        where:
        a                                                                | b
        []                                                               | 0
        [packageFactory("1", "App 1", "Author 1")]                       | 1
        [packageFactory("a", "a", "a"), packageFactory("a", null, null)] | 1
    }

    @Ignore
    def packageFactory(id, name, author) {
        Bundle bundle = Mock()
        bundle.get(MainPresenter.SUBSTRATUM_NAME) >> name
        bundle.getString(MainPresenter.SUBSTRATUM_NAME) >> name
        bundle.get(MainPresenter.SUBSTRATUM_LEGACY) >> new Object()
        bundle.get(MainPresenter.SUBSTRATUM_AUTHOR) >> author
        bundle.getString(MainPresenter.SUBSTRATUM_AUTHOR) >> author
        new Application(id, bundle)
    }
}