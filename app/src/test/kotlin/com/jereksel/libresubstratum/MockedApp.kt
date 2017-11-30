package com.jereksel.libresubstratum

import com.jereksel.libresubstratum.activities.detailed.DetailedContract
import com.jereksel.libresubstratum.activities.installed.InstalledContract
import com.jereksel.libresubstratum.activities.main.MainContract
import com.jereksel.libresubstratum.activities.priorities.PrioritiesContract
import com.jereksel.libresubstratum.activities.prioritiesdetail.PrioritiesDetailContract
import com.jereksel.libresubstratum.dagger.modules.AppModule
import com.jereksel.libresubstratum.domain.*
import com.jereksel.libresubstratum.domain.usecases.ICompileThemeUseCase
import com.jereksel.libresubstratum.domain.usecases.IGetThemeInfoUseCase
import io.kotlintest.mock.mock
import org.mockito.Mockito

class MockedApp : App() {

    val mockedMainPresenter: MainContract.Presenter = Mockito.mock(MainContract.Presenter::class.java)
    val mockedDetailedPresenter: DetailedContract.Presenter = Mockito.mock(DetailedContract.Presenter::class.java)
    val mockedInstalledPresenter: InstalledContract.Presenter = Mockito.mock(InstalledContract.Presenter::class.java)
    val mockedPrioritiesPresenter: PrioritiesContract.Presenter = Mockito.mock(PrioritiesContract.Presenter::class.java)
    val mockedPrioritiesDetailPresenter: PrioritiesDetailContract.Presenter = Mockito.mock(PrioritiesDetailContract.Presenter::class.java)

    override fun getAppModule(): AppModule {
        return object : AppModule(this) {
            override fun providesMainPresenter(packageManager: IPackageManager, themeReader: IThemeReader, overlayService: OverlayService, metrics: Metrics, keyFinder: IKeyFinder) = mockedMainPresenter
            override fun providesDetailedPresenter(packageManager: IPackageManager, getThemeInfoUseCase: IGetThemeInfoUseCase, overlayService: OverlayService, activityProxy: IActivityProxy, compileThemeUseCase: ICompileThemeUseCase, clipboardManager: ClipboardManager, metrics: Metrics): DetailedContract.Presenter = mockedDetailedPresenter
            override fun providesInstalledPresenter(packageManager: IPackageManager, overlayService: OverlayService, activityProxy: IActivityProxy, metrics: Metrics): InstalledContract.Presenter = mockedInstalledPresenter
            override fun providesPrioritiesPresenter(packageManager: IPackageManager, overlayService: OverlayService) = mockedPrioritiesPresenter
            override fun providesDetailedPrioritiesPresenter(packageManager: IPackageManager, overlayService: OverlayService, activityProxy: IActivityProxy) = mockedPrioritiesDetailPresenter

            override fun providesThemeCompiler(packageManager: IPackageManager, keyFinder: IKeyFinder): ThemeCompiler = mock()
        }
    }
}
