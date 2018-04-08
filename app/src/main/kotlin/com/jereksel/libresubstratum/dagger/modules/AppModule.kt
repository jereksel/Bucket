/*
 * Copyright (C) 2017 Andrzej Ressel (jereksel@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.jereksel.libresubstratum.dagger.modules

import android.app.Application
import com.jereksel.libresubstratum.activities.detailed.DetailedContract
import com.jereksel.libresubstratum.activities.detailed.DetailedPresenter
import com.jereksel.libresubstratum.activities.installed.InstalledContract
import com.jereksel.libresubstratum.activities.installed.InstalledPresenter
import com.jereksel.libresubstratum.activities.priorities.PrioritiesContract
import com.jereksel.libresubstratum.activities.priorities.PrioritiesPresenter
import com.jereksel.libresubstratum.activities.prioritiesdetail.PrioritiesDetailContract
import com.jereksel.libresubstratum.activities.prioritiesdetail.PrioritiesDetailPresenter
import com.jereksel.libresubstratum.domain.*
import com.jereksel.libresubstratum.domain.db.themeinfo.guavacache.ThemeInfoGuavaCache
import com.jereksel.libresubstratum.domain.usecases.*
import dagger.Module
import dagger.Provides
import javax.inject.Named
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
    open fun providesThemeReader(
            packageManager: IPackageManager,
            keyFinder: IKeyFinder
    ): IThemeReader {
        return ThemeReader(application, packageManager, keyFinder)
    }

    @Provides
    @Singleton
    @Named("default")
    open fun providesOverlayService(
            @Named("group") metrics: Metrics
    ): OverlayService {
        val service = OverlayServiceFactory.getOverlayService(application)
        metrics.logOverlayServiceType(service)
        return service
    }

    @Provides
    @Singleton
    @Named("logged")
    open fun providesLoggedOverlayService(
            @Named("default") overlayService: OverlayService,
            @Named("group") metrics: Metrics
    ): OverlayService {
        return LoggedOverlayService(overlayService, metrics)
    }

    @Provides
    @Singleton
    open fun providesActivityProxy(): IActivityProxy = ActivityProxy(application)

    @Provides
    @Singleton
    open fun providesThemeCompiler(
            packageManager: IPackageManager,
            keyFinder: IKeyFinder
    ): ThemeCompiler = AppThemeCompiler(application, packageManager, keyFinder)

    @Provides
    @Singleton
    open fun provideThemeExtractor(): ThemeExtractor = BaseThemeExtractor()

    @Provides
    open fun providesDetailedPresenter(
            packageManager: IPackageManager,
            getThemeInfoUseCase: IGetThemeInfoUseCase,
            @Named("logged") overlayService: OverlayService,
            activityProxy: IActivityProxy,
            compileThemeUseCase: ICompileThemeUseCase,
            clipboardManager: ClipboardManager,
            @Named("group") metrics: Metrics
    ): DetailedContract.Presenter {
        return DetailedPresenter(packageManager, getThemeInfoUseCase, overlayService, activityProxy, compileThemeUseCase, clipboardManager, metrics)
    }

    @Provides
    open fun providesInstalledPresenter(
            packageManager: IPackageManager,
            @Named("logged") overlayService: OverlayService,
            activityProxy: IActivityProxy,
            @Named("group") metrics: Metrics
    ): InstalledContract.Presenter {
        return InstalledPresenter(packageManager, overlayService, activityProxy, metrics)
    }

    @Provides
    open fun providesPrioritiesPresenter(
            packageManager: IPackageManager,
            @Named("logged") overlayService: OverlayService
    ): PrioritiesContract.Presenter {
        return PrioritiesPresenter(overlayService, packageManager)
    }

    @Provides
    open fun providesDetailedPrioritiesPresenter(
            packageManager: IPackageManager,
            @Named("logged") overlayService: OverlayService,
            activityProxy: IActivityProxy
    ): PrioritiesDetailContract.Presenter {
        return PrioritiesDetailPresenter(overlayService, packageManager, activityProxy)
    }

    @Provides
    @Singleton
    open fun providesCompileThemeUseCase(
            packageManager: IPackageManager,
            themeCompiler: ThemeCompiler
    ): ICompileThemeUseCase {
        return CompileThemeUseCase(packageManager, themeCompiler)
    }

    @Provides
    @Singleton
    open fun providesClipBoardManager(): ClipboardManager = AndroidClipboardManager(application)

    @Provides
    @Singleton
    open fun providesKeyFinder(
            packageManager: IPackageManager
    ): IKeyFinder = KeyFinder(application, packageManager)

    @Provides
    @Singleton
    open fun providesThemePackDatabase(
    ): ThemePackDatabase = ThemeInfoGuavaCache()

    @Provides
    @Singleton
    open fun providesGetThemeInfoUseCase(
            packageManager: IPackageManager,
            themePackDatabase: ThemePackDatabase,
            themeReader: IThemeReader
    ): IGetThemeInfoUseCase = GetThemeInfoUseCase(packageManager, themePackDatabase, themeReader)

    @Provides
    open fun providesCleanUnusedOverlays(
            packageManager: IPackageManager,
            @Named("logged") overlayService: OverlayService
    ): ICleanUnusedOverlays = CleanUnusedOverlays(packageManager, overlayService)

}
