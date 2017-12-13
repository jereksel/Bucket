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

package com.jereksel.libresubstratum.infrastructure

import android.content.Context
import com.jereksel.libresubstratum.data.KeyPair
import com.jereksel.libresubstratum.domain.KeyFinder
import com.jereksel.libresubstratum.domain.PackageManager
import com.jereksel.libresubstratum.domain.ThemeReader
import com.jereksel.libresubstratumlib.ThemePack

class AndroidThemeReader(
        val context: Context,
        val packageManager: PackageManager,
        val keyFinder: KeyFinder
): ThemeReader {
    private val themeReaderImpl = com.jereksel.libresubstratumlib.ThemeReader()
    private val themeReaderAssetManager = com.jereksel.themereaderassetmanager.Reader

    override fun readThemePack(appId: String): ThemePack {
        val keyPair = keyFinder.getKey(appId) ?: KeyPair.EMPTYKEY
        val assets = context.packageManager.getResourcesForApplication(appId).assets
        return themeReaderAssetManager.read(assets, keyPair.getTransformer())
    }

    override fun isThemeEncrypted(appId: String): Boolean {
        val location = packageManager.getAppLocation(appId)
        return themeReaderImpl.checkIfEncrypted(location)
    }

}