package com.jereksel.libresubstratum.domain

import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import com.jereksel.libresubstratum.extensions.getLogger
import javax.inject.Inject
import javax.inject.Named

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
        Answers.getInstance().logCustom(CustomEvent("Entered theme")
                .putCustomAttribute("themeId", themeId))
    }

    override fun userCompiledOverlay(themeId: String, targetApp: String) {
        Answers.getInstance().logCustom(CustomEvent("Compiled overlay")
                .putCustomAttribute("themeId", themeId)
                .putCustomAttribute("targetApp", targetApp))
    }

    override fun userEnabledOverlay(overlayId: String) {
        Answers.getInstance().logCustom(CustomEvent("Enabled overlay")
                .putCustomAttribute("overlayId", overlayId))
    }

    override fun userDisabledOverlay(overlayId: String) {
        Answers.getInstance().logCustom(CustomEvent("Disabled overlay")
                .putCustomAttribute("overlayId", overlayId))
    }

    override fun logOverlayServiceType(overlayService: OverlayService) {
        data["overlayService"] = overlayService.javaClass.toString()
        Crashlytics.setString("overlayService", overlayService.javaClass.toString())
    }

    override fun getMetrics() = data
}