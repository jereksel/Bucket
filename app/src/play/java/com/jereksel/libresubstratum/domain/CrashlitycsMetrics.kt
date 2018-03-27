package com.jereksel.libresubstratum.domain

import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
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
        Answers.getInstance().logCustom(CustomEvent("Entered theme")
                .putCustomAttribute("themeId", themeId))
    }

    override fun userCompiledOverlay(themeId: String, targetApp: String) {
        log.debug("User compiled overlay from theme {} for app {}", themeId, targetApp)
        Answers.getInstance().logCustom(CustomEvent("Compiled overlay")
                .putCustomAttribute("themeId", themeId)
                .putCustomAttribute("targetApp", targetApp))
    }

    override fun userEnabledOverlay(overlayId: String) {
        log.debug("User enabled overlay {}", overlayId)
        Answers.getInstance().logCustom(CustomEvent("Enabled overlay")
                .putCustomAttribute("overlayId", overlayId))
    }

    override fun userDisabledOverlay(overlayId: String) {
        log.debug("User disabled overlay {}", overlayId)
        Answers.getInstance().logCustom(CustomEvent("Disabled overlay")
                .putCustomAttribute("overlayId", overlayId))
    }

    override fun logOverlayServiceType(overlayService: OverlayService) {
        data["overlayService"] = overlayService.javaClass.toString()
        Crashlytics.setString("overlayService", overlayService.javaClass.toString())
        log.debug("User overlayService: {}", overlayService.javaClass.toString())
    }

    override fun getMetrics() = data
}