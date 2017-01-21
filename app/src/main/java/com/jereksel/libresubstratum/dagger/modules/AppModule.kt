package com.jereksel.libresubstratum.dagger.modules

import android.app.Application
import com.jereksel.libresubstratum.domain.AppPackageManager
import com.jereksel.libresubstratum.domain.IPackageManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val application: Application) {

    @Provides
    @Singleton
    fun providesApplication() = application

    @Provides
    @Singleton
    fun providesPackageManager(application: Application): IPackageManager {
        return AppPackageManager(application)
    }

}
