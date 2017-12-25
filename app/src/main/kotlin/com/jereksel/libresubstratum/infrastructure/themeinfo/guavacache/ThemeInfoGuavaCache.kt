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

package com.jereksel.libresubstratum.infrastructure.themeinfo.guavacache

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.jereksel.libresubstratum.domain.ThemePackDatabase
import com.jereksel.libresubstratumlib.ThemePack
import java.util.concurrent.TimeUnit

class ThemeInfoGuavaCache: ThemePackDatabase {

    val cache: Cache<String, Pair<ThemePack, ByteArray>> =
            CacheBuilder.newBuilder()
                    .expireAfterAccess(5, TimeUnit.MINUTES)
                    .maximumSize(20)
                    .build()

    override fun addThemePack(appId: String, checksum: ByteArray, themePack: ThemePack) {
        cache.put(appId, Pair(themePack, checksum))
    }

    override fun getThemePack(appId: String) = cache.getIfPresent(appId)

    override fun removeThemePack(appId: String) {
        cache.invalidate(appId)
    }

}
