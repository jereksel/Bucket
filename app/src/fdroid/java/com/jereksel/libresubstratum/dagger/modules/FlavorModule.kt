package com.jereksel.libresubstratum.dagger.modules

import com.jereksel.libresubstratum.domain.FdroidMetrics
import com.jereksel.libresubstratum.domain.FdroidPrivacyPolicySettings
import com.jereksel.libresubstratum.domain.Metrics
import com.jereksel.libresubstratum.domain.PrivacyPolicySettings
import dagger.Module
import dagger.Binds
import javax.inject.Named
import javax.inject.Singleton

@Module
abstract class FlavorModule {
    @Binds
    @Singleton
    @Named("volatile")
    internal abstract fun metricsFactory(factory: FdroidMetrics): Metrics

    @Binds
    internal abstract fun privacyPolicyFactory(factory: FdroidPrivacyPolicySettings): PrivacyPolicySettings

}