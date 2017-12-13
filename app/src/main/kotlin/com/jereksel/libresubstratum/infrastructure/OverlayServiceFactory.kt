/*
 * Copyright (C) 2017 Andrzej Ressel (jereksel@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.jereksel.libresubstratum.infrastructure

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build.VERSION.RELEASE
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.N
import android.os.Build.VERSION_CODES.N_MR1
import com.jereksel.libresubstratum.domain.OverlayService
import com.jereksel.libresubstratum.infrastructure.nougat.WDUCommitsOverlayService
import com.jereksel.libresubstratum.infrastructure.nougat.WODUCommitsOverlayService
import com.jereksel.libresubstratum.extensions.getLogger

object OverlayServiceFactory {

    val log = getLogger()

    fun getOverlayService(context: Context): OverlayService {

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