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

package com.jereksel.libresubstratum.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jereksel.libresubstratum.App
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.OverlayService
import com.jereksel.libresubstratum.extensions.getLogger
import javax.inject.Inject
import javax.inject.Named

class UninstallReceiver: BroadcastReceiver() {

    val log = getLogger()

    @Inject
    @field:Named("logged")
    lateinit var overlayService: OverlayService

    @Inject
    @field:Named
    lateinit var packageManager: IPackageManager

    override fun onReceive(context: Context, intent: Intent) {

        (context.applicationContext as App).getAppComponent(context).inject(this)

        val isReplaced = intent.getBooleanExtra("EXTRA_REPLACING", false)
        val app = intent.data.schemeSpecificPart

        if (isReplaced) {
            return
        }

        log.debug("Uninstalling overlays for $app")

        val installedOverlays = overlayService.getAllOverlaysForApk(app)

        log.debug("Installed overlays: $installedOverlays")

        overlayService.getAllOverlaysForApk(app).forEach {
            try {
                overlayService.uninstallApk(it.overlayId)
            } catch (e: Exception) {
                log.error("Cannot remove ${it.overlayId}", e)
            }
        }

        packageManager.getInstalledOverlays()
                .filter { it.sourceThemeId == app }
                .forEach {
                    try {
                        overlayService.uninstallApk(it.overlayId)
                    } catch (e: Exception) {
                        log.error("Cannot remove ${it.overlayId}", e)
                    }
                }

    }
}
