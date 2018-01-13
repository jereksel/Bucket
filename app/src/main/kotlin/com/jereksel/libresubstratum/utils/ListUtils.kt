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

package com.jereksel.libresubstratum.utils

object ListUtils {

    fun <T> List<T>.replace(filter: (T) -> Boolean, map: (T) -> (T)): List<T> {
        val i = indexOfFirst(filter)
        return replace(i, map)
    }

    fun <T> List<T>.replace(i: Int, map: (T) -> (T)): List<T> {

        val before = take(i)
        val el = get(i)
        val after = drop(i+1)

        return before + map(el) + after
    }

}