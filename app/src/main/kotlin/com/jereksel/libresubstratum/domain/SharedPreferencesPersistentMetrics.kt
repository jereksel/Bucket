package com.jereksel.libresubstratum.domain

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import dagger.Provides
import javax.inject.Inject
import javax.inject.Named

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