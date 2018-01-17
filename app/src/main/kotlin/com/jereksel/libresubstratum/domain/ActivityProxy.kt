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

import android.app.Application
import android.content.Context
import android.content.Intent
import android.widget.Toast

class ActivityProxy(context: Application): IActivityProxy {

    val context: Context = context.applicationContext

    override fun openActivityInSplit(appId: String): Boolean {
        val intent = context.packageManager.getLaunchIntentForPackage(appId)

        if (intent != null) {
            intent
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT)
            context.startActivity(Intent.createChooser(intent, "Split"));
            return true
        } else {
            return false
        }
    }

    override fun showToast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }
}