package com.jereksel.libresubstratum.domain

import java.io.File

interface OverlayService {
    fun enableOverlay(id: String)
    fun disableOverlay(id: String)
    fun getOverlayInfo(id: String): OverlayInfo?
    fun getAllOverlaysForApk(appId: String): List<OverlayInfo>
    fun restartSystemUI()
    fun installApk(apk: File)
    fun uninstallApk(appId: String)

    fun requiredPermissions(): List<String>

    fun additionalSteps(): String?
}