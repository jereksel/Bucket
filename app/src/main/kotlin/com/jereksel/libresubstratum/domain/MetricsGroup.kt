package com.jereksel.libresubstratum.domain

import dagger.Provides
import javax.inject.Inject
import javax.inject.Named

@Named("group")
class MetricsGroup
@Inject constructor(
        @Named("volatile") private val volatileMetrics: Metrics,
        @Named("persistent") private val persistentMetrics: Metrics
): Metrics {

    val metricsList = listOf(volatileMetrics, persistentMetrics)

    override fun userEnteredTheme(themeId: String) {
        metricsList.forEach { it.userEnteredTheme(themeId) }
    }

    override fun userCompiledOverlay(themeId: String, targetApp: String) {
        metricsList.forEach { it.userCompiledOverlay(themeId, targetApp) }
    }

    override fun userEnabledOverlay(overlayId: String) {
        metricsList.forEach { it.userEnabledOverlay(overlayId) }
    }

    override fun userDisabledOverlay(overlayId: String) {
        metricsList.forEach { it.userDisabledOverlay(overlayId) }
    }

    override fun logOverlayServiceType(overlayService: OverlayService) {
        metricsList.forEach { it.logOverlayServiceType(overlayService) }
    }

    override fun getMetrics(): Map<String, String> {
        return metricsList
                .map { it.getMetrics() }
                .map { it.entries }
                .flatten()
                .map { Pair(it.key, it.value) }
                .toMap()
    }


}

