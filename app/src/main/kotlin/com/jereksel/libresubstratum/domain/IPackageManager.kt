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

import android.graphics.drawable.Drawable
import com.jereksel.libresubstratum.data.InstalledOverlay
import com.jereksel.libresubstratum.data.InstalledTheme
import java.io.File

interface IPackageManager {
    fun getInstalledThemes(): List<InstalledTheme>
    fun getInstalledTheme(appId: String): InstalledTheme
    fun getInstalledOverlays(): List<InstalledOverlay>
    fun getAppVersion(appId: String): Pair<Int, String>
    fun getAppIcon(appId: String): Drawable?
    fun getAppName(appId: String): String
    fun isPackageInstalled(appId: String): Boolean
    fun getAppLocation(appId: String): File
    fun getCacheFolder(): File
}
