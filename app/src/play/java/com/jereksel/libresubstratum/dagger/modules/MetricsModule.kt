package com.jereksel.libresubstratum.dagger.modules

import com.jereksel.libresubstratum.domain.CrashlitycsMetrics
import com.jereksel.libresubstratum.domain.Metrics
import dagger.Module
import dagger.Binds

@Module
abstract class MetricsModule {
    @Binds
    internal abstract fun metricsFactory(factory: CrashlitycsMetrics): Metrics
}