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

import com.google.common.util.concurrent.ListenableFuture
import java.io.File

interface OverlayService {
    fun enableOverlay(id: String): ListenableFuture<*>

    fun disableOverlay(id: String): ListenableFuture<*>

    //Oreo has it in AIDL
    fun enableExclusive(id: String): ListenableFuture<*>

    fun getOverlayInfo(id: String): ListenableFuture<OverlayInfo?>

    fun getAllOverlaysForApk(appId: String): List<OverlayInfo>

    fun restartSystemUI(): ListenableFuture<*>

    fun installApk(apk: File): ListenableFuture<*>

    fun uninstallApk(appId: String): ListenableFuture<*>

    fun requiredPermissions(): List<String>

    fun additionalSteps(): String?

    fun getOverlaysPrioritiesForTarget(targetAppId: String): ListenableFuture<List<OverlayInfo>>

    /**
     * Returns Future<*>, because nothing is computed
     */
    fun updatePriorities(overlayIds: List<String>): ListenableFuture<*>
}