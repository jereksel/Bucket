package com.jereksel.libresubstratum.domain

import com.jereksel.libresubstratum.extensions.getLogger
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

class FdroidMetrics
@Inject constructor(): Metrics {

    val data = mutableMapOf<String, String>()

    val log = getLogger()

    override fun userEnteredTheme(themeId: String) {
        data["currentTheme"] = themeId
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
        data["overlayManager"] = overlayService.javaClass.toString()
        log.debug("User overlayManager: {}", overlayService.javaClass.toString())
    }

    override fun getMetrics() = data

}