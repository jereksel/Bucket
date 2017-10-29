package com.jereksel.libresubstratum.domain

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION.RELEASE
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.*
import android.provider.Settings
import com.jereksel.libresubstratum.domain.overlayService.nougat.WDUCommitsOverlayService
import com.jereksel.libresubstratum.domain.overlayService.nougat.WODUCommitsOverlayService
import com.jereksel.libresubstratum.domain.overlayService.oreo.OInterfacerOverlayService
import com.jereksel.libresubstratum.extensions.getLogger

object OverlayServiceFactory {

    val log = getLogger()

    fun getOverlayService(context: Context): OverlayService {

        if (SDK_INT == O) {
            return OInterfacerOverlayService(context)
        }

        val supportedAndroidVersions = listOf(N, N_MR1)

        if (!supportedAndroidVersions.contains(SDK_INT)) {
            log.error("Not supported android version: {} {}", SDK_INT, RELEASE)
            return InvalidOverlayService("This app works only on Android Nougat")
        }

        try {
            context.packageManager.getApplicationInfo("projekt.interfacer", 0)
        } catch (e: PackageManager.NameNotFoundException) {
            log.error("Interfacer is not installed")
            return InvalidOverlayService("Interfacer is not installed. Are you using Substratum compatible ROM?")
        }

        if (isNewInterfacerPermissionAvailable(context)) {
            log.debug("DU commits available")
            return WDUCommitsOverlayService(context)
        } else {
            log.debug("DU commits not available")
            return WODUCommitsOverlayService(context)
        }

    }

    private fun isNewInterfacerPermissionAvailable(context: Context): Boolean {

        try {

            val info = context.packageManager.getPackageInfo("projekt.interfacer", PackageManager.GET_PERMISSIONS)

            return info.permissions.firstOrNull { it.name == "projekt.interfacer.permission.ACCESS_SERVICE_INNER" } != null

        } catch (e: Exception) {
            return false
        }

    }


}