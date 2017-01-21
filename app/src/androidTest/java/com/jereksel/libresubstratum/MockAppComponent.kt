package com.jereksel.libresubstratum

import com.jereksel.libresubstratum.dagger.components.AppComponent
import com.jereksel.libresubstratum.domain.IPackageManager
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(MockAppModule::class))
interface MockAppComponent : AppComponent {
    fun getPackageManager() : IPackageManager
}