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

package com.jereksel.libresubstratum.domain

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.substratum.ISubstratumService
import android.os.Build.VERSION.RELEASE
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.*
import android.os.IBinder
import com.jereksel.libresubstratum.domain.overlayService.nougat.WDUCommitsOverlayService
import com.jereksel.libresubstratum.domain.overlayService.nougat.WODUCommitsOverlayService
import com.jereksel.libresubstratum.domain.overlayService.oreo.OreoOverlayService
import com.jereksel.libresubstratum.domain.overlayService.oreo.OreoSubstratumService
import com.jereksel.libresubstratum.extensions.getLogger
import java.io.File

object OverlayServiceFactory {

    val log = getLogger()

    fun getOverlayService(context: Context): OverlayService {


        val o = listOf(O, O_MR1)

        if (o.contains(SDK_INT)) {
            if (isOreoSubstratumServiceAvailable(context)) {
                return OreoSubstratumService(context)
            } else if (suExists()) {
                return OreoOverlayService(context)
            } else  {
                return InvalidOverlayService("Root is required for Oreo support")
            }
        }

        val supportedAndroidVersions = listOf(N, N_MR1)

        if (!supportedAndroidVersions.contains(SDK_INT)) {
            log.error("Not supported android version: {} {}", SDK_INT, RELEASE)
            return InvalidOverlayService("This app works only on Android Nougat or rooted Oreo")
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

    private fun isOreoSubstratumServiceAvailable(context: Context): Boolean {

        try {
            @SuppressLint("PrivateApi")
            val serviceServiceClass = Class.forName("android.os.ServiceManager")
            val binder = serviceServiceClass.getMethod("getService", String::class.java)
                    .invoke(null, "substratum")
            return ISubstratumService.Stub.asInterface(binder as IBinder?) != null
        } catch (e: Exception) {
            log.debug("Oreo Substratum Service is not available", e)
            return false
        }


    }

    private fun suExists(): Boolean {

        val PATH = System.getenv("PATH").split(":")

        return PATH
                .asSequence()
                .map { "${it}${File.separator}su" }
                .any { File(it).exists() }


    }

}