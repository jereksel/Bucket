/*
 * Copyright (C) 2018 Andrzej Ressel (jereksel@gmail.com)
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

package com.jereksel.libresubstratum.domain.usecases

import android.os.Looper
import com.google.common.util.concurrent.MoreExecutors
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.InvalidOverlayService
import com.jereksel.libresubstratum.domain.LoggedOverlayService
import com.jereksel.libresubstratum.domain.OverlayService
import com.jereksel.libresubstratum.extensions.getLogger
import java.util.concurrent.Executors

class CleanUnusedOverlays(
        val packageManager: IPackageManager,
        val overlayManager: OverlayService
): ICleanUnusedOverlays {

    val log = getLogger()

    val executor = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor())

    override fun clean() = executor.submit {

        if (overlayManager is InvalidOverlayService || (overlayManager as? LoggedOverlayService)?.overlayService is InvalidOverlayService) {
            return@submit
        }

        log.debug("Cleaning overlays")

        val overlays = packageManager.getInstalledOverlays()

        for (overlay in overlays) {

            val overlayId = overlay.overlayId
            val themeAppId = overlay.sourceThemeId
            val targetAppId = overlay.targetId

            if (!packageManager.isPackageInstalled(themeAppId) || !packageManager.isPackageInstalled(targetAppId)) {
                try {
                    overlayManager.uninstallApk(overlayId).get()
                } catch (e: Exception) {
                    log.error("Cannot remove $overlayId", e)
                }
            }

        }


    }
}