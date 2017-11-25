package com.jereksel.libresubstratum.domain

import com.crashlytics.android.Crashlytics
import com.jereksel.libresubstratum.extensions.getLogger
import javax.inject.Inject

/**
 * Anonymous metrics about app usage
 */
class CrashlitycsMetrics
@Inject constructor(): Metrics {

    val data = mutableMapOf<String, String>()

    val log = getLogger()

    override fun userEnteredTheme(themeId: String) {
        data["currentTheme"] = themeId
        Crashlytics.setString("currentTheme", themeId)
    }

    override fun userCompiledOverlay(themeId: String, targetApp: String) = Unit

    override fun userEnabledOverlay(overlayId: String) = Unit

    override fun userDisabledOverlay(overlayId: String) = Unit

    override fun logOverlayServiceType(overlayService: OverlayService) {
        data["overlayService"] = overlayService.javaClass.toString()
        Crashlytics.setString("overlayService", overlayService.javaClass.toString())
    }

    override fun getMetrics() = data
}