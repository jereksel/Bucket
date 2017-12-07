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
import java.util.concurrent.Future

class LoggedOverlayService(
        val overlayService: OverlayService,
        val metrics: Metrics
): OverlayService {
    override fun enableOverlay(id: String) {
        metrics.userEnabledOverlay(id)
        overlayService.enableOverlay(id)
    }

    override fun disableOverlay(id: String) {
        metrics.userDisabledOverlay(id)
        overlayService.disableOverlay(id)
    }

    override fun getOverlayInfo(id: String) = overlayService.getOverlayInfo(id)

    override fun getAllOverlaysForApk(appId: String) = overlayService.getAllOverlaysForApk(appId)

    override fun restartSystemUI() {
        overlayService.restartSystemUI()
    }

    override fun installApk(apk: File) {
        overlayService.installApk(apk)
    }

    override fun uninstallApk(appId: String) {
        overlayService.uninstallApk(appId)
    }

    override fun getOverlaysPrioritiesForTarget(targetAppId: String) =
            overlayService.getOverlaysPrioritiesForTarget(targetAppId)

    override fun updatePriorities(overlayIds: List<String>) =
            overlayService.updatePriorities(overlayIds)

    override fun requiredPermissions() = overlayService.requiredPermissions()

    override fun additionalSteps() = overlayService.additionalSteps()

}