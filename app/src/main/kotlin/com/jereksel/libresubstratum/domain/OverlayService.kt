package com.jereksel.libresubstratum.domain

import com.google.common.util.concurrent.ListenableFuture
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

    fun getOverlaysPrioritiesForTarget(targetAppId: String): ListenableFuture<List<OverlayInfo>>

    /**
     * Returns Future<*>, because nothing is computed
     */
    fun updatePriorities(overlayIds: List<String>): ListenableFuture<*>
}