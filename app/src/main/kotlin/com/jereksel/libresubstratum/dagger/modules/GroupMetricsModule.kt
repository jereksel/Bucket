package com.jereksel.libresubstratum.dagger.modules

import com.jereksel.libresubstratum.domain.Metrics
import com.jereksel.libresubstratum.domain.MetricsGroup
import com.jereksel.libresubstratum.domain.SharedPreferencesPersistentMetrics
import dagger.Binds
import dagger.Module
import javax.inject.Named

@Module
abstract class GroupMetricsModule {
    @Binds
    @Named("group")
    abstract fun providesMetricsGroup(group: MetricsGroup): Metrics

    @Binds
    @Named("persistent")
    abstract fun providesVolatileMetrics(metrics: SharedPreferencesPersistentMetrics): Metrics

}