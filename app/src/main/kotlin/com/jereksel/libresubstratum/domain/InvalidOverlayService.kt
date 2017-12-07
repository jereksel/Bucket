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

import java.io.File

class InvalidOverlayService(val message: String): OverlayService {

    override fun enableOverlay(id: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun disableOverlay(id: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOverlayInfo(id: String): OverlayInfo {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAllOverlaysForApk(appId: String): List<OverlayInfo> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun restartSystemUI() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun installApk(apk: File) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun uninstallApk(appId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOverlaysPrioritiesForTarget(targetAppId: String): List<OverlayInfo> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updatePriorities(overlayIds: List<String>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun requiredPermissions() = listOf<String>()

    override fun additionalSteps() = message
}