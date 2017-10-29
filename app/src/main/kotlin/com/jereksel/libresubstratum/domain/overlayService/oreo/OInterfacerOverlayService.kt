package com.jereksel.libresubstratum.domain.overlayService.oreo

import android.Manifest
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.om.InterfacerManager
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import com.jereksel.libresubstratum.domain.OverlayInfo
import com.jereksel.libresubstratum.domain.OverlayService
import java.io.File

class OInterfacerOverlayService(
        val context: Context
) : OverlayService {

    val interfacer = context.getSystemService("interfacer") as InterfacerManager

    override fun enableOverlays(ids: List<String>) {
        interfacer.enableOverlay(ids)
    }

    override fun disableOverlays(ids: List<String>) {
         interfacer.disableOverlay(ids)
    }

    override fun getOverlayInfo(id: String): OverlayInfo? {
        val info = interfacer.getOverlayInfo(id)
        return if (info != null) {
            OverlayInfo(id, info.isEnabled)
        } else {
            null
        }
    }

    override fun getAllOverlaysForApk(appId: String): List<OverlayInfo> {
        val map = interfacer.getOverlayInfosForTarget(appId)
        return map.map { OverlayInfo(it.packageName, it.isEnabled) }
    }

    override fun restartSystemUI() {
    }

    override fun installApk(apk: File) {
        interfacer.installPackage(listOf(apk.absolutePath))
    }

    override fun uninstallApk(appIds: List<String>) {
        interfacer.uninstallPackage(appIds)
    }

    override fun requiredPermissions(): List<String>  {
        return listOf(WRITE_EXTERNAL_STORAGE,
                "interfacer.permission.MODIFY_OVERLAY",
                "interfacer.permission.MODIFY_APPS")
                .filter { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_DENIED }
    }

    override fun additionalSteps() = null

}