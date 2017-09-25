package com.jereksel.libresubstratum.domain

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION.RELEASE
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.N
import android.os.Build.VERSION_CODES.N_MR1
import android.provider.Settings
import com.jereksel.libresubstratum.domain.overlayService.nougat.WDUCommitsOverlayService
import com.jereksel.libresubstratum.domain.overlayService.nougat.WODUCommitsOverlayService
import com.jereksel.libresubstratum.extensions.getLogger

object OverlayServiceFactory {

    val log = getLogger()

    fun getOverlayService(context: Context): OverlayService {

        val supportedAndroidVersions = listOf(N, N_MR1)

        if (!supportedAndroidVersions.contains(SDK_INT)) {
            log.error("Not supported android version: {} {}", SDK_INT, RELEASE)
            return InvalidOverlayService("This app works only on Android Nougat")
        }

        val isSettingForceAuthorizeAvailable = try {
            log.debug("force_authorize_substratum_packages supported")
            Settings.Secure.getInt(context.contentResolver, "force_authorize_substratum_packages")
            true
        } catch (e: Exception) {
            log.error("force_authorize_substratum_packages not supported")
            false
        }

        if (!isSettingForceAuthorizeAvailable) {
            return InvalidOverlayService("Your ROM is too old to support this app (3-rd party apps in Interfacer are not supported)")
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