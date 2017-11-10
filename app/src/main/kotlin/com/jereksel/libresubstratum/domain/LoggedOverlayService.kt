package com.jereksel.libresubstratum.domain

import java.io.File

class LoggedOverlayService(
        val overlayService: OverlayService,
        val metrics: Metrics
): OverlayService {
    override fun enableOverlay(id: String) {
        metrics.userEnabledOverlay(id)
        overlayService.enableOverlay(id)
    }

    override fun disableOverlay(id: String) {
        metrics.userDisabledOverlay(id)
        overlayService.disableOverlay(id)
    }

    override fun getOverlayInfo(id: String) = overlayService.getOverlayInfo(id)

    override fun getAllOverlaysForApk(appId: String) = overlayService.getAllOverlaysForApk(appId)

    override fun restartSystemUI() {
        overlayService.restartSystemUI()
    }

    override fun installApk(apk: File) {
        overlayService.installApk(apk)
    }

    override fun uninstallApk(appId: String) {
        overlayService.uninstallApk(appId)
    }

    override fun requiredPermissions() = overlayService.requiredPermissions()

    override fun additionalSteps() = overlayService.additionalSteps()

}