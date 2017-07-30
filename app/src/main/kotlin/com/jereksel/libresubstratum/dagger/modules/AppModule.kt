package com.jereksel.libresubstratum.dagger.modules

import android.app.Application
import com.jereksel.libresubstratum.activities.detailed.DetailedPresenter
import com.jereksel.libresubstratum.activities.main.MainPresenter
import com.jereksel.libresubstratum.activities.main.MainContract
import com.jereksel.libresubstratum.activities.detailed.DetailedContract
import com.jereksel.libresubstratum.activities.installed.InstalledContract
import com.jereksel.libresubstratum.activities.installed.InstalledPresenter
import com.jereksel.libresubstratum.domain.AppPackageManager
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.IThemeReader
import com.jereksel.libresubstratum.domain.ThemeReader
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
    open fun providesMainPresenter(packageManager: IPackageManager): MainContract.Presenter {
        return MainPresenter(packageManager)
    }

    @Provides
    @Singleton
    open fun providesDetailedPresenter(packageManager: IPackageManager, themeReader: IThemeReader) : DetailedContract.Presenter {
        return DetailedPresenter(packageManager, themeReader)
    }

    @Provides
    @Singleton
    open fun providesInstalledPresenter(packageManager: IPackageManager) : InstalledContract.Presenter {
        return InstalledPresenter(packageManager)
    }

}
