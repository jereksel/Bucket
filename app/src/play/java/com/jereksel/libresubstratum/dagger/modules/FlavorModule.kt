package com.jereksel.libresubstratum.dagger.modules

import com.jereksel.libresubstratum.domain.CrashlitycsMetrics
import com.jereksel.libresubstratum.domain.Metrics
import com.jereksel.libresubstratum.domain.PlayPrivacyPolicySettings
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
    internal abstract fun metricsFactory(factory: CrashlitycsMetrics): Metrics

    @Binds
    internal abstract fun privacyPolicyFactory(factory: PlayPrivacyPolicySettings): PrivacyPolicySettings
}