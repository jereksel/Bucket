package com.jereksel.libresubstratum.dagger.components

import com.jereksel.libresubstratum.activities.ErrorActivity
import com.jereksel.libresubstratum.activities.detailed.DetailedView
import com.jereksel.libresubstratum.activities.installed.InstalledView
import com.jereksel.libresubstratum.activities.main.MainView
import com.jereksel.libresubstratum.activities.priorities.PrioritiesView
import com.jereksel.libresubstratum.dagger.modules.AppModule
import com.jereksel.libresubstratum.dagger.modules.GroupMetricsModule
import com.jereksel.libresubstratum.dagger.modules.MetricsModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class, MetricsModule::class, GroupMetricsModule::class))
interface AppComponent {
    fun inject(view: MainView)
    fun inject(view: DetailedView)
    fun inject(view: InstalledView)
    fun inject(view: ErrorActivity)
    fun inject(view: PrioritiesView)
}
