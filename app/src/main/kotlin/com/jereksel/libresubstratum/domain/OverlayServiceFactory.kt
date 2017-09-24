package com.jereksel.libresubstratum.domain

import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import com.jereksel.libresubstratum.domain.overlayService.nougat.WDUCommitsOverlayService
import com.jereksel.libresubstratum.domain.overlayService.nougat.WODuCommitsOverlayService


object OverlayServiceFactory {

    fun getOverlayService(context: Context): OverlayService {

        if (android.os.Build.VERSION.SDK_INT != android.os.Build.VERSION_CODES.N &&
                android.os.Build.VERSION.SDK_INT != android.os.Build.VERSION_CODES.N_MR1) {
            return InvalidOverlayService("This app works only on Android Nougat")
        }

        val isSettingForceAuthorizeAvailable = try {
            Settings.Secure.getInt(context.contentResolver, "force_authorize_substratum_packages")
            true
        } catch (e: Exception) {
            false
        }

        if (!isSettingForceAuthorizeAvailable) {
            return InvalidOverlayService("Your ROM is too old to support this app")
        }


        if (isNewInterfacerPermissionAvailable(context)) {
            return WDUCommitsOverlayService(context)
        } else {
            return WODuCommitsOverlayService(context)
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