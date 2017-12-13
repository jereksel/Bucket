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
import com.jereksel.libresubstratum.activities.detailed.DetailedPresenter
import com.jereksel.libresubstratum.activities.main.MainPresenter
import com.jereksel.libresubstratum.activities.main.MainContract
import com.jereksel.libresubstratum.activities.detailed.DetailedContract
import com.jereksel.libresubstratum.activities.installed.InstalledContract
import com.jereksel.libresubstratum.activities.installed.InstalledPresenter
import com.jereksel.libresubstratum.activities.priorities.PrioritiesContract
import com.jereksel.libresubstratum.activities.priorities.PrioritiesPresenter
import com.jereksel.libresubstratum.activities.prioritiesdetail.PrioritiesDetailContract
import com.jereksel.libresubstratum.activities.prioritiesdetail.PrioritiesDetailPresenter
import com.jereksel.libresubstratum.domain.*
import com.jereksel.libresubstratum.infrastructure.themeinfo.guavacache.ThemeInfoGuavaCache
import com.jereksel.libresubstratum.domain.usecases.CompileThemeUseCaseImpl
import com.jereksel.libresubstratum.domain.usecases.GetThemeInfoUseCaseImpl
import com.jereksel.libresubstratum.domain.usecases.CompileThemeUseCase
import com.jereksel.libresubstratum.domain.usecases.GetThemeInfoUseCase
import com.jereksel.libresubstratum.infrastructure.*
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
    open fun providesPackageManager(application: Application): PackageManager {
        return AndroidPackageManager(application)
    }

    @Provides
    @Singleton
    open fun providesThemeReader(
            packageManager: PackageManager,
            keyFinder: KeyFinder
    ): ThemeReader {
        return AndroidThemeReader(application, packageManager, keyFinder)
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
    open fun providesActivityProxy(): ActivityProxy = AndroidActivityProxy(application)

    @Provides
    @Singleton
    open fun providesThemeCompiler(
            packageManager: PackageManager,
            keyFinder: KeyFinder
    ): ThemeCompiler = AndroidThemeCompiler(application, packageManager, keyFinder)

    @Provides
    open fun providesMainPresenter(
            packageManager: PackageManager,
            themeReader: ThemeReader,
            @Named("logged") overlayService: OverlayService,
            @Named("group") metrics: Metrics,
            keyFinder: KeyFinder
    ): MainContract.Presenter {
        return MainPresenter(packageManager, themeReader, overlayService, metrics, keyFinder)
    }

    @Provides
    @Singleton
    open fun provideThemeExtractor(): ThemeExtractor = AndroidThemeExtractor()

    @Provides
    open fun providesDetailedPresenter(
            packageManager: PackageManager,
            getThemeInfoUseCase: GetThemeInfoUseCase,
            @Named("logged") overlayService: OverlayService,
            activityProxy: ActivityProxy,
            compileThemeUseCase: CompileThemeUseCase,
            clipboardManager: ClipboardManager,
            @Named("group") metrics: Metrics
    ): DetailedContract.Presenter {
        return DetailedPresenter(packageManager, getThemeInfoUseCase, overlayService, activityProxy, compileThemeUseCase, clipboardManager, metrics)
    }

    @Provides
    open fun providesInstalledPresenter(
            packageManager: PackageManager,
            @Named("logged") overlayService: OverlayService,
            activityProxy: ActivityProxy,
            @Named("group") metrics: Metrics
    ): InstalledContract.Presenter {
        return InstalledPresenter(packageManager, overlayService, activityProxy, metrics)
    }

    @Provides
    open fun providesPrioritiesPresenter(
            packageManager: PackageManager,
            @Named("logged") overlayService: OverlayService
    ): PrioritiesContract.Presenter {
        return PrioritiesPresenter(overlayService, packageManager)
    }

    @Provides
    open fun providesDetailedPrioritiesPresenter(
            packageManager: PackageManager,
            @Named("logged") overlayService: OverlayService,
            activityProxy: ActivityProxy
    ): PrioritiesDetailContract.Presenter {
        return PrioritiesDetailPresenter(overlayService, packageManager, activityProxy)
    }

    @Provides
    @Singleton
    open fun providesCompileThemeUseCase(
            packageManager: PackageManager,
            themeCompiler: ThemeCompiler
    ): CompileThemeUseCase {
        return CompileThemeUseCaseImpl(packageManager, themeCompiler)
    }

    @Provides
    @Singleton
    open fun providesClipBoardManager(): ClipboardManager = AndroidClipboardManager(application)

    @Provides
    @Singleton
    open fun providesKeyFinder(
            packageManager: PackageManager
    ): KeyFinder = AndroidKeyFinder(application, packageManager)

    @Provides
    @Singleton
    open fun providesThemePackDatabase(
    ): ThemePackDatabase = ThemeInfoGuavaCache()

    @Provides
    @Singleton
    open fun providesGetThemeInfoUseCase(
            packageManager: PackageManager,
            themePackDatabase: ThemePackDatabase,
            themeReader: ThemeReader
    ): GetThemeInfoUseCase = GetThemeInfoUseCaseImpl(packageManager, themePackDatabase, themeReader)

}
