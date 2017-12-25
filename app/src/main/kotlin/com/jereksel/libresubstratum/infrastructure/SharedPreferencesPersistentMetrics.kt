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

package com.jereksel.libresubstratum.infrastructure

import android.app.Application
import android.content.Context.MODE_PRIVATE
import com.jereksel.libresubstratum.domain.Metrics
import com.jereksel.libresubstratum.domain.OverlayService
import javax.inject.Inject

class SharedPreferencesPersistentMetrics
@Inject constructor(
        context: Application
): Metrics {

    val SP_KEY = "metrics"

    val sharedPreferences = context.getSharedPreferences(SP_KEY, MODE_PRIVATE)

    val THEME_KEY = "THEME"
    val OVERLAY_SERVICE_TYPE_KEY = "OVERLAY_SERVICE"

    override fun userEnteredTheme(themeId: String) {
        sharedPreferences.edit().putString(THEME_KEY, themeId).commit()
    }

    override fun logOverlayServiceType(overlayService: OverlayService) {
        sharedPreferences.edit().putString(OVERLAY_SERVICE_TYPE_KEY, overlayService.javaClass.toString()).commit()
    }

    override fun getMetrics(): Map<String, String> {
        return mapOf(
                "currentTheme" to sharedPreferences.getString(THEME_KEY, null),
                "overlayService" to sharedPreferences.getString(OVERLAY_SERVICE_TYPE_KEY, null)
        )
                .filterValues { it != null }

    }

    override fun userCompiledOverlay(themeId: String, targetApp: String) {
    }

    override fun userEnabledOverlay(overlayId: String) {
    }

    override fun userDisabledOverlay(overlayId: String) {
    }

}