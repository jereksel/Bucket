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

package com.jereksel.libresubstratum.domain.overlayService.nougat

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings

open class WODUCommitsOverlayService(context: Context): InterfacerOverlayService(context) {
    override fun allPermissions() = listOf(WRITE_EXTERNAL_STORAGE)

    override fun additionalSteps(): String? {

        try {
            context.packageManager.getPackageInfo("projekt.substratum", 0)
        } catch (e: PackageManager.NameNotFoundException) {
            return "Please install Substratum app (it's required for Interfacer to function properly)"
        }

        val areAllPackagesAllowed = Settings.Secure.getInt(context.contentResolver, "force_authorize_substratum_packages", 0) == 1

        return if (!areAllPackagesAllowed) {
            """Please turn on "Force authorize every theme app" in developer settings"""
        } else {
            null
        }
    }
}