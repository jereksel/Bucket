package com.jereksel.libresubstratum.dagger.components

import com.jereksel.libresubstratum.activities.detailed.DetailedView
import com.jereksel.libresubstratum.activities.installed.InstalledView
import com.jereksel.libresubstratum.activities.main.MainView
import com.jereksel.libresubstratum.dagger.modules.AppModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {
    fun inject(view: MainView)
    fun inject(view: DetailedView)
    fun inject(view: InstalledView)
}
