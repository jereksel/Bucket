package com.jereksel.libresubstratum.domain

import com.google.common.util.concurrent.ListenableFuture
import java.io.File

class InvalidOverlayService(val message: String): OverlayService {

    override fun enableOverlay(id: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun disableOverlay(id: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOverlayInfo(id: String): OverlayInfo {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAllOverlaysForApk(appId: String): List<OverlayInfo> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun restartSystemUI() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun installApk(apk: File) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun uninstallApk(appId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOverlaysPrioritiesForTarget(targetAppId: String): ListenableFuture<List<OverlayInfo>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updatePriorities(overlayIds: List<String>): ListenableFuture<*> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun requiredPermissions() = listOf<String>()

    override fun additionalSteps() = message
}