package com.jereksel.libresubstratum.dagger.modules

import com.jereksel.libresubstratum.domain.FdroidMetrics
import com.jereksel.libresubstratum.domain.Metrics
import dagger.Module
import dagger.Binds
import javax.inject.Named
import javax.inject.Singleton

@Module
abstract class MetricsModule {
    @Binds
    @Singleton
    @Named("volatile")
    internal abstract fun metricsFactory(factory: FdroidMetrics): Metrics
}