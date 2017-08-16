package com.jereksel.libresubstratum.dagger.modules

import android.app.Application
import com.jereksel.libresubstratum.activities.detailed.DetailedPresenter
import com.jereksel.libresubstratum.activities.main.MainPresenter
import com.jereksel.libresubstratum.activities.main.MainContract
import com.jereksel.libresubstratum.activities.detailed.DetailedContract
import com.jereksel.libresubstratum.activities.installed.InstalledContract
import com.jereksel.libresubstratum.activities.installed.InstalledPresenter
import com.jereksel.libresubstratum.domain.*
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
        return InterfacerOverlayService(application)
    }

    @Provides
    @Singleton
    open fun providesActivityProxy(): IActivityProxy = ActivityProxy(application)

    @Provides
    @Singleton
    open fun providesMainPresenter(packageManager: IPackageManager): MainContract.Presenter {
        return MainPresenter(packageManager)
    }

    @Provides
    open fun providesDetailedPresenter(
            packageManager: IPackageManager,
            themeReader: IThemeReader,
            overlayService: OverlayService
    ): DetailedContract.Presenter {
        return DetailedPresenter(packageManager, themeReader, overlayService)
    }

    @Provides
    @Singleton
    open fun providesInstalledPresenter(
            packageManager: IPackageManager,
            overlayService: OverlayService,
            activityProxy: IActivityProxy
    ): InstalledContract.Presenter {
        return InstalledPresenter(packageManager, overlayService, activityProxy)
    }

}
