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
import com.jereksel.libresubstratum.domain.usecases.ICleanUnusedOverlays
import com.jereksel.libresubstratum.extensions.getLogger
import javax.inject.Inject

class UninstallReceiver: BroadcastReceiver() {

    val log = getLogger()

    @Inject
    lateinit var cleanUnusedOverlays: ICleanUnusedOverlays

    override fun onReceive(context: Context, intent: Intent) {

        log.debug("UninstallReceiver invoked")

        (context.applicationContext as App).getAppComponent(context).inject(this)

        cleanUnusedOverlays.clean()

    }
}
