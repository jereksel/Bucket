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
        log.debug("User entered theme {}", themeId)
    }

    override fun userCompiledOverlay(themeId: String, targetApp: String) {
        log.debug("User compiled overlay from theme {} for app {}", themeId, targetApp)
    }

    override fun userEnabledOverlay(overlayId: String) {
        log.debug("User enabled overlay {}", overlayId)
    }

    override fun userDisabledOverlay(overlayId: String) {
        log.debug("User disabled overlay {}", overlayId)
    }

    override fun logOverlayServiceType(overlayService: OverlayService) {
        data["overlayService"] = overlayService.javaClass.toString()
        Crashlytics.setString("overlayService", overlayService.javaClass.toString())
        log.debug("User overlayService: {}", overlayService.javaClass.toString())
    }

    override fun getMetrics() = data
}