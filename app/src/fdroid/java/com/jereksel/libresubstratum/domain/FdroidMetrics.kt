package com.jereksel.libresubstratum.domain

import com.jereksel.libresubstratum.extensions.getLogger
import javax.inject.Inject

class FdroidMetrics
@Inject constructor(): Metrics {

    val log = getLogger()

    override fun userEnteredTheme(themeId: String) {
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
        log.debug("User overlayService: {}", overlayService.javaClass.toString())
    }

}