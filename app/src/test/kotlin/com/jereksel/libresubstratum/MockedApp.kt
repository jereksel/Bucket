package com.jereksel.libresubstratum

import android.app.Application
import com.jereksel.libresubstratum.App
import com.jereksel.libresubstratum.activities.detailed.DetailedContract
import com.jereksel.libresubstratum.activities.installed.InstalledContract
import com.jereksel.libresubstratum.activities.main.MainContract
import com.jereksel.libresubstratum.dagger.modules.AppModule
import com.jereksel.libresubstratum.domain.*
import com.jereksel.libresubstratum.domain.usecases.ICompileThemeUseCase
import com.jereksel.libresubstratum.domain.usecases.IGetThemeInfoUseCase
import io.kotlintest.mock.mock
import org.mockito.Mockito

class MockedApp : App() {

    val mockedPackageManager: IPackageManager = mock()
    val mockedMainPresenter: MainContract.Presenter = Mockito.mock(MainContract.Presenter::class.java)
    val mockedDetailedPresenter: DetailedContract.Presenter = Mockito.mock(DetailedContract.Presenter::class.java)
    val mockedInstalledPresenter: InstalledContract.Presenter = Mockito.mock(InstalledContract.Presenter::class.java)
    val mockedOverlayService: OverlayService = mock()
    val mockedActivityProxy: IActivityProxy = mock()
    val mockedThemeCompiler: ThemeCompiler = mock()

    override fun getAppModule(): AppModule {
        return object : AppModule(this) {
            override fun providesPackageManager(application: Application) = mockedPackageManager
            override fun providesMainPresenter(packageManager: IPackageManager, themeReader: IThemeReader, overlayService: OverlayService, metrics: Metrics) = mockedMainPresenter
            override fun providesDetailedPresenter(packageManager: IPackageManager, getThemeInfoUseCase: IGetThemeInfoUseCase, overlayService: OverlayService, activityProxy: IActivityProxy, themeExtractor: ThemeExtractor, compileThemeUseCase: ICompileThemeUseCase, clipboardManager: ClipboardManager, metrics: Metrics) = mockedDetailedPresenter
            override fun providesOverlayService() = mockedOverlayService
            override fun providesActivityProxy() = mockedActivityProxy
            override fun providesInstalledPresenter(packageManager: IPackageManager, overlayService: OverlayService, activityProxy: IActivityProxy, metrics: Metrics) = mockedInstalledPresenter
            override fun providesThemeCompiler(packageManager: IPackageManager) = mockedThemeCompiler
        }
    }
}
