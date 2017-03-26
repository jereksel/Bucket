package com.jereksel.libresubstratum

import android.app.Application
import com.jereksel.libresubstratum.App
import com.jereksel.libresubstratum.activities.main.MainContract
import com.jereksel.libresubstratum.dagger.modules.AppModule
import com.jereksel.libresubstratum.domain.IPackageManager
import io.kotlintest.mock.mock
import org.mockito.Mockito

class MockedApp : App() {

    val mockedPackageManager: IPackageManager = mock()
    val mockedMainPresenter: MainContract.Presenter = Mockito.mock(MainContract.Presenter::class.java)

    override fun getAppModule(): AppModule {
        return object : AppModule(this) {
            override fun providesPackageManager(application: Application) = mockedPackageManager
            override fun providesMainPresenter(packageManager: IPackageManager) = mockedMainPresenter
        }
    }
}
