package com.jereksel.libresubstratum.domain

import java.io.File

interface OverlayService {
    fun enableOverlay(id: String) = enableOverlays(listOf(id))
    fun enableOverlays(ids: List<String>)
    fun disableOverlay(id: String) = disableOverlays(listOf(id))
    fun disableOverlays(ids: List<String>)
    fun getOverlayInfo(id: String): OverlayInfo
    fun getAllOverlaysForApk(appId: String): List<OverlayInfo>
    fun restartSystemUI()
    fun installApk(apk: File)
    fun uninstallApk(appId: String) = uninstallApk(listOf(appId))
    fun uninstallApk(appIds: List<String>)
    fun toggleOverlay(id: String, enabled: Boolean) {
        if (enabled) {
            enableOverlay(id)
        } else {
            disableOverlay(id)
        }
    }
}