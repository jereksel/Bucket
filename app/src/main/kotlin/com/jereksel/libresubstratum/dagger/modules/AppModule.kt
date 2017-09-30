package com.jereksel.libresubstratum.dagger.modules

import android.app.Application
import com.jereksel.libresubstratum.activities.detailed.DetailedPresenter
import com.jereksel.libresubstratum.activities.main.MainPresenter
import com.jereksel.libresubstratum.activities.main.MainContract
import com.jereksel.libresubstratum.activities.detailed.DetailedContract
import com.jereksel.libresubstratum.activities.installed.InstalledContract
import com.jereksel.libresubstratum.activities.installed.InstalledPresenter
import com.jereksel.libresubstratum.domain.*
import com.jereksel.libresubstratum.domain.usecases.CompileThemeUseCase
import com.jereksel.libresubstratum.domain.usecases.ICompileThemeUseCase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
open class AppModule(private val application: Application) {

    @Provides
    @Singleton
    fun providesApplication() = application

    @Provides
    @Singleton
    open fun providesPackageManager(application: Application): IPackageManager {
        return AppPackageManager(application)
    }

    @Provides
    @Singleton
    open fun providesThemeReader(): IThemeReader {
        return ThemeReader()
    }

    @Provides
    @Singleton
    open fun providesOverlayService(): OverlayService {
        return OverlayServiceFactory.getOverlayService(application)
    }

    @Provides
    @Singleton
    open fun providesActivityProxy(): IActivityProxy = ActivityProxy(application)

    @Provides
    @Singleton
    open fun providesThemeCompiler(
            packageManager: IPackageManager
    ): ThemeCompiler = AppThemeCompiler(application, packageManager)

    @Provides
//    @Singleton
    open fun providesMainPresenter(packageManager: IPackageManager, themeReader: IThemeReader, overlayService: OverlayService): MainContract.Presenter {
        return MainPresenter(packageManager, themeReader, overlayService)
    }

    @Provides
    open fun provideThemeExtractor(): ThemeExtractor = BaseThemeExtractor()

    @Provides
    open fun providesDetailedPresenter(
            packageManager: IPackageManager,
            themeReader: IThemeReader,
            overlayService: OverlayService,
            activityProxy: IActivityProxy,
            themeCompiler: ThemeCompiler,
            themeExtractor: ThemeExtractor,
            compileThemeUseCase: ICompileThemeUseCase
    ): DetailedContract.Presenter {
        return DetailedPresenter(packageManager, themeReader, overlayService, activityProxy, themeCompiler, themeExtractor, compileThemeUseCase)
    }

    @Provides
//    @Singleton
    open fun providesInstalledPresenter(
            packageManager: IPackageManager,
            overlayService: OverlayService,
            activityProxy: IActivityProxy
    ): InstalledContract.Presenter {
        return InstalledPresenter(packageManager, overlayService, activityProxy)
    }

    @Provides
    open fun providesCompileThemeUseCase(
            packageManager: IPackageManager,
            themeReader: IThemeReader,
            overlayService: OverlayService,
            activityProxy: IActivityProxy,
            themeCompiler: ThemeCompiler,
            themeExtractor: ThemeExtractor
    ): ICompileThemeUseCase {
        return CompileThemeUseCase(packageManager, themeReader, overlayService, activityProxy, themeCompiler, themeExtractor)
    }

}
