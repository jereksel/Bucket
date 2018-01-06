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

package com.jereksel.libresubstratum.domain.overlayService.oreo

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import com.jereksel.libresubstratum.domain.OverlayInfo

object OreoOverlayReader {

    fun read(output: String): Multimap<String, OverlayInfo> {

        val map = ArrayListMultimap.create<String, OverlayInfo>()

        var currentApp = ""

        output.lineSequence()
                .filter { it.isNotEmpty() }
                .forEach { line ->
                    if(line.startsWith("[")) {
                        val enabled = line[1].equals('x', ignoreCase = true)
                        val name = line.drop(3).trim()
                        map.put(currentApp, OverlayInfo(name, currentApp, enabled))
                    } else {
                        currentApp = line
                    }
                }

        return map

    }

}