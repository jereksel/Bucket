/*
 * Copyright (C) 2017 Andrzej Ressel (jereksel@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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

