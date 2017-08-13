package com.jereksel.libresubstratum

import android.app.Application
import com.jereksel.libresubstratum.activities.installed.InstalledContract
import com.jereksel.libresubstratum.activities.main.MainContract
import com.jereksel.libresubstratum.dagger.modules.AppModule
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.OverlayService
import io.kotlintest.mock.mock
import org.mockito.Mockito

class MockedApp : App() {

    val mockedPackageManager: IPackageManager = mock()
    val mockedMainPresenter: MainContract.Presenter = Mockito.mock(MainContract.Presenter::class.java)
    val mockedInstalledPresenter: InstalledContract.Presenter = Mockito.mock(InstalledContract.Presenter::class.java)
    val mockedOverlayService: OverlayService = mock()

    override fun getAppModule(): AppModule {
        return object : AppModule(this) {
            override fun providesPackageManager(application: Application) = mockedPackageManager
            override fun providesMainPresenter(packageManager: IPackageManager) = mockedMainPresenter
            override fun providesOverlayService() = mockedOverlayService
            override fun providesInstalledPresenter(packageManager: IPackageManager, overlayService: OverlayService) = mockedInstalledPresenter
        }
    }
}
