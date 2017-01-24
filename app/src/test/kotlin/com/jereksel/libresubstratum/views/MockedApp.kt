package com.jereksel.libresubstratum.views

import android.app.Application
import com.jereksel.libresubstratum.App
import com.jereksel.libresubstratum.dagger.modules.AppModule
import com.jereksel.libresubstratum.domain.IPackageManager
import io.kotlintest.mock.mock

class MockedApp : App() {

    val mockedPackageManager: IPackageManager = mock()

    override fun getAppModule(): AppModule {
        return object : AppModule(this) {
            override fun providesPackageManager(application: Application) = mockedPackageManager
        }
    }
}
