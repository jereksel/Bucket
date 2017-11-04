package com.jereksel.libresubstratum.domain

import java.io.File

class InvalidOverlayService(val message: String): OverlayService {
    override fun enableOverlays(ids: List<String>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun disableOverlays(ids: List<String>) {
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

    override fun installApk(apk: List<File>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun uninstallApk(appIds: List<String>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun requiredPermissions() = listOf<String>()

    override fun additionalSteps() = message
}